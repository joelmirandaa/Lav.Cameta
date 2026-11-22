package br.com.lavicameta.controller;

import br.com.lavicameta.exception.ApiException;
import br.com.lavicameta.integration.payment.NotificacaoInvalidaException;
import br.com.lavicameta.integration.payment.NotificacaoPagamento;
import br.com.lavicameta.integration.payment.PaymentGateway;
import br.com.lavicameta.service.PagamentoService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Recebe notificações do gateway; a autenticidade é verificada pelo adaptador do provedor. */
@RestController
@RequestMapping("/api/v1/payments/webhook")
public class WebhookPagamentoController {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookPagamentoController.class);

    private final PaymentGateway gateway;
    private final PagamentoService pagamentoService;

    public WebhookPagamentoController(PaymentGateway gateway, PagamentoService pagamentoService) {
        this.gateway = gateway;
        this.pagamentoService = pagamentoService;
    }

    @PostMapping("/{provedor}")
    public ResponseEntity<Void> receber(@PathVariable String provedor, @RequestBody String corpo,
                                        HttpServletRequest request) {
        if (!gateway.provedor().equals(provedor)) {
            throw ApiException.naoEncontrado("Provedor não habilitado.");
        }
        NotificacaoPagamento notificacao;
        try {
            notificacao = gateway.interpretarNotificacao(corpo, cabecalhos(request));
        } catch (NotificacaoInvalidaException ex) {
            LOG.warn("Notificação de pagamento rejeitada ({}): {}", provedor, ex.getMessage());
            throw ApiException.naoAutorizado("NOTIFICACAO_INVALIDA", "Notificação não autenticada.");
        }
        pagamentoService.aplicarNotificacao(provedor, notificacao);
        return ResponseEntity.ok().build();
    }

    private static Map<String, String> cabecalhos(HttpServletRequest request) {
        Map<String, String> mapa = new HashMap<>();
        for (String nome : Collections.list(request.getHeaderNames())) {
            mapa.put(nome.toLowerCase(Locale.ROOT), request.getHeader(nome));
        }
        return mapa;
    }
}
