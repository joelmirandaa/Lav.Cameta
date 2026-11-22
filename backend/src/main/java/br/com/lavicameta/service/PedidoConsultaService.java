package br.com.lavicameta.service;

import br.com.lavicameta.dto.DtoMapper;
import br.com.lavicameta.dto.PedidoDtos.CicloResponse;
import br.com.lavicameta.dto.PedidoDtos.PaginaResponse;
import br.com.lavicameta.dto.PedidoDtos.PedidoResponse;
import br.com.lavicameta.entity.Ciclo;
import br.com.lavicameta.entity.EtapaCiclo;
import br.com.lavicameta.entity.Pagamento;
import br.com.lavicameta.entity.Pedido;
import br.com.lavicameta.entity.StatusCiclo;
import br.com.lavicameta.entity.StatusPagamento;
import br.com.lavicameta.entity.StatusPedido;
import br.com.lavicameta.entity.Usuario;
import br.com.lavicameta.exception.ApiException;
import br.com.lavicameta.repository.CicloRepository;
import br.com.lavicameta.repository.PagamentoRepository;
import br.com.lavicameta.repository.PedidoRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Leitura de pedidos do cliente: acompanhamento (UC08) e histórico (RF14). */
@Service
public class PedidoConsultaService {

    private static final int TAMANHO_MAXIMO_PAGINA = 50;

    private final PedidoRepository pedidos;
    private final PagamentoRepository pagamentos;
    private final CicloRepository ciclos;
    private final UsuarioService usuarioService;
    private final Clock clock;

    public PedidoConsultaService(PedidoRepository pedidos, PagamentoRepository pagamentos, CicloRepository ciclos,
                                 UsuarioService usuarioService, Clock clock) {
        this.pedidos = pedidos;
        this.pagamentos = pagamentos;
        this.ciclos = ciclos;
        this.usuarioService = usuarioService;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public PedidoResponse obter(String usuarioId, String pedidoId) {
        return converter(buscarDoUsuario(usuarioId, pedidoId));
    }

    @Transactional(readOnly = true)
    public PaginaResponse<PedidoResponse> historico(String usuarioId, int pagina, int tamanho) {
        Usuario usuario = usuarioService.buscar(usuarioId);
        int tamanhoValido = Math.clamp(tamanho, 1, TAMANHO_MAXIMO_PAGINA);
        Page<Pedido> page = pedidos.findByUsuarioOrderByCriadoEmDesc(usuario,
                PageRequest.of(Math.max(pagina, 0), tamanhoValido));
        return new PaginaResponse<>(page.map(this::converter).getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.hasNext());
    }

    Pedido buscarDoUsuario(String usuarioId, String pedidoId) {
        Pedido pedido = pedidos.findDetalhadoById(pedidoId)
                .orElseThrow(() -> ApiException.naoEncontrado("Pedido não encontrado."));
        if (!pedido.getUsuario().getId().equals(usuarioId)) {
            throw ApiException.naoEncontrado("Pedido não encontrado.");
        }
        return pedido;
    }

    PedidoResponse converter(Pedido pedido) {
        Instant agora = clock.instant();
        Pagamento pagamento = pagamentos.findFirstByPedidoAndStatusIn(pedido,
                        EnumSet.of(StatusPagamento.APROVADO, StatusPagamento.ESTORNO_PENDENTE, StatusPagamento.ESTORNADO))
                .orElseGet(() -> pagamentos.findFirstByPedidoOrderByCriadoEmDesc(pedido).orElse(null));
        List<Ciclo> listaCiclos = ciclos.findByPedidoOrderByInicioEmAsc(pedido);
        List<CicloResponse> ciclosDto = listaCiclos.stream().map(c -> DtoMapper.ciclo(c, agora)).toList();
        long segundos = pedido.aguardandoPagamento()
                ? Math.max(0, Duration.between(agora, pedido.getExpiraEm()).getSeconds()) : 0;
        return new PedidoResponse(pedido.getId(), pedido.getStatus(), pedido.getServico().getNome(),
                pedido.getServico().getDescricao(), pedido.getValorTotal(), pedido.getMaquina().getId(),
                pedido.getMaquina().getNome(), pedido.getCriadoEm(), pedido.getExpiraEm(), segundos,
                DtoMapper.pagamento(pagamento), ciclosDto, podeIniciarSecagem(pedido, listaCiclos));
    }

    static boolean podeIniciarSecagem(Pedido pedido, List<Ciclo> listaCiclos) {
        if (!pedido.getServico().getNome().incluiSecagemPosterior() || pedido.getStatus() != StatusPedido.EM_ANDAMENTO) {
            return false;
        }
        boolean lavagemConcluida = listaCiclos.stream()
                .anyMatch(c -> c.getEtapa() == EtapaCiclo.LAVAGEM && c.getStatus() == StatusCiclo.CONCLUIDO);
        boolean secagemCriada = listaCiclos.stream().anyMatch(c -> c.getEtapa() == EtapaCiclo.SECAGEM);
        return lavagemConcluida && !secagemCriada;
    }
}
