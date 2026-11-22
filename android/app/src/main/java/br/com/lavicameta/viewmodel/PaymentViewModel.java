package br.com.lavicameta.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import br.com.lavicameta.data.repository.OrderRepository;
import br.com.lavicameta.domain.model.Pagamento;
import br.com.lavicameta.domain.model.Pedido;
import br.com.lavicameta.util.Event;
import br.com.lavicameta.util.Poller;
import br.com.lavicameta.util.Result;

/**
 * T04 Pagamento: gera a cobrança (PIX ou cartão), acompanha a confirmação feita pelo backend e
 * a contagem dos 5 minutos de reserva (RF10–RF12, RN03, RN05).
 */
public class PaymentViewModel extends ViewModel {

    public enum Desfecho { CONFIRMADO, EXPIRADO, CANCELADO, ESTORNADO }

    private static final long INTERVALO_CONSULTA_MS = 3000;

    private final OrderRepository pedidos;
    private final MutableLiveData<Result<Pedido>> pedido = new MutableLiveData<>();
    private final MutableLiveData<Result<Pagamento>> cobranca = new MutableLiveData<>();
    private final MutableLiveData<Long> segundosRestantes = new MutableLiveData<>();
    private final MutableLiveData<Event<Desfecho>> desfecho = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> mensagem = new MutableLiveData<>();
    private final Poller consulta;
    private final Poller relogio;
    private String pedidoId;
    private boolean encerrado;

    public PaymentViewModel(OrderRepository pedidos) {
        this.pedidos = pedidos;
        this.consulta = new Poller(INTERVALO_CONSULTA_MS, this::consultar);
        this.relogio = new Poller(1000, this::contar);
    }

    public LiveData<Result<Pedido>> pedido() { return pedido; }
    public LiveData<Result<Pagamento>> cobranca() { return cobranca; }
    public LiveData<Long> segundosRestantes() { return segundosRestantes; }
    public LiveData<Event<Desfecho>> desfecho() { return desfecho; }
    public LiveData<Event<String>> mensagem() { return mensagem; }

    public void iniciar(String id) {
        if (pedidoId != null) {
            return;
        }
        pedidoId = id;
        pedido.setValue(Result.loading());
        pedidos.obter(id, resultado -> {
            pedido.setValue(resultado);
            if (resultado.isSuccess()) {
                avaliar(resultado.data);
            }
        });
    }

    public void pagar(String metodo) {
        if (encerrado || (cobranca.getValue() != null && cobranca.getValue().isLoading())) {
            return;
        }
        cobranca.setValue(Result.loading());
        pedidos.checkout(pedidoId, metodo, resultado -> {
            cobranca.setValue(resultado);
            if (resultado.isError() && "PEDIDO_EXPIRADO".equals(resultado.code)) {
                encerrar(Desfecho.EXPIRADO);
            }
        });
    }

    public void retomarAcompanhamento() {
        if (!encerrado && pedidoId != null) {
            consulta.iniciar();
            relogio.iniciar();
            consultar();
        }
    }

    public void pausarAcompanhamento() {
        consulta.parar();
        relogio.parar();
    }

    public void cancelar() {
        if (encerrado || pedidoId == null) {
            return;
        }
        pedidos.cancelar(pedidoId, resultado -> {
            if (resultado.isSuccess()) {
                encerrar(Desfecho.CANCELADO);
            } else {
                mensagem.setValue(new Event<>(resultado.message));
                consultar();
            }
        });
    }

    private void consultar() {
        if (pedidoId == null || encerrado) {
            return;
        }
        pedidos.obter(pedidoId, resultado -> {
            if (resultado.isSuccess()) {
                pedido.setValue(resultado);
                avaliar(resultado.data);
            }
        });
    }

    private void avaliar(Pedido atual) {
        segundosRestantes.setValue(atual.segundosParaExpirar);
        Pagamento pagamento = atual.pagamento;
        if (pagamento != null && Pagamento.RECUSADO.equals(pagamento.status)) {
            Result<Pagamento> ultimo = cobranca.getValue();
            if (ultimo != null && ultimo.isSuccess() && ultimo.data != null && ultimo.data.id.equals(pagamento.id)) {
                cobranca.setValue(Result.success(pagamento));
            }
        }
        switch (atual.status) {
            case "PAGO":
            case "EM_ANDAMENTO":
            case "CONCLUIDO":
                encerrar(Desfecho.CONFIRMADO);
                break;
            case "EXPIRADO":
                encerrar(Desfecho.EXPIRADO);
                break;
            case "CANCELADO":
                encerrar(Desfecho.CANCELADO);
                break;
            case "ESTORNADO":
                encerrar(Desfecho.ESTORNADO);
                break;
            default:
                break;
        }
    }

    private void contar() {
        Long atual = segundosRestantes.getValue();
        if (atual != null && atual > 0) {
            segundosRestantes.setValue(atual - 1);
        }
    }

    private void encerrar(Desfecho resultado) {
        if (encerrado) {
            return;
        }
        encerrado = true;
        pausarAcompanhamento();
        desfecho.setValue(new Event<>(resultado));
    }

    @Override
    protected void onCleared() {
        pausarAcompanhamento();
    }
}
