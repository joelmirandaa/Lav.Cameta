package br.com.lavicameta.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Executa as ações externas apenas após a confirmação persistida (comando à máquina, estorno).
 * Se algo falhar aqui, o agendador reprocessa a partir do estado gravado.
 */
@Component
public class ProcessadorEventosPedido {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessadorEventosPedido.class);

    private final CicloService cicloService;
    private final EstornoService estornoService;

    public ProcessadorEventosPedido(CicloService cicloService, EstornoService estornoService) {
        this.cicloService = cicloService;
        this.estornoService = estornoService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void aoAprovarPagamento(EventosPedido.PagamentoAprovado evento) {
        try {
            cicloService.iniciarPrimeiraEtapa(evento.pedidoId());
        } catch (RuntimeException ex) {
            LOG.error("Início do pedido {} será reprocessado pelo agendador", evento.pedidoId(), ex);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void aoSolicitarEstorno(EventosPedido.EstornoSolicitado evento) {
        try {
            estornoService.processar(evento.pagamentoId());
        } catch (RuntimeException ex) {
            LOG.error("Estorno {} será reprocessado pelo agendador", evento.pagamentoId(), ex);
        }
    }
}
