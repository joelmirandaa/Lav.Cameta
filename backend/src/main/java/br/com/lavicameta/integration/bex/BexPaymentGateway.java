package br.com.lavicameta.integration.bex;

import br.com.lavicameta.entity.Pagamento;
import br.com.lavicameta.exception.IntegracaoException;
import br.com.lavicameta.integration.payment.NotificacaoInvalidaException;
import br.com.lavicameta.integration.payment.NotificacaoPagamento;
import br.com.lavicameta.integration.payment.PaymentGateway;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Adaptador do checkout Bex (decisão confirmada). A documentação oficial (endpoints, autenticação,
 * webhook, assinatura, estorno) ainda NÃO foi fornecida; por isso, conforme AGENTS.md, nenhum
 * contrato é presumido: toda operação falha explicitamente e nenhum pagamento é dado como aprovado.
 * Implementar aqui ao receber a documentação, mantendo a interface {@link PaymentGateway}.
 */
@Component
@ConditionalOnProperty(name = "lavicameta.pagamento.provedor", havingValue = "bex", matchIfMissing = true)
public class BexPaymentGateway implements PaymentGateway {

    public static final String PROVEDOR = "bex";
    private static final String MSG = "Integração Bex aguardando documentação oficial do provedor.";

    @Override
    public String provedor() {
        return PROVEDOR;
    }

    @Override
    public Cobranca criarCobranca(Pagamento pagamento) {
        throw new IntegracaoException(MSG);
    }

    @Override
    public void estornar(Pagamento pagamento) {
        throw new IntegracaoException(MSG);
    }

    @Override
    public NotificacaoPagamento interpretarNotificacao(String corpo, Map<String, String> cabecalhos) {
        throw new NotificacaoInvalidaException(MSG);
    }
}
