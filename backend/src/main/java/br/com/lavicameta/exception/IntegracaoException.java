package br.com.lavicameta.exception;

/** Falha de comunicação com serviço externo (gateway de pagamento ou máquina). */
public class IntegracaoException extends RuntimeException {

    public IntegracaoException(String mensagem) {
        super(mensagem);
    }

    public IntegracaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
