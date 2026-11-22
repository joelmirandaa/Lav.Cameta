package br.com.lavicameta.domain.usecase;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Validações de formulário no app, espelhando as regras da API (que valida novamente).
 * Retorna o código de erro do campo ou {@code null} se válido.
 */
public final class FormValidator {

    public enum Erro {
        OBRIGATORIO, EMAIL_INVALIDO, TELEFONE_INVALIDO, SENHA_FRACA, CONTATO_OBRIGATORIO, SENHAS_DIFERENTES,
        NUMERO_INVALIDO, MUITO_LONGO
    }

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern TELEFONE = Pattern.compile("^\\+?[0-9 ()-]{10,20}$");
    private static final Pattern SENHA = Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9]).{8,72}$");

    private FormValidator() {
    }

    public static boolean vazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public static Erro obrigatorio(String valor, int max) {
        if (vazio(valor)) {
            return Erro.OBRIGATORIO;
        }
        return valor.trim().length() > max ? Erro.MUITO_LONGO : null;
    }

    public static Erro emailOpcional(String email) {
        return vazio(email) || EMAIL.matcher(email.trim()).matches() ? null : Erro.EMAIL_INVALIDO;
    }

    public static Erro emailObrigatorio(String email) {
        return vazio(email) ? Erro.OBRIGATORIO : emailOpcional(email);
    }

    public static Erro telefoneOpcional(String telefone) {
        return vazio(telefone) || TELEFONE.matcher(telefone.trim()).matches() ? null : Erro.TELEFONE_INVALIDO;
    }

    public static Erro contato(String email, String telefone) {
        return vazio(email) && vazio(telefone) ? Erro.CONTATO_OBRIGATORIO : null;
    }

    public static Erro senha(String senha) {
        if (vazio(senha)) {
            return Erro.OBRIGATORIO;
        }
        return SENHA.matcher(senha).matches() ? null : Erro.SENHA_FRACA;
    }

    public static Erro confirmacao(String senha, String confirmacao) {
        return senha != null && senha.equals(confirmacao) ? null : Erro.SENHAS_DIFERENTES;
    }

    /** Aceita vírgula ou ponto como separador decimal. */
    public static BigDecimal decimalPositivo(String valor) {
        if (vazio(valor)) {
            return null;
        }
        try {
            BigDecimal numero = new BigDecimal(valor.trim().replace(",", "."));
            return numero.signum() > 0 ? numero : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static Integer inteiroPositivo(String valor) {
        if (vazio(valor)) {
            return null;
        }
        try {
            int numero = Integer.parseInt(valor.trim());
            return numero > 0 ? numero : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
