package br.com.lavicameta.domain.usecase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import br.com.lavicameta.domain.usecase.FormValidator.Erro;
import java.math.BigDecimal;
import org.junit.Test;

public class FormValidatorTest {

    @Test
    public void senhaExigeLetrasENumerosEOitoCaracteres() {
        assertEquals(Erro.OBRIGATORIO, FormValidator.senha(""));
        assertEquals(Erro.SENHA_FRACA, FormValidator.senha("abcdefgh"));
        assertEquals(Erro.SENHA_FRACA, FormValidator.senha("1234567"));
        assertNull(FormValidator.senha("senha123"));
    }

    @Test
    public void emailETelefoneOpcionaisMasValidos() {
        assertNull(FormValidator.emailOpcional(""));
        assertNull(FormValidator.emailOpcional("ana@exemplo.com"));
        assertEquals(Erro.EMAIL_INVALIDO, FormValidator.emailOpcional("ana@"));
        assertNull(FormValidator.telefoneOpcional("(91) 98888-7777"));
        assertEquals(Erro.TELEFONE_INVALIDO, FormValidator.telefoneOpcional("123"));
        assertEquals(Erro.CONTATO_OBRIGATORIO, FormValidator.contato(" ", null));
    }

    @Test
    public void decimalAceitaVirgula() {
        assertEquals(0, new BigDecimal("12.5").compareTo(FormValidator.decimalPositivo("12,5")));
        assertNull(FormValidator.decimalPositivo("0"));
        assertNull(FormValidator.decimalPositivo("abc"));
        assertNull(FormValidator.inteiroPositivo("-3"));
        assertEquals(Integer.valueOf(40), FormValidator.inteiroPositivo("40"));
    }
}
