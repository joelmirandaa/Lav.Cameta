package br.com.lavicameta.util;

import java.util.Locale;

/** Normalização de e-mail e telefone para busca e unicidade. */
public final class Contatos {

    private Contatos() {
    }

    public static String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    /** Mantém apenas dígitos (e o "+" inicial, se houver). */
    public static String normalizarTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return null;
        }
        String limpo = telefone.trim();
        String digitos = limpo.replaceAll("[^0-9]", "");
        return limpo.startsWith("+") ? "+" + digitos : digitos;
    }

    public static boolean pareceEmail(String identificador) {
        return identificador != null && identificador.contains("@");
    }
}
