package br.com.lavicameta.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import br.com.lavicameta.data.repository.OrderRepository;
import br.com.lavicameta.domain.model.Pagina;
import br.com.lavicameta.domain.model.Pedido;
import br.com.lavicameta.util.Result;
import java.util.ArrayList;
import java.util.List;

/** Histórico de serviços com paginação (RF14). */
public class HistoryViewModel extends ViewModel {

    private static final int TAMANHO = 20;

    private final OrderRepository pedidos;
    private final MutableLiveData<Result<List<Pedido>>> itens = new MutableLiveData<>();
    private final List<Pedido> acumulado = new ArrayList<>();
    private int proximaPagina;
    private boolean temMais = true;
    private boolean carregando;

    public HistoryViewModel(OrderRepository pedidos) {
        this.pedidos = pedidos;
    }

    public LiveData<Result<List<Pedido>>> itens() {
        return itens;
    }

    public void recarregar() {
        proximaPagina = 0;
        temMais = true;
        acumulado.clear();
        carregando = false;
        itens.setValue(Result.loading());
        carregarMais();
    }

    public void carregarMais() {
        if (carregando || !temMais) {
            return;
        }
        carregando = true;
        pedidos.historico(proximaPagina, TAMANHO, (Result<Pagina<Pedido>> resultado) -> {
            carregando = false;
            if (resultado.isError()) {
                itens.setValue(Result.error(resultado.message, resultado.code, null));
                return;
            }
            acumulado.addAll(resultado.data.itens);
            temMais = resultado.data.temProxima;
            proximaPagina++;
            itens.setValue(Result.success(new ArrayList<>(acumulado)));
        });
    }
}
