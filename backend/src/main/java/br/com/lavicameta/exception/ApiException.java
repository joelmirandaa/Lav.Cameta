package br.com.lavicameta.exception;

import org.springframework.http.HttpStatus;

/** Erro de domínio/aplicação com status HTTP e código estável para o cliente. */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String codigo;

    public ApiException(HttpStatus status, String codigo, String mensagem) {
        super(mensagem);
        this.status = status;
        this.codigo = codigo;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCodigo() {
        return codigo;
    }

    public static ApiException naoEncontrado(String mensagem) {
        return new ApiException(HttpStatus.NOT_FOUND, "NAO_ENCONTRADO", mensagem);
    }

    public static ApiException conflito(String codigo, String mensagem) {
        return new ApiException(HttpStatus.CONFLICT, codigo, mensagem);
    }

    public static ApiException invalido(String codigo, String mensagem) {
        return new ApiException(HttpStatus.UNPROCESSABLE_CONTENT, codigo, mensagem);
    }

    public static ApiException naoAutorizado(String codigo, String mensagem) {
        return new ApiException(HttpStatus.UNAUTHORIZED, codigo, mensagem);
    }

    public static ApiException proibido(String mensagem) {
        return new ApiException(HttpStatus.FORBIDDEN, "ACESSO_NEGADO", mensagem);
    }

    public static ApiException indisponivel(String codigo, String mensagem) {
        return new ApiException(HttpStatus.SERVICE_UNAVAILABLE, codigo, mensagem);
    }
}
