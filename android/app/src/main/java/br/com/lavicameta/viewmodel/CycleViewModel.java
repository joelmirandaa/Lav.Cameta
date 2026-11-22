package br.com.lavicameta.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import br.com.lavicameta.data.repository.OrderRepository;
import br.com.lavicameta.domain.model.Ciclo;
import br.com.lavicameta.domain.model.Pedido;
import br.com.lavicameta.util.Event;
import br.com.lavicameta.util.Poller;
import br.com.lavicameta.util.Result;

/** Acompanhamento do ciclo (UC08): tempo restante, fim de etapa e secagem do combo. */
public class CycleViewModel extends ViewModel {

    public enum Aviso { ETAPA_CONCLUIDA, PEDIDO_CONCLUIDO }

    private static final long INTERVALO_MS = 5000;

    private final OrderRepository pedidos;
    private final MutableLiveData<Result<Pedido>> pedido = new MutableLiveData<>();
    private final MutableLiveData<Result<Pedido>> secagem = new MutableLiveData<>();
    private final MutableLiveData<Event<Aviso>> aviso = new MutableLiveData<>();
    private final Poller poller;
    private String pedidoId;
    private String ultimoEstado;

    public CycleViewModel(OrderRepository pedidos) {
        this.pedidos = pedidos;
        this.poller = new Poller(INTERVALO_MS, this::consultar);
    }

    public LiveData<Result<Pedido>> pedido() { return pedido; }
    public LiveData<Result<Pedido>> secagem() { return secagem; }
    public LiveData<Event<Aviso>> aviso() { return aviso; }

    public void iniciar(String id) {
        if (pedidoId == null) {
            pedidoId = id;
            pedido.setValue(Result.loading());
        }
        consultar();
        poller.iniciar();
    }

    public void pausar() {
        poller.parar();
    }

    public void iniciarSecagem() {
        if (secagem.getValue() != null && secagem.getValue().isLoading()) {
            return;
        }
        secagem.setValue(Result.loading());
        pedidos.iniciarSecagem(pedidoId, resultado -> {
            secagem.setValue(resultado);
            if (resultado.isSuccess()) {
                aplicar(resultado.data);
            }
        });
    }

    private void consultar() {
        if (pedidoId == null) {
            return;
        }
        pedidos.obter(pedidoId, resultado -> {
            if (resultado.isSuccess()) {
                aplicar(resultado.data);
            } else if (pedido.getValue() == null || !pedido.getValue().isSuccess()) {
                pedido.setValue(resultado);
            }
        });
    }

    private void aplicar(Pedido atual) {
        pedido.setValue(Result.success(atual));
        String estado = chaveEstado(atual);
        if (ultimoEstado != null && !ultimoEstado.equals(estado)) {
            if (Pedido.CONCLUIDO.equals(atual.status)) {
                aviso.setValue(new Event<>(Aviso.PEDIDO_CONCLUIDO));
            } else if (atual.podeIniciarSecagem) {
                aviso.setValue(new Event<>(Aviso.ETAPA_CONCLUIDA));
            }
        }
        ultimoEstado = estado;
        if (Pedido.CONCLUIDO.equals(atual.status) || "ESTORNADO".equals(atual.status)) {
            poller.parar();
        }
    }

    static String chaveEstado(Pedido p) {
        Ciclo ciclo = p.cicloAtual();
        return p.status + "|" + (ciclo == null ? "-" : ciclo.etapa + ":" + ciclo.status) + "|" + p.podeIniciarSecagem;
    }

    @Override
    protected void onCleared() {
        poller.parar();
    }
}
