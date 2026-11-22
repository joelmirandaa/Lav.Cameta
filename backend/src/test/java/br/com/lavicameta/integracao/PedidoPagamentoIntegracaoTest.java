package br.com.lavicameta.integracao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.lavicameta.integration.machine.SimulatedMachineController;
import br.com.lavicameta.integration.payment.SandboxPaymentGateway;
import br.com.lavicameta.service.CicloService;
import br.com.lavicameta.service.PedidoService;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.JsonNode;

class PedidoPagamentoIntegracaoTest extends IntegracaoBase {

    @Autowired
    private SandboxPaymentGateway sandbox;
    @Autowired
    private SimulatedMachineController simulador;
    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private CicloService cicloService;

    private Resposta webhook(String transacao, String resultado, String valor, String eventoId, boolean assinar) {
        String corpo = json.writeValueAsString(Map.of("eventoId", eventoId, "transacaoId", transacao,
                "resultado", resultado, "valor", valor));
        String assinatura = assinar ? sandbox.assinar(corpo) : "0000";
        return chamarComCabecalhos("POST", "/payments/webhook/sandbox", corpo, null,
                Map.of("X-Sandbox-Signature", assinatura));
    }

    private JsonNode pedido(String token, String id) {
        return get("/orders/" + id, token).corpo();
    }

    private JsonNode maquina(String token, int id) {
        return get("/machines/" + id, token).corpo().get("maquina");
    }

    @Test
    void catalogoFiltrosPrecosEStatusCt06aCt11() {
        String token = novoCliente();
        int lavadora = novaMaquina("LAVADORA");
        int secadora = novaMaquina("SECADORA");
        for (JsonNode m : get("/machines?filtro=LAVAR", token).corpo()) {
            assertEquals("LAVADORA", m.get("tipo").asString());
        }
        for (JsonNode m : get("/machines?filtro=SECAR", token).corpo()) {
            assertEquals("SECADORA", m.get("tipo").asString());
        }
        JsonNode detalhe = get("/machines/" + lavadora, token).corpo();
        assertEquals(2, detalhe.get("servicos").size());
        assertEquals("12.0", detalhe.get("servicos").get(0).get("preco").asString().replace("12.00", "12.0"));
        assertEquals(0, new java.math.BigDecimal("18").compareTo(detalhe.get("servicos").get(1).get("preco").decimalValue()));
        assertEquals(1, get("/machines/" + secadora, token).corpo().get("servicos").size());

        // Serviço incompatível com o tipo da máquina
        assertEquals(422, post("/orders", Map.of("maquinaId", secadora, "servico", "LAVAR"), token).status());

        // CT09: manutenção
        String admin = tokenAdmin();
        assertEquals(200, chamar("PATCH", "/admin/machines/" + lavadora + "/status", Map.of("status", "MANUTENCAO"), admin).status());
        assertFalse(maquina(token, lavadora).get("contratavel").asBoolean());
        Resposta r = post("/orders", Map.of("maquinaId", lavadora, "servico", "LAVAR"), token);
        assertEquals(409, r.status());
        assertEquals("MAQUINA_EM_MANUTENCAO", r.corpo().get("codigo").asString());
        chamar("PATCH", "/admin/machines/" + lavadora + "/status", Map.of("status", "DISPONIVEL"), admin);
        assertTrue(maquina(token, lavadora).get("contratavel").asBoolean());
    }

    @Test
    void pixAprovadoIniciaMaquinaEReentregaNaoDuplicaCt12() {
        String token = novoCliente();
        int lavadora = novaMaquina("LAVADORA");
        String pedidoId = criarPedido(token, lavadora, "LAVAR");
        JsonNode criado = pedido(token, pedidoId);
        assertEquals("AGUARDANDO_PAGAMENTO", criado.get("status").asString());
        assertTrue(maquina(token, lavadora).get("reservada").asBoolean());

        JsonNode pagamento = checkout(token, pedidoId, "PIX");
        assertTrue(pagamento.get("codigoPix").asString().startsWith("SANDBOX-"));
        // Repetir o checkout com o mesmo método devolve a mesma cobrança (idempotência).
        assertEquals(pagamento.get("id").asString(), checkout(token, pedidoId, "PIX").get("id").asString());

        String tx = transacao(pagamento);
        assertEquals(401, webhook(tx, "APROVADO", "12.00", "ev-" + UUID.randomUUID(), false).status());
        assertEquals(422, webhook(tx, "APROVADO", "1.00", "ev-" + UUID.randomUUID(), true).status());
        assertEquals("AGUARDANDO_PAGAMENTO", pedido(token, pedidoId).get("status").asString());

        String evento = "ev-" + UUID.randomUUID();
        assertEquals(200, webhook(tx, "APROVADO", "12.00", evento, true).status());
        assertEquals(200, webhook(tx, "APROVADO", "12.00", evento, true).status());
        assertEquals(200, webhook(tx, "APROVADO", "12.00", "ev-" + UUID.randomUUID(), true).status());

        JsonNode pago = pedido(token, pedidoId);
        assertEquals("EM_ANDAMENTO", pago.get("status").asString());
        assertEquals("APROVADO", pago.get("pagamento").get("status").asString());
        assertEquals(1, pago.get("ciclos").size());
        assertEquals("EM_USO", maquina(token, lavadora).get("status").asString());

        // Fim do ciclo: máquina volta a Disponível e pedido conclui (UC08).
        relogio.avancar(Duration.ofMinutes(41));
        cicloService.ciclosVencidos().forEach(cicloService::finalizarSeVencido);
        assertEquals("CONCLUIDO", pedido(token, pedidoId).get("status").asString());
        assertEquals("DISPONIVEL", maquina(token, lavadora).get("status").asString());

        // CT17: histórico
        JsonNode historico = get("/orders", token).corpo();
        assertEquals(pedidoId, historico.get("itens").get(0).get("id").asString());
    }

    @Test
    void cartaoRecusadoPermiteOutroMetodoCt14() {
        String token = novoCliente();
        int lavadora = novaMaquina("LAVADORA");
        String pedidoId = criarPedido(token, lavadora, "LAVAR");
        JsonNode cartao = checkout(token, pedidoId, "CARTAO_CREDITO");
        assertNull(cartao.get("codigoPix").asString(null));
        assertEquals(200, webhook(transacao(cartao), "RECUSADO", "12.00", "ev-" + UUID.randomUUID(), true).status());
        JsonNode p = pedido(token, pedidoId);
        assertEquals("AGUARDANDO_PAGAMENTO", p.get("status").asString());
        assertEquals("RECUSADO", p.get("pagamento").get("status").asString());
        assertEquals("PENDENTE", checkout(token, pedidoId, "PIX").get("status").asString());
    }

    @Test
    void pixExpiradoLiberaMaquinaEPagamentoTardioEEstornadoCt13() {
        String token = novoCliente();
        int lavadora = novaMaquina("LAVADORA");
        String pedidoId = criarPedido(token, lavadora, "LAVAR");
        JsonNode pagamento = checkout(token, pedidoId, "PIX");

        relogio.avancar(Duration.ofMinutes(5).plusSeconds(1));
        assertTrue(maquina(token, lavadora).get("contratavel").asBoolean());
        assertEquals(409, post("/orders/" + pedidoId + "/checkout", Map.of("metodo", "PIX"), token).status());
        pedidoService.expirarSeVencido(pedidoId);
        assertEquals("EXPIRADO", pedido(token, pedidoId).get("status").asString());

        String corpo = json.writeValueAsString(Map.of("eventoId", "ev-" + UUID.randomUUID(),
                "transacaoId", transacao(pagamento), "resultado", "APROVADO", "valor", "12.00",
                "ocorridoEm", relogio.instant().toString()));
        assertEquals(200, chamarComCabecalhos("POST", "/payments/webhook/sandbox", corpo, null,
                Map.of("X-Sandbox-Signature", sandbox.assinar(corpo))).status());
        JsonNode depois = pedido(token, pedidoId);
        assertEquals("EXPIRADO", depois.get("status").asString());
        assertEquals("ESTORNADO", depois.get("pagamento").get("status").asString());
        assertEquals("DISPONIVEL", maquina(token, lavadora).get("status").asString());
    }

    @Test
    void falhaNaMaquinaGeraEstornoIntegralCt15() {
        String token = novoCliente();
        int lavadora = novaMaquina("LAVADORA");
        simulador.definirFalha(lavadora, true);
        try {
            String pedidoId = criarPedido(token, lavadora, "LAVAR");
            JsonNode pagamento = checkout(token, pedidoId, "PIX");
            webhook(transacao(pagamento), "APROVADO", "12.00", "ev-" + UUID.randomUUID(), true);
            JsonNode p = pedido(token, pedidoId);
            assertEquals("ESTORNADO", p.get("status").asString());
            assertEquals("ESTORNADO", p.get("pagamento").get("status").asString());
            assertEquals("DISPONIVEL", maquina(token, lavadora).get("status").asString());
        } finally {
            simulador.definirFalha(lavadora, false);
        }
    }

    @Test
    void concorrenciaApenasOPrimeiroReservaCt16() throws Exception {
        int lavadora = novaMaquina("LAVADORA");
        int clientes = 6;
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < clientes; i++) {
            tokens.add(novoCliente());
        }
        ExecutorService executor = Executors.newFixedThreadPool(clientes);
        CountDownLatch largada = new CountDownLatch(1);
        List<Future<Integer>> resultados = new ArrayList<>();
        for (String token : tokens) {
            Callable<Integer> tarefa = () -> {
                largada.await();
                return post("/orders", Map.of("maquinaId", lavadora, "servico", "LAVAR"), token).status();
            };
            resultados.add(executor.submit(tarefa));
        }
        largada.countDown();
        int sucesso = 0;
        int conflito = 0;
        for (Future<Integer> f : resultados) {
            int status = f.get();
            if (status == 201) {
                sucesso++;
            } else if (status == 409) {
                conflito++;
            }
        }
        executor.shutdown();
        assertEquals(1, sucesso);
        assertEquals(clientes - 1, conflito);
    }

    @Test
    void comboLavarSecarUsaLavadoraESecadoraRn08() {
        String token = novoCliente();
        int lavadora = novaMaquina("LAVADORA");
        novaMaquina("SECADORA");
        String pedidoId = criarPedido(token, lavadora, "LAVAR_SECAR");
        assertEquals(0, new java.math.BigDecimal("18").compareTo(pedido(token, pedidoId).get("valorTotal").decimalValue()));
        JsonNode pagamento = checkout(token, pedidoId, "PIX");
        webhook(transacao(pagamento), "APROVADO", "18.00", "ev-" + UUID.randomUUID(), true);
        assertEquals(409, post("/orders/" + pedidoId + "/drying", null, token).status());

        relogio.avancar(Duration.ofMinutes(41));
        cicloService.ciclosVencidos().forEach(cicloService::finalizarSeVencido);
        JsonNode aposLavagem = pedido(token, pedidoId);
        assertEquals("EM_ANDAMENTO", aposLavagem.get("status").asString());
        assertTrue(aposLavagem.get("podeIniciarSecagem").asBoolean());

        Resposta secagem = post("/orders/" + pedidoId + "/drying", null, token);
        assertEquals(200, secagem.status(), secagem.texto());
        assertEquals(2, secagem.corpo().get("ciclos").size());
        assertEquals("SECAGEM", secagem.corpo().get("ciclos").get(1).get("etapa").asString());

        relogio.avancar(Duration.ofMinutes(41));
        cicloService.ciclosVencidos().forEach(cicloService::finalizarSeVencido);
        assertEquals("CONCLUIDO", pedido(token, pedidoId).get("status").asString());
    }

    @Test
    void cancelamentoLiberaMaquinaEOutroClienteNaoVePedido() {
        String token = novoCliente();
        String outro = novoCliente();
        int lavadora = novaMaquina("LAVADORA");
        String pedidoId = criarPedido(token, lavadora, "LAVAR");
        assertEquals(404, get("/orders/" + pedidoId, outro).status());
        assertEquals(404, post("/orders/" + pedidoId + "/cancel", null, outro).status());
        assertEquals("CANCELADO", post("/orders/" + pedidoId + "/cancel", null, token).corpo().get("status").asString());
        assertTrue(maquina(token, lavadora).get("contratavel").asBoolean());
    }

    @Test
    void carteiraNaoExpoeDadosDeCartaoCt24() {
        String token = novoCliente();
        int lavadora = novaMaquina("LAVADORA");
        String pedidoId = criarPedido(token, lavadora, "LAVAR");
        checkout(token, pedidoId, "CARTAO_DEBITO");
        JsonNode carteira = get("/wallet", token).corpo();
        assertEquals(1, carteira.get("pagamentos").size());
        assertEquals(0, carteira.get("metodosSalvos").size());
        assertFalse(carteira.toString().toLowerCase().contains("token"));
    }
}
