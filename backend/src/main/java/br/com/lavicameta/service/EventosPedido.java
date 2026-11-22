package br.com.lavicameta.service;

/** Eventos de domínio processados somente após o commit da transação que os gerou. */
public final class EventosPedido {

    private EventosPedido() {
    }

    public record PagamentoAprovado(String pedidoId) {
    }

    public record EstornoSolicitado(String pagamentoId) {
    }
}
