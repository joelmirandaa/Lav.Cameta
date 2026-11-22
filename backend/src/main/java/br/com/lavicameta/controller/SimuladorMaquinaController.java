package br.com.lavicameta.controller;

import br.com.lavicameta.integration.machine.SimulatedMachineController;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Homologação (CT15): marca uma máquina simulada para falhar no próximo comando. Só admin. */
@RestController
@RequestMapping("/api/v1/admin/simulator/machines")
@ConditionalOnProperty(name = "lavicameta.maquinas.controlador", havingValue = "simulador")
public class SimuladorMaquinaController {

    private final SimulatedMachineController simulador;

    public SimuladorMaquinaController(SimulatedMachineController simulador) {
        this.simulador = simulador;
    }

    @PutMapping("/{id}/failure")
    public Map<String, Object> definirFalha(@PathVariable Integer id, @RequestParam boolean ativa) {
        simulador.definirFalha(id, ativa);
        return Map.of("maquinaId", id, "falhaAtiva", simulador.possuiFalha(id));
    }
}
