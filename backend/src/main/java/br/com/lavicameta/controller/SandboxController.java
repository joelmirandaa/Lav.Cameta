package br.com.lavicameta.controller;

import br.com.lavicameta.dto.PedidoDtos.PagamentoResponse;
import br.com.lavicameta.entity.StatusPagamento;
import br.com.lavicameta.integration.payment.NotificacaoPagamento;
import br.com.lavicameta.integration.payment.SandboxPaymentGateway;
import br.com.lavicameta.service.PagamentoService;
import java.time.Clock;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

/**
 * Checkout de HOMOLOGAÇÃO: substitui a página do provedor enquanto a documentação Bex não chega.
 * Aprovar/recusar aqui gera a mesma notificação interna que um webhook autenticado geraria.
 * Existe somente com PAYMENT_PROVIDER=sandbox.
 */
@RestController
@RequestMapping("/api/v1/sandbox/checkout")
@ConditionalOnProperty(name = "lavicameta.pagamento.provedor", havingValue = "sandbox")
public class SandboxController {

    private final PagamentoService pagamentoService;
    private final Clock clock;

    public SandboxController(PagamentoService pagamentoService, Clock clock) {
        this.pagamentoService = pagamentoService;
        this.clock = clock;
    }

    @GetMapping(value = "/{transacao}", produces = MediaType.TEXT_HTML_VALUE)
    public String pagina(@PathVariable String transacao) {
        return renderizar(transacao, pagamentoService.consultarTransacao(transacao), null);
    }

    @PostMapping(value = "/{transacao}", produces = MediaType.TEXT_HTML_VALUE)
    public String decidir(@PathVariable String transacao, @RequestParam NotificacaoPagamento.Resultado resultado) {
        PagamentoResponse pagamento = pagamentoService.consultarTransacao(transacao);
        String mensagem;
        if (pagamento.status() != StatusPagamento.PENDENTE) {
            mensagem = "Esta cobrança não está mais pendente.";
        } else {
            pagamentoService.aplicarNotificacao(SandboxPaymentGateway.PROVEDOR, new NotificacaoPagamento(
                    "sandbox-" + UUID.randomUUID(), transacao, resultado, pagamento.valor(), clock.instant(),
                    resultado == NotificacaoPagamento.Resultado.RECUSADO ? "Recusado no sandbox" : null));
            mensagem = resultado == NotificacaoPagamento.Resultado.APROVADO
                    ? "Pagamento aprovado. Volte ao aplicativo." : "Pagamento recusado. Volte ao aplicativo.";
        }
        return renderizar(transacao, pagamentoService.consultarTransacao(transacao), mensagem);
    }

    private static String renderizar(String transacao, PagamentoResponse pagamento, String mensagem) {
        String tx = HtmlUtils.htmlEscape(transacao);
        String aviso = mensagem == null ? "" : "<p class=\"aviso\" role=\"status\">" + HtmlUtils.htmlEscape(mensagem) + "</p>";
        String acoes = pagamento.status() == StatusPagamento.PENDENTE
                ? """
                  <form method="post"><input type="hidden" name="resultado" value="APROVADO">
                  <button class="primario" type="submit">Aprovar pagamento</button></form>
                  <form method="post"><input type="hidden" name="resultado" value="RECUSADO">
                  <button class="secundario" type="submit">Recusar pagamento</button></form>
                  """
                : "";
        return """
                <!doctype html><html lang="pt-BR"><head><meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>lav.cametá — checkout de homologação</title>
                <link rel="preconnect" href="https://fonts.googleapis.com">
                <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600;700&display=swap" rel="stylesheet">
                <style>
                 :root{--cor-principal:#45257A;--cor-principal-escura:#341B5E;--cor-secundaria:#9B7FE0;--cor-fundo-secao:#F4F3F7;
                  --cor-texto:#2E2B36;--cor-texto-secundario:#5E5A6B;--cor-borda:#E2DEEB;--cor-alerta:#B26B00;--cor-info-fundo:#EFEBF5}
                 body{margin:0;font-family:Outfit,system-ui,sans-serif;line-height:1.5;background:var(--cor-fundo-secao);color:var(--cor-texto)}
                 main{max-width:420px;margin:32px auto;padding:24px;background:#fff;border:1px solid var(--cor-borda);border-radius:16px;
                  box-shadow:0 2px 8px rgba(69,37,122,0.08)}
                 h1{font-size:24px;line-height:1.2;margin:0 0 8px}.selo{color:var(--cor-alerta);font-weight:600}
                 dl{display:grid;grid-template-columns:auto 1fr;gap:8px 16px}dt{color:var(--cor-texto-secundario)}dd{margin:0;word-break:break-all}
                 button{width:100%%;height:52px;border-radius:12px;font:600 16px Outfit,system-ui,sans-serif;margin-top:12px;cursor:pointer;
                  transition:background-color 150ms ease-out}
                 button:focus-visible{outline:2px solid var(--cor-secundaria);box-shadow:0 0 0 3px rgba(155,127,224,0.4)}
                 .primario{background:var(--cor-principal);color:#fff;border:0}.primario:hover,.primario:active{background:var(--cor-principal-escura)}
                 .secundario{background:transparent;color:var(--cor-principal);border:2px solid var(--cor-principal)}
                 .aviso{padding:12px;border-radius:12px;background:var(--cor-info-fundo);color:var(--cor-principal)}
                </style></head><body><main>
                <h1>Checkout de homologação</h1>
                <p class="selo">Ambiente de testes — nenhum valor é cobrado.</p>
                <dl><dt>Transação</dt><dd>%s</dd><dt>Método</dt><dd>%s</dd><dt>Valor</dt><dd>R$ %s</dd><dt>Status</dt><dd>%s</dd></dl>
                %s%s
                </main></body></html>
                """.formatted(tx, pagamento.metodo(), pagamento.valor().toPlainString().replace('.', ','),
                pagamento.status(), aviso, acoes);
    }
}
