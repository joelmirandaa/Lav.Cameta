package br.com.lavicameta.integration.payment;

import br.com.lavicameta.config.LavicametaProperties;
import br.com.lavicameta.entity.Pagamento;
import br.com.lavicameta.util.Hashes;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Gateway de HOMOLOGAÇÃO (PDF 10.2: "gateway em modo sandbox"). Não movimenta dinheiro.
 * As cobranças são confirmadas ou recusadas pela página /api/v1/sandbox/checkout/{transacao}
 * ou por webhook assinado com HMAC-SHA256 (cabeçalho X-Sandbox-Signature).
 * Ativo somente com PAYMENT_PROVIDER=sandbox; nunca usar em produção.
 */
@Component
@ConditionalOnProperty(name = "lavicameta.pagamento.provedor", havingValue = "sandbox")
public class SandboxPaymentGateway implements PaymentGateway {

    public static final String PROVEDOR = "sandbox";
    public static final String CABECALHO_ASSINATURA = "x-sandbox-signature";
    private static final Logger LOG = LoggerFactory.getLogger(SandboxPaymentGateway.class);

    private final String urlPublica;
    private final String segredoWebhook;
    private final JsonMapper jsonMapper;

    public SandboxPaymentGateway(LavicametaProperties propriedades, JsonMapper jsonMapper) {
        this.urlPublica = propriedades.pagamento().urlPublica();
        this.segredoWebhook = propriedades.pagamento().webhookSegredo();
        this.jsonMapper = jsonMapper;
        LOG.warn("Gateway de pagamento SANDBOX ativo: nenhuma cobrança real será feita.");
    }

    @Override
    public String provedor() {
        return PROVEDOR;
    }

    @Override
    public Cobranca criarCobranca(Pagamento pagamento) {
        String transacao = "SBX-" + UUID.randomUUID();
        String checkoutUrl = urlPublica + "/api/v1/sandbox/checkout/" + transacao;
        String codigoPix = pagamento.getMetodo().isCartao() ? null
                : "SANDBOX-LAVCAMETA|" + transacao + "|" + pagamento.getValor().toPlainString();
        return new Cobranca(transacao, codigoPix, checkoutUrl);
    }

    @Override
    public void estornar(Pagamento pagamento) {
        LOG.info("Sandbox: estorno registrado para a transação {}", pagamento.getTransacaoId());
    }

    @Override
    public NotificacaoPagamento interpretarNotificacao(String corpo, Map<String, String> cabecalhos) {
        if (segredoWebhook == null || segredoWebhook.isBlank()) {
            throw new NotificacaoInvalidaException("PAYMENT_WEBHOOK_SECRET não configurado");
        }
        String assinatura = cabecalhos.get(CABECALHO_ASSINATURA);
        if (assinatura == null || !Hashes.iguaisEmTempoConstante(assinatura.toLowerCase(), assinar(corpo))) {
            throw new NotificacaoInvalidaException("Assinatura inválida");
        }
        try {
            JsonNode json = jsonMapper.readTree(corpo);
            return new NotificacaoPagamento(
                    json.required("eventoId").asString(),
                    json.required("transacaoId").asString(),
                    NotificacaoPagamento.Resultado.valueOf(json.required("resultado").asString()),
                    new BigDecimal(json.required("valor").asString()),
                    json.hasNonNull("ocorridoEm") ? Instant.parse(json.get("ocorridoEm").asString()) : Instant.now(),
                    json.hasNonNull("motivo") ? json.get("motivo").asString() : null);
        } catch (RuntimeException ex) {
            throw new NotificacaoInvalidaException("Corpo da notificação inválido");
        }
    }

    /** Assinatura HMAC-SHA256 hexadecimal do corpo, usada por ferramentas de teste. */
    public String assinar(String corpo) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(segredoWebhook.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(corpo.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
