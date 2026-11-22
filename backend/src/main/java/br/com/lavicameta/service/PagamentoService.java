package br.com.lavicameta.service;

import br.com.lavicameta.dto.DtoMapper;
import br.com.lavicameta.dto.PedidoDtos.PagamentoResponse;
import br.com.lavicameta.entity.EventoPagamento;
import br.com.lavicameta.entity.MetodoPagamento;
import br.com.lavicameta.entity.Pagamento;
import br.com.lavicameta.entity.Pedido;
import br.com.lavicameta.entity.StatusPagamento;
import br.com.lavicameta.entity.StatusPedido;
import br.com.lavicameta.exception.ApiException;
import br.com.lavicameta.exception.IntegracaoException;
import br.com.lavicameta.integration.payment.NotificacaoPagamento;
import br.com.lavicameta.integration.payment.PaymentGateway;
import br.com.lavicameta.integration.payment.PaymentGateway.Cobranca;
import br.com.lavicameta.repository.EventoPagamentoRepository;
import br.com.lavicameta.repository.PagamentoRepository;
import br.com.lavicameta.repository.PedidoRepository;
import java.time.Clock;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Checkout e confirmação de pagamentos (RF10–RF12, RN04, RN13). A confirmação só vem de
 * notificação autenticada do gateway; o retorno visual do checkout nunca aprova nada.
 */
@Service
public class PagamentoService {

    private static final Logger LOG = LoggerFactory.getLogger(PagamentoService.class);

    private final PedidoRepository pedidos;
    private final PagamentoRepository pagamentos;
    private final EventoPagamentoRepository eventos;
    private final PedidoConsultaService consulta;
    private final PaymentGateway gateway;
    private final ApplicationEventPublisher publicador;
    private final Clock clock;

    public PagamentoService(PedidoRepository pedidos, PagamentoRepository pagamentos, EventoPagamentoRepository eventos,
                            PedidoConsultaService consulta, PaymentGateway gateway,
                            ApplicationEventPublisher publicador, Clock clock) {
        this.pedidos = pedidos;
        this.pagamentos = pagamentos;
        this.eventos = eventos;
        this.consulta = consulta;
        this.gateway = gateway;
        this.publicador = publicador;
        this.clock = clock;
    }

    public String provedorAtivo() {
        return gateway.provedor();
    }

    @Transactional(readOnly = true)
    public PagamentoResponse consultarTransacao(String transacaoId) {
        return pagamentos.findByProvedorAndTransacaoId(gateway.provedor(), transacaoId)
                .map(DtoMapper::pagamento)
                .orElseThrow(() -> ApiException.naoEncontrado("Transação não encontrada."));
    }

    /**
     * Gera (ou reaproveita) a cobrança do pedido. Trocar de método encerra a cobrança pendente anterior
     * (UC07: "possibilidade de tentar outro método").
     */
    @Transactional
    public PagamentoResponse checkout(String usuarioId, String pedidoId, MetodoPagamento metodo) {
        consulta.buscarDoUsuario(usuarioId, pedidoId);
        Pedido pedido = pedidos.findByIdForUpdate(pedidoId)
                .orElseThrow(() -> ApiException.naoEncontrado("Pedido não encontrado."));
        Instant agora = clock.instant();
        if (!pedido.reservaVigente(agora)) {
            throw ApiException.conflito("PEDIDO_EXPIRADO", "O prazo para pagamento terminou. Faça um novo pedido.");
        }
        for (Pagamento pendente : pagamentos.findByPedidoAndStatus(pedido, StatusPagamento.PENDENTE)) {
            if (pendente.getMetodo() == metodo) {
                return DtoMapper.pagamento(pendente);
            }
            pendente.expirar(agora);
        }
        Pagamento pagamento = new Pagamento(pedido, metodo, gateway.provedor(), agora);
        Cobranca cobranca;
        try {
            cobranca = gateway.criarCobranca(pagamento);
        } catch (IntegracaoException ex) {
            LOG.warn("Falha ao criar cobrança no gateway {}: {}", gateway.provedor(), ex.getMessage());
            throw ApiException.indisponivel("GATEWAY_INDISPONIVEL",
                    "Pagamento indisponível no momento. Tente novamente em instantes.");
        }
        pagamento.registrarCobranca(cobranca.transacaoId(), cobranca.codigoPix(), cobranca.checkoutUrl(), agora);
        return DtoMapper.pagamento(pagamentos.save(pagamento));
    }

    /**
     * Aplica uma notificação já autenticada pelo adaptador. Idempotente por (provedor, eventoId)
     * e por estado do pagamento: reentregas nunca aprovam duas vezes nem reiniciam a máquina.
     */
    @Transactional
    public void aplicarNotificacao(String provedor, NotificacaoPagamento notificacao) {
        if (eventos.existsByProvedorAndEventoId(provedor, notificacao.eventoId())) {
            LOG.info("Evento {} de {} já processado; ignorado.", notificacao.eventoId(), provedor);
            return;
        }
        Instant agora = clock.instant();
        eventos.saveAndFlush(new EventoPagamento(provedor, notificacao.eventoId(), notificacao.transacaoId(),
                notificacao.resultado().name(), agora));

        Pagamento pagamento = pagamentos.findByProvedorAndTransacaoId(provedor, notificacao.transacaoId())
                .orElseThrow(() -> ApiException.naoEncontrado("Transação desconhecida."));
        if (pagamento.getValor().compareTo(notificacao.valor()) != 0) {
            LOG.error("Valor divergente na transação {}: esperado {}, recebido {}", notificacao.transacaoId(),
                    pagamento.getValor(), notificacao.valor());
            throw ApiException.invalido("VALOR_DIVERGENTE", "Valor da notificação diverge da cobrança.");
        }
        Pedido pedido = pedidos.findByIdForUpdate(pagamento.getPedido().getId()).orElseThrow();

        switch (notificacao.resultado()) {
            case APROVADO -> aprovar(pagamento, pedido, notificacao, agora);
            case RECUSADO -> recusar(pagamento, notificacao, agora);
        }
    }

    private void aprovar(Pagamento pagamento, Pedido pedido, NotificacaoPagamento notificacao, Instant agora) {
        if (pagamento.getStatus() == StatusPagamento.APROVADO || pagamento.getStatus() == StatusPagamento.ESTORNADO
                || pagamento.getStatus() == StatusPagamento.ESTORNO_PENDENTE) {
            return;
        }
        Instant pagoEm = notificacao.ocorridoEm() == null ? agora : notificacao.ocorridoEm();
        boolean dentroDoPrazo = pedido.getStatus() == StatusPedido.AGUARDANDO_PAGAMENTO
                && !pagoEm.isAfter(pedido.getExpiraEm());
        boolean jaPossuiAprovado = pagamentos.findFirstByPedidoAndStatusIn(pedido,
                java.util.EnumSet.of(StatusPagamento.APROVADO)).isPresent();

        pagamento.aprovar(pagoEm, agora);
        if (dentroDoPrazo && !jaPossuiAprovado) {
            pedido.confirmarPagamento(agora);
            pagamentos.findByPedidoAndStatus(pedido, StatusPagamento.PENDENTE).forEach(p -> p.expirar(agora));
            publicador.publishEvent(new EventosPedido.PagamentoAprovado(pedido.getId()));
            return;
        }
        // Pagamento após expiração/cancelamento ou duplicado: devolve integralmente.
        LOG.warn("Pagamento {} recebido fora do prazo ou em duplicidade; estorno solicitado.", pagamento.getId());
        pagamento.solicitarEstorno("Pagamento recebido após o encerramento do pedido", agora);
        publicador.publishEvent(new EventosPedido.EstornoSolicitado(pagamento.getId()));
    }

    private void recusar(Pagamento pagamento, NotificacaoPagamento notificacao, Instant agora) {
        if (!pagamento.pendente()) {
            return;
        }
        String motivo = notificacao.motivo() == null || notificacao.motivo().isBlank()
                ? "Pagamento recusado" : notificacao.motivo();
        pagamento.recusar(motivo.length() > 255 ? motivo.substring(0, 255) : motivo, agora);
    }
}
