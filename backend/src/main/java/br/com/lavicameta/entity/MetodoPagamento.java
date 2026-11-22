package br.com.lavicameta.entity;

public enum MetodoPagamento {
    PIX,
    CARTAO_CREDITO,
    CARTAO_DEBITO;

    public boolean isCartao() {
        return this != PIX;
    }
}
