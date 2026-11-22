package br.com.lavicameta.integracao;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

/** Relógio de teste que permite avançar o tempo (expiração de 5 min, fim de ciclo). */
public class RelogioAjustavel extends Clock {

    private volatile Instant agora = Instant.now();

    public void avancar(Duration duracao) {
        agora = agora.plus(duracao);
    }

    public void sincronizar() {
        agora = Instant.now();
    }

    @Override
    public ZoneId getZone() {
        return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return Clock.fixed(agora, zone);
    }

    @Override
    public Instant instant() {
        return agora;
    }
}
