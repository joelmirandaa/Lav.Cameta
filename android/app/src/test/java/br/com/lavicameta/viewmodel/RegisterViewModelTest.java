package br.com.lavicameta.viewmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import br.com.lavicameta.domain.usecase.FormValidator.Erro;
import java.util.Map;
import org.junit.Test;

public class RegisterViewModelTest {

    @Test
    public void exigeNomeContatoESenhasIguais() {
        Map<String, Erro> erros = RegisterViewModel.validar("", "", "", "senha123", "senha124");
        assertEquals(Erro.OBRIGATORIO, erros.get("nome"));
        assertEquals(Erro.CONTATO_OBRIGATORIO, erros.get("email"));
        assertEquals(Erro.SENHAS_DIFERENTES, erros.get("confirmacao"));
    }

    @Test
    public void cadastroValidoSomenteComTelefone() {
        assertTrue(RegisterViewModel.validar("Ana", "", "91988887777", "senha123", "senha123").isEmpty());
    }
}
