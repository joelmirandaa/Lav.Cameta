package br.com.lavicameta.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class RelatorioDtos {

    private RelatorioDtos() {
    }

    public enum Periodo { MES, ANO }

    public record PontoSerie(String rotulo, BigDecimal valor) {
    }

    /**
     * {@code variacaoPercentual} é nula quando o período anterior não tem ganhos (RN10 sem base comparável).
     */
    public record RelatorioResponse(Periodo periodo, int ano, Integer mes, BigDecimal ganhos,
                                    BigDecimal ganhosPeriodoAnterior, BigDecimal variacaoPercentual,
                                    List<PontoSerie> serie, BigDecimal totalPeriodo,
                                    BigDecimal totalPagamentosConfirmados, long quantidadePagamentos,
                                    Instant atualizadoEm) {
    }
}
