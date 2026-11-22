package br.com.lavicameta.util;

import android.os.Handler;
import android.os.Looper;

/** Repete uma tarefa na thread principal enquanto a tela está visível (RNF02: status em até 5 s). */
public final class Poller {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final long intervaloMs;
    private final Runnable tarefa;
    private boolean ativo;
    private final Runnable ciclo = new Runnable() {
        @Override
        public void run() {
            if (!ativo) {
                return;
            }
            tarefa.run();
            handler.postDelayed(this, intervaloMs);
        }
    };

    public Poller(long intervaloMs, Runnable tarefa) {
        this.intervaloMs = intervaloMs;
        this.tarefa = tarefa;
    }

    public void iniciar() {
        if (ativo) {
            return;
        }
        ativo = true;
        handler.postDelayed(ciclo, intervaloMs);
    }

    public void parar() {
        ativo = false;
        handler.removeCallbacks(ciclo);
    }
}
