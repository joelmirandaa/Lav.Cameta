package br.com.lavicameta.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import br.com.lavicameta.data.repository.AccountRepository;
import br.com.lavicameta.data.repository.AuthRepository;
import br.com.lavicameta.domain.model.Usuario;
import br.com.lavicameta.domain.usecase.FormValidator;
import br.com.lavicameta.domain.usecase.FormValidator.Erro;
import br.com.lavicameta.util.Event;
import br.com.lavicameta.util.Result;
import java.util.HashMap;
import java.util.Map;

/** Perfil do cliente: dados pessoais e troca de senha (RF16). */
public class ProfileViewModel extends ViewModel {

    private final AccountRepository conta;
    private final AuthRepository auth;
    private final MutableLiveData<Result<Usuario>> perfil = new MutableLiveData<>();
    private final MutableLiveData<Result<Usuario>> salvamento = new MutableLiveData<>();
    private final MutableLiveData<Result<Void>> trocaSenha = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Erro>> errosCampos = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<Event<String>> sucesso = new MutableLiveData<>();

    public ProfileViewModel(AccountRepository conta, AuthRepository auth) {
        this.conta = conta;
        this.auth = auth;
    }

    public LiveData<Result<Usuario>> perfil() { return perfil; }
    public LiveData<Result<Usuario>> salvamento() { return salvamento; }
    public LiveData<Result<Void>> trocaSenha() { return trocaSenha; }
    public LiveData<Map<String, Erro>> errosCampos() { return errosCampos; }
    public LiveData<Event<String>> sucesso() { return sucesso; }

    public void carregar() {
        perfil.setValue(Result.loading());
        conta.perfil(perfil::setValue);
    }

    public void salvar(String nome, String email, String telefone) {
        Map<String, Erro> erros = new HashMap<>();
        colocar(erros, "nome", FormValidator.obrigatorio(nome, 120));
        colocar(erros, "email", FormValidator.emailOpcional(email));
        colocar(erros, "telefone", FormValidator.telefoneOpcional(telefone));
        if (erros.isEmpty()) {
            colocar(erros, "email", FormValidator.contato(email, telefone));
        }
        errosCampos.setValue(erros);
        if (!erros.isEmpty()) {
            return;
        }
        salvamento.setValue(Result.loading());
        conta.atualizarPerfil(nome.trim(), email, telefone, resultado -> {
            salvamento.setValue(resultado);
            if (resultado.isSuccess()) {
                perfil.setValue(resultado);
                sucesso.setValue(new Event<>("perfil"));
            }
        });
    }

    public void alterarSenha(String atual, String nova, String confirmacao) {
        Map<String, Erro> erros = new HashMap<>();
        colocar(erros, "novaSenha", FormValidator.senha(nova));
        if (!erros.containsKey("novaSenha")) {
            colocar(erros, "confirmacao", FormValidator.confirmacao(nova, confirmacao));
        }
        Usuario u = perfil.getValue() != null ? perfil.getValue().data : null;
        if (u != null && u.possuiSenha && FormValidator.vazio(atual)) {
            erros.put("senhaAtual", Erro.OBRIGATORIO);
        }
        errosCampos.setValue(erros);
        if (!erros.isEmpty()) {
            return;
        }
        trocaSenha.setValue(Result.loading());
        conta.alterarSenha(atual, nova, resultado -> {
            trocaSenha.setValue(resultado);
            if (resultado.isSuccess()) {
                sucesso.setValue(new Event<>("senha"));
                carregar();
            }
        });
    }

    public void sair() {
        auth.sair();
    }

    private static void colocar(Map<String, Erro> erros, String campo, Erro erro) {
        if (erro != null) {
            erros.put(campo, erro);
        }
    }
}
