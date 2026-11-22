package br.com.lavicameta.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import br.com.lavicameta.data.repository.AuthRepository;
import br.com.lavicameta.domain.usecase.FormValidator;
import br.com.lavicameta.domain.usecase.FormValidator.Erro;
import br.com.lavicameta.util.Result;
import java.util.HashMap;
import java.util.Map;

/** Recuperação de senha em duas etapas: pedir código por e-mail e redefinir (RF04). */
public class RecoverPasswordViewModel extends ViewModel {

    public enum Etapa { SOLICITAR, REDEFINIR, CONCLUIDO }

    private final AuthRepository auth;
    private final MutableLiveData<Etapa> etapa = new MutableLiveData<>(Etapa.SOLICITAR);
    private final MutableLiveData<Result<Map<String, String>>> estado = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Erro>> errosCampos = new MutableLiveData<>(new HashMap<>());
    private String email;

    public RecoverPasswordViewModel(AuthRepository auth) {
        this.auth = auth;
    }

    public LiveData<Etapa> etapa() {
        return etapa;
    }

    public LiveData<Result<Map<String, String>>> estado() {
        return estado;
    }

    public LiveData<Map<String, Erro>> errosCampos() {
        return errosCampos;
    }

    public String email() {
        return email;
    }

    public void solicitar(String emailInformado) {
        Map<String, Erro> erros = new HashMap<>();
        Erro erro = FormValidator.emailObrigatorio(emailInformado);
        if (erro != null) {
            erros.put("email", erro);
        }
        errosCampos.setValue(erros);
        if (!erros.isEmpty() || carregando()) {
            return;
        }
        email = emailInformado.trim();
        estado.setValue(Result.loading());
        auth.solicitarRecuperacao(email, resultado -> {
            estado.setValue(resultado);
            if (resultado.isSuccess()) {
                etapa.setValue(Etapa.REDEFINIR);
            }
        });
    }

    public void redefinir(String codigo, String senha, String confirmacao) {
        Map<String, Erro> erros = new HashMap<>();
        if (FormValidator.vazio(codigo)) {
            erros.put("codigo", Erro.OBRIGATORIO);
        }
        Erro erroSenha = FormValidator.senha(senha);
        if (erroSenha != null) {
            erros.put("senha", erroSenha);
        } else if (FormValidator.confirmacao(senha, confirmacao) != null) {
            erros.put("confirmacao", Erro.SENHAS_DIFERENTES);
        }
        errosCampos.setValue(erros);
        if (!erros.isEmpty() || carregando()) {
            return;
        }
        estado.setValue(Result.loading());
        auth.redefinirSenha(email, codigo.trim(), senha, resultado -> {
            estado.setValue(resultado);
            if (resultado.isSuccess()) {
                etapa.setValue(Etapa.CONCLUIDO);
            }
        });
    }

    public void voltarParaSolicitacao() {
        etapa.setValue(Etapa.SOLICITAR);
    }

    private boolean carregando() {
        return estado.getValue() != null && estado.getValue().isLoading();
    }
}
