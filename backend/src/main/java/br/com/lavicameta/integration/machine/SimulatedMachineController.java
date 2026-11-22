package br.com.lavicameta.integration.machine;

import br.com.lavicameta.entity.Maquina;
import br.com.lavicameta.exception.IntegracaoException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Máquina simulada para homologação (PDF 10.2 e risco "Criar simulador"). Aceita o comando de
 * início; o término ocorre pela duração do serviço. Permite marcar máquinas com falha para o CT15.
 * Ativo somente com MACHINE_CONTROLLER=simulador.
 */
@Component
@ConditionalOnProperty(name = "lavicameta.maquinas.controlador", havingValue = "simulador")
public class SimulatedMachineController implements MachineController {

    private static final Logger LOG = LoggerFactory.getLogger(SimulatedMachineController.class);

    private final Set<Integer> maquinasComFalha = ConcurrentHashMap.newKeySet();

    public SimulatedMachineController() {
        LOG.warn("Simulador de máquinas ativo: nenhum equipamento físico será acionado.");
    }

    @Override
    public void iniciarCiclo(Maquina maquina, int duracaoMin, String chaveIdempotencia) {
        if (maquinasComFalha.contains(maquina.getId())) {
            throw new IntegracaoException("Simulador: falha de comunicação com " + maquina.getNome());
        }
        LOG.info("Simulador: {} iniciou ciclo de {} min ({})", maquina.getNome(), duracaoMin, chaveIdempotencia);
    }

    public void definirFalha(Integer maquinaId, boolean ativa) {
        if (ativa) {
            maquinasComFalha.add(maquinaId);
        } else {
            maquinasComFalha.remove(maquinaId);
        }
    }

    public boolean possuiFalha(Integer maquinaId) {
        return maquinasComFalha.contains(maquinaId);
    }
}
