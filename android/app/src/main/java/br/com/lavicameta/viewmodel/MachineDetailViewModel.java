package br.com.lavicameta.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import br.com.lavicameta.data.repository.CatalogRepository;
import br.com.lavicameta.data.repository.OrderRepository;
import br.com.lavicameta.domain.model.MaquinaDetalhe;
import br.com.lavicameta.domain.model.Pedido;
import br.com.lavicameta.domain.model.Servico;
import br.com.lavicameta.util.Event;
import br.com.lavicameta.util.Result;

/** T03 Detalhe da máquina: escolha do serviço com preço visível e criação do pedido (RF07–RF09). */
public class MachineDetailViewModel extends ViewModel {

    private final CatalogRepository catalogo;
    private final OrderRepository pedidos;
    private final MutableLiveData<Result<MaquinaDetalhe>> detalhe = new MutableLiveData<>();
    private final MutableLiveData<Servico> selecionado = new MutableLiveData<>();
    private final MutableLiveData<Result<Pedido>> criacao = new MutableLiveData<>();
    private final MutableLiveData<Event<Pedido>> pedidoCriado = new MutableLiveData<>();
    private int maquinaId;

    public MachineDetailViewModel(CatalogRepository catalogo, OrderRepository pedidos) {
        this.catalogo = catalogo;
        this.pedidos = pedidos;
    }

    public LiveData<Result<MaquinaDetalhe>> detalhe() {
        return detalhe;
    }

    public LiveData<Servico> selecionado() {
        return selecionado;
    }

    public LiveData<Result<Pedido>> criacao() {
        return criacao;
    }

    public LiveData<Event<Pedido>> pedidoCriado() {
        return pedidoCriado;
    }

    public void carregar(int id) {
        maquinaId = id;
        detalhe.setValue(Result.loading());
        catalogo.maquina(id, detalhe::setValue);
    }

    public void recarregar() {
        carregar(maquinaId);
    }

    public void selecionar(Servico servico) {
        selecionado.setValue(servico);
    }

    public void continuar() {
        Servico servico = selecionado.getValue();
        if (servico == null || (criacao.getValue() != null && criacao.getValue().isLoading())) {
            return;
        }
        criacao.setValue(Result.loading());
        pedidos.criar(maquinaId, servico.nome, resultado -> {
            criacao.setValue(resultado);
            if (resultado.isSuccess()) {
                pedidoCriado.setValue(new Event<>(resultado.data));
            } else {
                recarregar(); // o status pode ter mudado (máquina reservada por outro cliente)
            }
        });
    }
}
