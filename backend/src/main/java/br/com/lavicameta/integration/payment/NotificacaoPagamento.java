package br.com.lavicameta.integration.payment;

import java.math.BigDecimal;
import java.time.Instant;

/** Notificação autenticada do gateway, já convertida para o domínio. */
public record NotificacaoPagamento(String eventoId, String transacaoId, Resultado resultado, BigDecimal valor,
                                   Instant ocorridoEm, String motivo) {

    public enum Resultado { APROVADO, RECUSADO }
}
