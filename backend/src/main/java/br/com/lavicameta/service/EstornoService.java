package br.com.lavicameta.service;

import br.com.lavicameta.entity.Pagamento;
import br.com.lavicameta.entity.Pedido;
import br.com.lavicameta.entity.StatusPagamento;
import br.com.lavicameta.entity.StatusPedido;
import br.com.lavicameta.exception.IntegracaoException;
import br.com.lavicameta.integration.payment.PaymentGateway;
import br.com.lavicameta.repository.PagamentoRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Estorno integral (RN06). O pedido passa a ESTORNADO e o pagamento fica ESTORNO_PENDENTE até o
 * gateway confirmar; falhas são reprocessadas pelo agendador, sem simular sucesso.
 */
@Service
public class EstornoService {

    private static final Logger LOG = LoggerFactory.getLogger(EstornoService.class);

    private final PagamentoRepository pagamentos;
    private final PaymentGateway gateway;
    private final ApplicationEventPublisher publicador;
    private final Clock clock;

    public EstornoService(PagamentoRepository pagamentos, PaymentGateway gateway,
                          ApplicationEventPublisher publicador, Clock clock) {
        this.pagamentos = pagamentos;
        this.gateway = gateway;
        this.publicador = publicador;
        this.clock = clock;
    }

    /** Chamado dentro da transação que detectou a falha (pedido já travado). */
    public void solicitar(Pedido pedido, String motivo, Instant agora) {
        pagamentos.findByPedidoAndStatus(pedido, StatusPagamento.APROVADO).forEach(pagamento -> {
            pagamento.solicitarEstorno(motivo, agora);
            publicador.publishEvent(new EventosPedido.EstornoSolicitado(pagamento.getId()));
        });
        if (pedido.getStatus() != StatusPedido.ESTORNADO) {
            pedido.estornar(agora);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processar(String pagamentoId) {
        Pagamento pagamento = pagamentos.findById(pagamentoId).orElse(null);
        if (pagamento == null || pagamento.getStatus() != StatusPagamento.ESTORNO_PENDENTE) {
            return;
        }
        try {
            gateway.estornar(pagamento);
            pagamento.confirmarEstorno(clock.instant());
            LOG.info("Estorno confirmado para o pagamento {}", pagamentoId);
        } catch (IntegracaoException ex) {
            LOG.error("Estorno do pagamento {} falhou; nova tentativa pelo agendador: {}", pagamentoId, ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<String> pendentes() {
        return pagamentos.findByStatus(StatusPagamento.ESTORNO_PENDENTE).stream().map(Pagamento::getId).toList();
    }
}
