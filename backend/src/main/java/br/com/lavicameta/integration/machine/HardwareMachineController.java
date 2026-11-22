package br.com.lavicameta.integration.machine;

import br.com.lavicameta.entity.Maquina;
import br.com.lavicameta.exception.IntegracaoException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Adaptador do equipamento real. Fabricante, protocolo e conectividade ainda não foram definidos
 * (pendência documentada). Sem protocolo, o comando falha explicitamente e o fluxo de estorno (RN06)
 * é acionado — nunca se simula sucesso em produção.
 */
@Component
@ConditionalOnProperty(name = "lavicameta.maquinas.controlador", havingValue = "hardware", matchIfMissing = true)
public class HardwareMachineController implements MachineController {

    @Override
    public void iniciarCiclo(Maquina maquina, int duracaoMin, String chaveIdempotencia) {
        throw new IntegracaoException("Protocolo de comunicação com as máquinas ainda não configurado.");
    }
}
