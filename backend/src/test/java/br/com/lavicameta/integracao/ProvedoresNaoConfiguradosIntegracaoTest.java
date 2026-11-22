package br.com.lavicameta.integracao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

/**
 * Configuração padrão de produção enquanto Bex e hardware não têm contrato: nenhuma cobrança é
 * criada, nenhum webhook é aceito e nada é simulado como sucesso.
 */
@TestPropertySource(properties = {
        "lavicameta.pagamento.provedor=bex",
        "lavicameta.maquinas.controlador=hardware"})
class ProvedoresNaoConfiguradosIntegracaoTest extends IntegracaoBase {

    @Test
    void checkoutFalhaExplicitamenteSemGatewayConfigurado() {
        String token = novoCliente();
        int lavadora = novaMaquina("LAVADORA");
        String pedidoId = criarPedido(token, lavadora, "LAVAR");
        Resposta r = post("/orders/" + pedidoId + "/checkout", Map.of("metodo", "PIX"), token);
        assertEquals(503, r.status());
        assertEquals("GATEWAY_INDISPONIVEL", r.corpo().get("codigo").asString());
        assertEquals("AGUARDANDO_PAGAMENTO", get("/orders/" + pedidoId, token).corpo().get("status").asString());
    }

    @Test
    void webhookESandboxIndisponiveis() {
        assertEquals(401, chamarComCabecalhos("POST", "/payments/webhook/bex", "{}", null, Map.of()).status());
        assertEquals(404, chamarComCabecalhos("POST", "/payments/webhook/sandbox", "{}", null, Map.of()).status());
        assertEquals(404, get("/sandbox/checkout/SBX-1", null).status());
    }
}
