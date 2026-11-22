package br.com.lavicameta.integration.payment;

public class NotificacaoInvalidaException extends RuntimeException {

    public NotificacaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
