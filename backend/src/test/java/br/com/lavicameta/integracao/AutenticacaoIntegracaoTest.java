package br.com.lavicameta.integracao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;

class AutenticacaoIntegracaoTest extends IntegracaoBase {

    @Test
    void cadastroLoginEValidacoes() {
        String email = "ana-" + UUID.randomUUID() + "@teste.local";
        Resposta cadastro = post("/auth/register",
                Map.of("nome", "Ana", "email", email, "telefone", "(91) 98888-" + (int) (Math.random() * 9000 + 1000),
                        "senha", "senha1234"), null);
        assertEquals(201, cadastro.status(), cadastro.texto());
        assertEquals("CLIENTE", cadastro.corpo().get("usuario").get("tipo").asString());

        assertEquals(409, post("/auth/register", Map.of("nome", "Ana", "email", email, "senha", "senha1234"), null).status());
        Resposta invalido = post("/auth/register", Map.of("nome", "", "senha", "curta"), null);
        assertEquals(400, invalido.status());
        assertTrue(invalido.corpo().get("campos").has("senha"));

        // CT01 / CT02
        assertEquals(200, post("/auth/login", Map.of("identificador", email.toUpperCase(), "senha", "senha1234"), null).status());
        Resposta errado = post("/auth/login", Map.of("identificador", email, "senha", "outra1234"), null);
        assertEquals(401, errado.status());
        assertEquals("CREDENCIAIS_INVALIDAS", errado.corpo().get("codigo").asString());
    }

    @Test
    void bloqueioAposCincoTentativasCt03() {
        String email = "bloq-" + UUID.randomUUID() + "@teste.local";
        post("/auth/register", Map.of("nome", "Bia", "email", email, "senha", "senha1234"), null);
        for (int i = 0; i < 4; i++) {
            assertEquals(401, post("/auth/login", Map.of("identificador", email, "senha", "errada123"), null).status());
        }
        assertEquals(423, post("/auth/login", Map.of("identificador", email, "senha", "errada123"), null).status());
        assertEquals(423, post("/auth/login", Map.of("identificador", email, "senha", "senha1234"), null).status());
        relogio.avancar(Duration.ofMinutes(16));
        assertEquals(200, post("/auth/login", Map.of("identificador", email, "senha", "senha1234"), null).status());
    }

    @Test
    void separacaoClienteAdministradorCt18Ct19() {
        String cliente = novoCliente();
        assertEquals(403, get("/admin/reports", cliente).status());
        assertEquals(401, get("/admin/reports", null).status());
        assertEquals(401, get("/machines", "token-invalido").status());
        String admin = tokenAdmin();
        assertEquals(200, get("/admin/reports", admin).status());
        assertEquals(403, post("/auth/login", Map.of("identificador", "admin@teste.local", "senha", "AdminTeste2026"), null).status());
        assertEquals(403, get("/orders", admin).status());
    }

    @Test
    void recuperacaoDeSenhaCt05() {
        String email = "rec-" + UUID.randomUUID() + "@teste.local";
        post("/auth/register", Map.of("nome", "Caio", "email", email, "senha", "senha1234"), null);
        assertEquals(202, post("/auth/password-recovery", Map.of("email", email), null).status());
        assertEquals(202, post("/auth/password-recovery", Map.of("email", "inexistente@teste.local"), null).status());

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, org.mockito.Mockito.atLeastOnce()).send(captor.capture());
        SimpleMailMessage enviada = captor.getAllValues().stream()
                .filter(m -> m.getTo()[0].equals(email)).findFirst().orElseThrow();
        Matcher m = Pattern.compile("\\n\\s+([A-Z0-9]{8})\\n").matcher(enviada.getText());
        assertTrue(m.find());
        String codigo = m.group(1);

        assertEquals(422, post("/auth/password-reset", Map.of("email", email, "codigo", "AAAAAAAA", "novaSenha", "nova12345"), null).status());
        assertEquals(200, post("/auth/password-reset", Map.of("email", email, "codigo", codigo.toLowerCase(), "novaSenha", "nova12345"), null).status());
        assertEquals(422, post("/auth/password-reset", Map.of("email", email, "codigo", codigo, "novaSenha", "outra12345"), null).status());
        assertEquals(200, post("/auth/login", Map.of("identificador", email, "senha", "nova12345"), null).status());
        verify(mailSender, org.mockito.Mockito.atLeastOnce()).send(any(SimpleMailMessage.class));
    }

    @Test
    void perfilEdicaoRf16() {
        String token = novoCliente();
        String novoEmail = "perfil-" + UUID.randomUUID() + "@teste.local";
        Resposta r = chamar("PUT", "/me", Map.of("nome", "Nome Novo", "email", novoEmail), token);
        assertEquals(200, r.status(), r.texto());
        assertEquals("Nome Novo", get("/me", token).corpo().get("nome").asString());
        assertEquals(422, chamar("PUT", "/me/password", Map.of("senhaAtual", "errada", "novaSenha", "nova12345"), token).status());
        assertEquals(204, chamar("PUT", "/me/password", Map.of("senhaAtual", "senha1234", "novaSenha", "nova12345"), token).status());
        assertEquals(200, post("/auth/login", Map.of("identificador", novoEmail, "senha", "nova12345"), null).status());
    }
}
