package br.com.lavicameta.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Formatação pt-BR: moeda em BRL e datas no fuso da lavanderia (Cametá/PA). */
public final class Formatters {

    public static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    public static final ZoneId ZONA = ZoneId.of("America/Belem");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm", PT_BR);
    private static final DateTimeFormatter DATA_HORA_CURTA = DateTimeFormatter.ofPattern("dd/MM HH:mm", PT_BR);
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm", PT_BR);

    private Formatters() {
    }

    public static String moeda(BigDecimal valor) {
        NumberFormat formato = NumberFormat.getCurrencyInstance(PT_BR);
        return formato.format(valor == null ? BigDecimal.ZERO : valor).replace(' ', ' ');
    }

    public static String percentual(BigDecimal valor) {
        if (valor == null) {
            return null;
        }
        NumberFormat formato = NumberFormat.getNumberInstance(PT_BR);
        formato.setMinimumFractionDigits(1);
        formato.setMaximumFractionDigits(1);
        String texto = formato.format(valor.abs());
        return (valor.signum() > 0 ? "+" : valor.signum() < 0 ? "−" : "") + texto + "%";
    }

    public static String capacidade(BigDecimal kg) {
        NumberFormat formato = NumberFormat.getNumberInstance(PT_BR);
        formato.setMaximumFractionDigits(1);
        return formato.format(kg == null ? BigDecimal.ZERO : kg) + " kg";
    }

    public static String dataHora(String iso) {
        return formatar(iso, DATA_HORA);
    }

    public static String dataHoraCurta(String iso) {
        return formatar(iso, DATA_HORA_CURTA);
    }

    public static String hora(String iso) {
        return formatar(iso, HORA);
    }

    public static String contagem(long segundos) {
        long s = Math.max(0, segundos);
        return String.format(PT_BR, "%d:%02d", s / 60, s % 60);
    }

    private static String formatar(String iso, DateTimeFormatter formato) {
        if (iso == null || iso.isEmpty()) {
            return "";
        }
        try {
            return formato.format(Instant.parse(iso).atZone(ZONA));
        } catch (RuntimeException ex) {
            return "";
        }
    }
}
