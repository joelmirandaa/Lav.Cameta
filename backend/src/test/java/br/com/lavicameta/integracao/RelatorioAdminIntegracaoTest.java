package br.com.lavicameta.integracao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

class RelatorioAdminIntegracaoTest extends IntegracaoBase {

    @Test
    void relatoriosMensalEAnualCt20aCt23() {
        String admin = tokenAdmin();
        JsonNode antes = get("/admin/reports?periodo=MES", admin).corpo();
        BigDecimal ganhosAntes = antes.get("ganhos").decimalValue();

        String token = novoCliente();
        int lavadora = novaMaquina("LAVADORA");
        String pedidoId = criarPedido(token, lavadora, "LAVAR");
        String tx = transacao(checkout(token, pedidoId, "PIX"));
        post("/sandbox/checkout/" + tx + "?resultado=APROVADO", null, null);

        JsonNode mes = get("/admin/reports?periodo=MES", admin).corpo();
        assertEquals(0, ganhosAntes.add(new BigDecimal("12")).compareTo(mes.get("ganhos").decimalValue()));
        assertEquals(0, mes.get("ganhos").decimalValue().compareTo(mes.get("totalPagamentosConfirmados").decimalValue()));
        assertTrue(mes.get("serie").size() >= 4);
        assertNotNull(mes.get("atualizadoEm").asString());

        JsonNode ano = get("/admin/reports?periodo=ANO", admin).corpo();
        assertEquals(12, ano.get("serie").size());
        assertEquals("Jan", ano.get("serie").get(0).get("rotulo").asString());
        assertEquals(400, get("/admin/reports?periodo=SEMANA", admin).status());
        assertEquals(422, get("/admin/reports?periodo=MES&mes=13", admin).status());
    }

    @Test
    void gestaoDeMaquinasEPrecosUc15() {
        String admin = tokenAdmin();
        String nome = "Lavadora " + UUID.randomUUID().toString().substring(0, 6);
        Resposta criada = post("/admin/machines", Map.of("nome", nome, "tipo", "LAVADORA", "capacidadeKg", 12.5,
                "recursos", "Ciclo rápido, Economia de água"), admin);
        assertEquals(201, criada.status());
        assertEquals(409, post("/admin/machines", Map.of("nome", nome, "tipo", "LAVADORA", "capacidadeKg", 10), admin).status());
        assertEquals(400, post("/admin/machines", Map.of("nome", "", "tipo", "LAVADORA", "capacidadeKg", -1), admin).status());
        int id = criada.corpo().get("id").asInt();
        assertEquals(200, chamar("PUT", "/admin/machines/" + id, Map.of("nome", nome + " B", "tipo", "LAVADORA",
                "capacidadeKg", 11), admin).status());
        assertEquals(422, chamar("PATCH", "/admin/machines/" + id + "/status", Map.of("status", "EM_USO"), admin).status());

        JsonNode servicos = get("/admin/services", admin).corpo();
        JsonNode lavar = servicos.get(0);
        Resposta alterado = chamar("PUT", "/admin/services/" + lavar.get("id").asInt(),
                Map.of("descricao", lavar.get("descricao").asString(), "preco", "13.50", "duracaoMin", 40), admin);
        assertEquals(200, alterado.status());
        assertEquals(0, new BigDecimal("13.50").compareTo(alterado.corpo().get("preco").decimalValue()));
        chamar("PUT", "/admin/services/" + lavar.get("id").asInt(),
                Map.of("descricao", lavar.get("descricao").asString(), "preco", "12.00", "duracaoMin", 40), admin);
        assertEquals(400, chamar("PUT", "/admin/services/" + lavar.get("id").asInt(),
                Map.of("descricao", "x", "preco", "0", "duracaoMin", 40), admin).status());
    }
}
