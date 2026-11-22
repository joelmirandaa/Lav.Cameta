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

/** Cadastro do cliente (RF01, UC01). */
public class RegisterViewModel extends ViewModel {

    private final AuthRepository auth;
    private final MutableLiveData<Result<Sessao>> estado = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Erro>> errosCampos = new MutableLiveData<>(new HashMap<>());

    public RegisterViewModel(AuthRepository auth) {
        this.auth = auth;
    }

    public LiveData<Result<Sessao>> estado() {
        return estado;
    }

    public LiveData<Map<String, Erro>> errosCampos() {
        return errosCampos;
    }

    public void cadastrar(String nome, String email, String telefone, String senha, String confirmacao) {
        Map<String, Erro> erros = validar(nome, email, telefone, senha, confirmacao);
        errosCampos.setValue(erros);
        if (!erros.isEmpty() || (estado.getValue() != null && estado.getValue().isLoading())) {
            return;
        }
        estado.setValue(Result.loading());
        auth.registrar(nome.trim(), email, telefone, senha, estado::setValue);
    }

    static Map<String, Erro> validar(String nome, String email, String telefone, String senha, String confirmacao) {
        Map<String, Erro> erros = new HashMap<>();
        colocar(erros, "nome", FormValidator.obrigatorio(nome, 120));
        colocar(erros, "email", FormValidator.emailOpcional(email));
        colocar(erros, "telefone", FormValidator.telefoneOpcional(telefone));
        if (!erros.containsKey("email") && !erros.containsKey("telefone")) {
            colocar(erros, "email", FormValidator.contato(email, telefone));
        }
        colocar(erros, "senha", FormValidator.senha(senha));
        if (!erros.containsKey("senha")) {
            colocar(erros, "confirmacao", FormValidator.confirmacao(senha, confirmacao));
        }
        return erros;
    }

    private static void colocar(Map<String, Erro> erros, String campo, Erro erro) {
        if (erro != null) {
            erros.put(campo, erro);
        }
    }
}
