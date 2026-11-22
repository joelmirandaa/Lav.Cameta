package br.com.lavicameta.integration.payment;

import br.com.lavicameta.entity.Pagamento;
import br.com.lavicameta.exception.IntegracaoException;
import java.util.Map;

/**
 * Porta de integração com o gateway de pagamento (AD-005). Cada provedor implementa a criação
 * da cobrança, o estorno e a autenticação/interpretação das notificações que envia.
 */
public interface PaymentGateway {

    /** Nome gravado em {@code pagamentos.provedor} e usado na rota do webhook. */
    String provedor();

    /** Cria a cobrança no provedor. Nunca recebe nem devolve dados completos de cartão. */
    Cobranca criarCobranca(Pagamento pagamento) throws IntegracaoException;

    /** Solicita estorno integral da transação aprovada (RN06). */
    void estornar(Pagamento pagamento) throws IntegracaoException;

    /**
     * Autentica e interpreta uma notificação recebida. Deve lançar
     * {@link NotificacaoInvalidaException} se a origem não puder ser comprovada.
     */
    NotificacaoPagamento interpretarNotificacao(String corpo, Map<String, String> cabecalhos);

    record Cobranca(String transacaoId, String codigoPix, String checkoutUrl) {
    }
}
