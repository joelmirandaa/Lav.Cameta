package br.com.lavicameta.util;

import java.util.Collections;
import java.util.Map;

/** Estado de uma operação assíncrona exibido pela UI: carregando, sucesso ou erro. */
public final class Result<T> {

    public enum Status { LOADING, SUCCESS, ERROR }

    public final Status status;
    public final T data;
    public final String message;
    public final String code;
    public final Map<String, String> fieldErrors;

    private Result(Status status, T data, String message, String code, Map<String, String> fieldErrors) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.code = code;
        this.fieldErrors = fieldErrors == null ? Collections.emptyMap() : fieldErrors;
    }

    public static <T> Result<T> loading() {
        return new Result<>(Status.LOADING, null, null, null, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(Status.SUCCESS, data, null, null, null);
    }

    public static <T> Result<T> error(String message, String code, Map<String, String> fieldErrors) {
        return new Result<>(Status.ERROR, null, message, code, fieldErrors);
    }

    public boolean isLoading() {
        return status == Status.LOADING;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public boolean isError() {
        return status == Status.ERROR;
    }
}
