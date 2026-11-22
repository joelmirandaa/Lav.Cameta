package br.com.lavicameta.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import br.com.lavicameta.data.repository.CatalogRepository;
import br.com.lavicameta.domain.model.Maquina;
import br.com.lavicameta.util.Poller;
import br.com.lavicameta.util.Result;
import java.util.List;

/** T02 "Escolha uma máquina": lista com filtro e atualização periódica do status (RF05, RF06, RNF02). */
public class MachinesViewModel extends ViewModel {

    public static final String LAVAR = "LAVAR";
    public static final String SECAR = "SECAR";
    public static final String AMBOS = "AMBOS";
    private static final long INTERVALO_MS = 5000;

    private final CatalogRepository catalogo;
    private final MutableLiveData<Result<List<Maquina>>> maquinas = new MutableLiveData<>();
    private final Poller poller;
    private String filtro = AMBOS;
    private int requisicao;

    public MachinesViewModel(CatalogRepository catalogo) {
        this.catalogo = catalogo;
        this.poller = new Poller(INTERVALO_MS, () -> buscar(false));
    }

    public LiveData<Result<List<Maquina>>> maquinas() {
        return maquinas;
    }

    public String filtro() {
        return filtro;
    }

    public void filtrar(String novoFiltro) {
        if (!novoFiltro.equals(filtro)) {
            filtro = novoFiltro;
            buscar(true);
        }
    }

    public void atualizar() {
        buscar(true);
    }

    public void iniciarAtualizacaoAutomatica() {
        buscar(maquinas.getValue() == null);
        poller.iniciar();
    }

    public void pararAtualizacaoAutomatica() {
        poller.parar();
    }

    private void buscar(boolean mostrarCarregando) {
        int minha = ++requisicao;
        if (mostrarCarregando) {
            maquinas.setValue(Result.loading());
        }
        catalogo.maquinas(filtro, resultado -> {
            // Ignora respostas antigas quando o filtro muda durante a requisição.
            if (minha != requisicao) {
                return;
            }
            if (resultado.isError() && !mostrarCarregando && maquinas.getValue() != null
                    && maquinas.getValue().isSuccess()) {
                return; // mantém a última lista válida em falhas transitórias da atualização automática
            }
            maquinas.setValue(resultado);
        });
    }

    @Override
    protected void onCleared() {
        poller.parar();
    }
}
