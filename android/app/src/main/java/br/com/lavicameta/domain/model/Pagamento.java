package br.com.lavicameta.domain.model;

import java.math.BigDecimal;

public final class Pagamento {
    public static final String PIX = "PIX";
    public static final String PENDENTE = "PENDENTE";
    public static final String RECUSADO = "RECUSADO";

    public String id;
    public String metodo;
    public String status;
    public BigDecimal valor;
    public String codigoPix;
    public String checkoutUrl;
    public String motivo;
    public String pagoEm;
    public String criadoEm;

    public boolean isPix() {
        return PIX.equals(metodo);
    }
}
