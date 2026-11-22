package br.com.lavicameta.dto;

import br.com.lavicameta.entity.EtapaCiclo;
import br.com.lavicameta.entity.MetodoPagamento;
import br.com.lavicameta.entity.StatusCiclo;
import br.com.lavicameta.entity.StatusPagamento;
import br.com.lavicameta.entity.StatusPedido;
import br.com.lavicameta.entity.TipoServico;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** Contratos de pedidos, pagamentos, ciclos e carteira. */
public final class PedidoDtos {

    private PedidoDtos() {
    }

    public record CriarPedidoRequest(
            @NotNull(message = "Escolha a máquina.") Integer maquinaId,
            @NotNull(message = "Escolha o serviço.") TipoServico servico) {
    }

    public record CheckoutRequest(@NotNull(message = "Escolha a forma de pagamento.") MetodoPagamento metodo) {
    }

    public record PagamentoResponse(String id, MetodoPagamento metodo, StatusPagamento status, BigDecimal valor,
                                    String codigoPix, String checkoutUrl, String motivo, Instant pagoEm,
                                    Instant criadoEm) {
    }

    public record CicloResponse(String id, EtapaCiclo etapa, StatusCiclo status, Integer maquinaId,
                                String maquinaNome, Instant inicioEm, Instant fimPrevistoEm, Instant fimEm,
                                int minutosRestantes) {
    }

    public record PedidoResponse(String id, StatusPedido status, TipoServico servico, String servicoDescricao,
                                 BigDecimal valorTotal, Integer maquinaId, String maquinaNome, Instant criadoEm,
                                 Instant expiraEm, long segundosParaExpirar, PagamentoResponse pagamento,
                                 List<CicloResponse> ciclos, boolean podeIniciarSecagem) {
    }

    public record PaginaResponse<T>(List<T> itens, int pagina, int tamanho, long total, boolean temProxima) {
    }

    public record MetodoSalvoResponse(String id, String bandeira, String ultimos4, Instant criadoEm) {
    }

    public record CarteiraResponse(List<PagamentoResponse> pagamentos, List<MetodoSalvoResponse> metodosSalvos) {
    }
}
