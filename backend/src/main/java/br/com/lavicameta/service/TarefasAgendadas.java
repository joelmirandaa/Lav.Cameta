package br.com.lavicameta.service;

import br.com.lavicameta.entity.StatusPedido;
import br.com.lavicameta.repository.PedidoRepository;
import java.time.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Rotinas periódicas: expiração de reservas (RN05), encerramento de ciclos, retomada de inícios
 * pendentes e de estornos. Cada item roda em transação própria para isolar falhas.
 */
@Component
@ConditionalOnProperty(name = "lavicameta.agendador.ativo", havingValue = "true", matchIfMissing = true)
public class TarefasAgendadas {

    private static final Logger LOG = LoggerFactory.getLogger(TarefasAgendadas.class);

    private final PedidoRepository pedidos;
    private final PedidoService pedidoService;
    private final CicloService cicloService;
    private final EstornoService estornoService;
    private final Clock clock;

    public TarefasAgendadas(PedidoRepository pedidos, PedidoService pedidoService, CicloService cicloService,
                            EstornoService estornoService, Clock clock) {
        this.pedidos = pedidos;
        this.pedidoService = pedidoService;
        this.cicloService = cicloService;
        this.estornoService = estornoService;
        this.clock = clock;
    }

    @Scheduled(fixedDelayString = "${lavicameta.agendador.intervalo-ms:5000}")
    public void executar() {
        executarComSeguranca("expirar pedidos", () -> pedidos
                .findIdsByStatusAndExpiraEmBefore(StatusPedido.AGUARDANDO_PAGAMENTO, clock.instant())
                .forEach(pedidoService::expirarSeVencido));
        executarComSeguranca("iniciar pedidos pagos", () -> cicloService.pedidosPagosSemCiclo()
                .forEach(cicloService::iniciarPrimeiraEtapa));
        executarComSeguranca("finalizar ciclos", () -> cicloService.ciclosVencidos()
                .forEach(cicloService::finalizarSeVencido));
        executarComSeguranca("atualizar minutos", cicloService::atualizarMinutosRestantes);
        executarComSeguranca("reprocessar estornos", () -> estornoService.pendentes()
                .forEach(estornoService::processar));
    }

    private static void executarComSeguranca(String tarefa, Runnable acao) {
        try {
            acao.run();
        } catch (RuntimeException ex) {
            LOG.error("Falha na tarefa agendada '{}'", tarefa, ex);
        }
    }
}
