package br.com.lavicameta.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import br.com.lavicameta.data.repository.AuthRepository;
import br.com.lavicameta.domain.model.Sessao;
import br.com.lavicameta.domain.usecase.FormValidator;
import br.com.lavicameta.domain.usecase.FormValidator.Erro;
import br.com.lavicameta.util.Result;
import java.util.HashMap;
import java.util.Map;

/** Login do cliente (T01) e do administrador (T05). */
public class LoginViewModel extends ViewModel {

    public static final String CAMPO_IDENTIFICADOR = "identificador";
    public static final String CAMPO_SENHA = "senha";

    private final AuthRepository auth;
    private final MutableLiveData<Result<Sessao>> estado = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Erro>> errosCampos = new MutableLiveData<>(new HashMap<>());

    public LoginViewModel(AuthRepository auth) {
        this.auth = auth;
    }

    public LiveData<Result<Sessao>> estado() {
        return estado;
    }

    public LiveData<Map<String, Erro>> errosCampos() {
        return errosCampos;
    }

    public boolean sessaoAtiva() {
        return auth.sessaoValida();
    }

    public boolean sessaoAdmin() {
        return auth.sessaoAdmin();
    }

    public void entrar(String identificador, String senha, boolean admin) {
        Map<String, Erro> erros = new HashMap<>();
        Erro erroId = FormValidator.obrigatorio(identificador, 150);
        if (erroId == null && identificador.contains("@")) {
            erroId = FormValidator.emailOpcional(identificador);
        }
        if (erroId != null) {
            erros.put(CAMPO_IDENTIFICADOR, erroId);
        }
        if (FormValidator.vazio(senha)) {
            erros.put(CAMPO_SENHA, Erro.OBRIGATORIO);
        }
        errosCampos.setValue(erros);
        if (!erros.isEmpty() || emAndamento()) {
            return;
        }
        estado.setValue(Result.loading());
        if (admin) {
            auth.loginAdmin(identificador.trim(), senha, estado::setValue);
        } else {
            auth.login(identificador.trim(), senha, estado::setValue);
        }
    }

    public void entrarComGoogle(String idToken) {
        if (emAndamento()) {
            return;
        }
        estado.setValue(Result.loading());
        auth.loginGoogle(idToken, estado::setValue);
    }

    public void sair() {
        auth.sair();
    }

    private boolean emAndamento() {
        Result<Sessao> atual = estado.getValue();
        return atual != null && atual.isLoading();
    }
}
