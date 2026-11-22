package br.com.lavicameta.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import br.com.lavicameta.data.repository.AdminRepository;
import br.com.lavicameta.domain.model.Maquina;
import br.com.lavicameta.util.Event;
import br.com.lavicameta.util.Result;
import java.util.List;

/** Gestão de máquinas: lista e alternância Manutenção ⇄ Disponível (UC15). */
public class AdminMachinesViewModel extends ViewModel {

    private final AdminRepository admin;
    private final MutableLiveData<Result<List<Maquina>>> maquinas = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> mensagem = new MutableLiveData<>();

    public AdminMachinesViewModel(AdminRepository admin) {
        this.admin = admin;
    }

    public LiveData<Result<List<Maquina>>> maquinas() { return maquinas; }
    public LiveData<Event<String>> mensagem() { return mensagem; }

    public void carregar() {
        maquinas.setValue(Result.loading());
        admin.maquinas(maquinas::setValue);
    }

    public void alternarManutencao(Maquina maquina) {
        String novo = Maquina.MANUTENCAO.equals(maquina.status) ? Maquina.DISPONIVEL : Maquina.MANUTENCAO;
        admin.alterarStatus(maquina.id, novo, resultado -> {
            if (resultado.isError()) {
                mensagem.setValue(new Event<>(resultado.message));
            }
            admin.maquinas(maquinas::setValue);
        });
    }
}
