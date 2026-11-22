package br.com.lavicameta.integration.machine;

import br.com.lavicameta.entity.Maquina;
import br.com.lavicameta.exception.IntegracaoException;

/**
 * Porta de comando dos equipamentos (UC08). A chave de idempotência permite ao adaptador
 * descartar reenvios do mesmo comando.
 */
public interface MachineController {

    void iniciarCiclo(Maquina maquina, int duracaoMin, String chaveIdempotencia) throws IntegracaoException;
}
