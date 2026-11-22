package br.com.lavicameta.dto;

import java.time.Instant;
import java.util.Map;

public record ErroResponse(String codigo, String mensagem, Map<String, String> campos, Instant momento) {

    public static ErroResponse de(String codigo, String mensagem) {
        return new ErroResponse(codigo, mensagem, Map.of(), Instant.now());
    }
}
