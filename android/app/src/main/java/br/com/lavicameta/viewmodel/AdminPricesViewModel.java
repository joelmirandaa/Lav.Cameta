package br.com.lavicameta.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import br.com.lavicameta.data.repository.AdminRepository;
import br.com.lavicameta.domain.model.Servico;
import br.com.lavicameta.domain.usecase.FormValidator;
import br.com.lavicameta.util.Event;
import br.com.lavicameta.util.Result;
import java.math.BigDecimal;
import java.util.List;

/** Preços e durações dos serviços (RN01: configuráveis pelo administrador). */
public class AdminPricesViewModel extends ViewModel {

    private final AdminRepository admin;
    private final MutableLiveData<Result<List<Servico>>> servicos = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> mensagem = new MutableLiveData<>();
    private final MutableLiveData<Event<Boolean>> salvo = new MutableLiveData<>();

    public AdminPricesViewModel(AdminRepository admin) {
        this.admin = admin;
    }

    public LiveData<Result<List<Servico>>> servicos() { return servicos; }
    public LiveData<Event<String>> mensagem() { return mensagem; }
    public LiveData<Event<Boolean>> salvo() { return salvo; }

    public void carregar() {
        servicos.setValue(Result.loading());
        admin.servicos(servicos::setValue);
    }

    /** Retorna false quando os valores digitados são inválidos (a UI mostra o erro no campo). */
    public boolean salvar(Servico servico, String preco, String duracao) {
        BigDecimal valor = FormValidator.decimalPositivo(preco);
        Integer minutos = FormValidator.inteiroPositivo(duracao);
        if (valor == null || valor.scale() > 2 || minutos == null || minutos > 600) {
            return false;
        }
        admin.atualizarServico(servico, valor, minutos, resultado -> {
            if (resultado.isError()) {
                mensagem.setValue(new Event<>(resultado.message));
            } else {
                salvo.setValue(new Event<>(true));
            }
            admin.servicos(servicos::setValue);
        });
        return true;
    }
}
