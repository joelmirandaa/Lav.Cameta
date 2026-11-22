package br.com.lavicameta.service;

import br.com.lavicameta.config.LavicametaProperties;
import br.com.lavicameta.dto.PedidoDtos.CriarPedidoRequest;
import br.com.lavicameta.dto.PedidoDtos.PedidoResponse;
import br.com.lavicameta.entity.Maquina;
import br.com.lavicameta.entity.Pagamento;
import br.com.lavicameta.entity.Pedido;
import br.com.lavicameta.entity.Servico;
import br.com.lavicameta.entity.StatusMaquina;
import br.com.lavicameta.entity.StatusPagamento;
import br.com.lavicameta.entity.TipoMaquina;
import br.com.lavicameta.entity.Usuario;
import br.com.lavicameta.exception.ApiException;
import br.com.lavicameta.repository.MaquinaRepository;
import br.com.lavicameta.repository.PagamentoRepository;
import br.com.lavicameta.repository.PedidoRepository;
import br.com.lavicameta.repository.ServicoRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Criação, cancelamento e expiração de pedidos com reserva de 5 minutos (RF08, RN02, RN03, RN05, RN07). */
@Service
public class PedidoService {

    private final PedidoRepository pedidos;
    private final MaquinaRepository maquinas;
    private final ServicoRepository servicos;
    private final PagamentoRepository pagamentos;
    private final UsuarioService usuarioService;
    private final PedidoConsultaService consulta;
    private final Clock clock;
    private final Duration reserva;

    public PedidoService(PedidoRepository pedidos, MaquinaRepository maquinas, ServicoRepository servicos,
                         PagamentoRepository pagamentos, UsuarioService usuarioService,
                         PedidoConsultaService consulta, Clock clock, LavicametaProperties propriedades) {
        this.pedidos = pedidos;
        this.maquinas = maquinas;
        this.servicos = servicos;
        this.pagamentos = pagamentos;
        this.usuarioService = usuarioService;
        this.consulta = consulta;
        this.clock = clock;
        this.reserva = Duration.ofMinutes(propriedades.pedidos().reservaMinutos());
    }

    @Transactional
    public PedidoResponse criar(String usuarioId, CriarPedidoRequest req) {
        Usuario usuario = usuarioService.buscar(usuarioId);
        Servico servico = servicos.findByNome(req.servico())
                .orElseThrow(() -> ApiException.naoEncontrado("Serviço não encontrado."));
        // Trava pessimista: dois clientes na mesma máquina são serializados; só o primeiro reserva (CT16).
        Maquina maquina = maquinas.findByIdForUpdate(req.maquinaId())
                .orElseThrow(() -> ApiException.naoEncontrado("Máquina não encontrada."));
        Instant agora = clock.instant();
        validarContratacao(maquina, servico, agora);
        Pedido pedido = pedidos.save(new Pedido(usuario, maquina, servico, agora, agora.plus(reserva)));
        return consulta.converter(pedido);
    }

    private void validarContratacao(Maquina maquina, Servico servico, Instant agora) {
        if (maquina.getStatus() == StatusMaquina.MANUTENCAO) {
            throw ApiException.conflito("MAQUINA_EM_MANUTENCAO", "Esta máquina está em manutenção. Escolha outra.");
        }
        if (maquina.getStatus() == StatusMaquina.EM_USO || pedidos.existsReservaVigente(maquina.getId(), agora)) {
            throw ApiException.conflito("MAQUINA_INDISPONIVEL", "Esta máquina não está disponível. Escolha outra.");
        }
        if (servico.getNome().tipoMaquinaInicial() != maquina.getTipo()) {
            throw ApiException.invalido("SERVICO_INCOMPATIVEL", "Este serviço não está disponível nesta máquina.");
        }
        if (servico.getNome().incluiSecagemPosterior()
                && maquinas.findByTipoOrderByNomeAsc(TipoMaquina.SECADORA).stream()
                        .noneMatch(m -> m.getStatus() != StatusMaquina.MANUTENCAO)) {
            throw ApiException.conflito("SEM_SECADORA", "Nenhuma secadora em operação no momento para o combo.");
        }
    }

    /** Cliente desiste antes de pagar ("voltar" na tela de pagamento): libera a máquina na hora. */
    @Transactional
    public PedidoResponse cancelar(String usuarioId, String pedidoId) {
        consulta.buscarDoUsuario(usuarioId, pedidoId);
        Pedido pedido = pedidos.findByIdForUpdate(pedidoId)
                .orElseThrow(() -> ApiException.naoEncontrado("Pedido não encontrado."));
        if (!pedido.aguardandoPagamento()) {
            throw ApiException.conflito("PEDIDO_NAO_CANCELAVEL", "Este pedido não pode mais ser cancelado.");
        }
        Instant agora = clock.instant();
        pedido.cancelar(agora);
        encerrarCobrancasPendentes(pedido, agora);
        return consulta.converter(pedido);
    }

    /** RN05: executado pelo agendador para cada pedido vencido, em transação própria. */
    @Transactional
    public void expirarSeVencido(String pedidoId) {
        pedidos.findByIdForUpdate(pedidoId).ifPresent(pedido -> {
            Instant agora = clock.instant();
            if (pedido.aguardandoPagamento() && !pedido.getExpiraEm().isAfter(agora)) {
                pedido.expirar(agora);
                encerrarCobrancasPendentes(pedido, agora);
            }
        });
    }

    private void encerrarCobrancasPendentes(Pedido pedido, Instant agora) {
        for (Pagamento pagamento : pagamentos.findByPedidoAndStatus(pedido, StatusPagamento.PENDENTE)) {
            pagamento.expirar(agora);
        }
    }
}
