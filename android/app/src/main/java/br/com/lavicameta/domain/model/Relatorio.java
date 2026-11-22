package br.com.lavicameta.domain.model;

import java.math.BigDecimal;
import java.util.List;

public final class Relatorio {
    public String periodo;
    public int ano;
    public Integer mes;
    public BigDecimal ganhos;
    public BigDecimal ganhosPeriodoAnterior;
    public BigDecimal variacaoPercentual;
    public List<Ponto> serie;
    public BigDecimal totalPeriodo;
    public BigDecimal totalPagamentosConfirmados;
    public long quantidadePagamentos;
    public String atualizadoEm;

    public static final class Ponto {
        public String rotulo;
        public BigDecimal valor;
    }
}
