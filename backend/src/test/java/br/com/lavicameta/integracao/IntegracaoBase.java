package br.com.lavicameta.integracao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Clock;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.mysql.MySQLContainer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** Sobe a API completa sobre um MySQL 8.4 real (Testcontainers), com gateway sandbox e máquina simulada. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(IntegracaoBase.Config.class)
public abstract class IntegracaoBase {

    @ServiceConnection
    static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.4");

    static {
        MYSQL.start();
    }

    @TestConfiguration
    static class Config {
        @Bean
        @Primary
        RelogioAjustavel relogio() {
            return new RelogioAjustavel();
        }
    }

    @LocalServerPort
    protected int porta;

    @Autowired
    protected JsonMapper json;

    @Autowired
    protected RelogioAjustavel relogio;

    @Autowired
    protected Clock clock;

    @MockitoBean
    protected JavaMailSender mailSender;

    private final HttpClient http = HttpClient.newHttpClient();

    @BeforeEach
    void sincronizarRelogio() {
        relogio.sincronizar();
    }

    protected record Resposta(int status, JsonNode corpo, String texto) {
    }

    protected Resposta chamar(String metodo, String caminho, Object corpo, String token) {
        return chamarComCabecalhos(metodo, caminho, corpo == null ? null : json.writeValueAsString(corpo), token, Map.of());
    }

    protected Resposta chamarComCabecalhos(String metodo, String caminho, String corpo, String token,
                                           Map<String, String> cabecalhos) {
        try {
            HttpRequest.Builder req = HttpRequest.newBuilder(URI.create("http://localhost:" + porta + "/api/v1" + caminho))
                    .method(metodo, corpo == null ? HttpRequest.BodyPublishers.noBody()
                            : HttpRequest.BodyPublishers.ofString(corpo));
            if (corpo != null) {
                req.header("Content-Type", "application/json");
            }
            if (token != null) {
                req.header("Authorization", "Bearer " + token);
            }
            cabecalhos.forEach(req::header);
            HttpResponse<String> resp = http.send(req.build(), HttpResponse.BodyHandlers.ofString());
            String texto = resp.body();
            JsonNode no = texto != null && (texto.startsWith("{") || texto.startsWith("[")) ? json.readTree(texto) : null;
            return new Resposta(resp.statusCode(), no, texto);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    protected Resposta post(String caminho, Object corpo, String token) {
        return chamar("POST", caminho, corpo, token);
    }

    protected Resposta get(String caminho, String token) {
        return chamar("GET", caminho, null, token);
    }

    protected String tokenAdmin() {
        Resposta r = post("/admin/auth/login", Map.of("identificador", "admin@teste.local", "senha", "AdminTeste2026"), null);
        assertEquals(200, r.status(), r.texto());
        return r.corpo().get("token").asString();
    }

    protected String novoCliente() {
        String email = "cliente-" + UUID.randomUUID() + "@teste.local";
        Resposta r = post("/auth/register", Map.of("nome", "Cliente Teste", "email", email, "senha", "senha1234"), null);
        assertEquals(201, r.status(), r.texto());
        return r.corpo().get("token").asString();
    }

    protected int novaMaquina(String tipo) {
        Resposta r = post("/admin/machines", Map.of("nome", tipo + "-" + UUID.randomUUID().toString().substring(0, 8),
                "tipo", tipo, "capacidadeKg", 10), tokenAdmin());
        assertEquals(201, r.status(), r.texto());
        return r.corpo().get("id").asInt();
    }

    protected String criarPedido(String token, int maquinaId, String servico) {
        Resposta r = post("/orders", Map.of("maquinaId", maquinaId, "servico", servico), token);
        assertEquals(201, r.status(), r.texto());
        return r.corpo().get("id").asString();
    }

    protected JsonNode checkout(String token, String pedidoId, String metodo) {
        Resposta r = post("/orders/" + pedidoId + "/checkout", Map.of("metodo", metodo), token);
        assertEquals(200, r.status(), r.texto());
        return r.corpo();
    }

    protected static String transacao(JsonNode pagamento) {
        String url = pagamento.get("checkoutUrl").asString();
        return url.substring(url.lastIndexOf('/') + 1);
    }
}
