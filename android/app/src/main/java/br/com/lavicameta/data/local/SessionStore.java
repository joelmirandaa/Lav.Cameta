package br.com.lavicameta.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import br.com.lavicameta.domain.model.Sessao;
import br.com.lavicameta.util.Event;
import java.time.Instant;

/**
 * Guarda o token de acesso no armazenamento privado do app (não acessível por outros apps).
 * Nenhum dado de cartão ou senha é armazenado no dispositivo.
 */
public class SessionStore {

    private static final String ARQUIVO = "lavicameta_sessao";
    private static final String TOKEN = "token";
    private static final String EXPIRA = "expira_em";
    private static final String PAPEL = "papel";
    private static final String NOME = "nome";

    private final SharedPreferences prefs;
    private final MutableLiveData<Event<Boolean>> sessaoExpirada = new MutableLiveData<>();

    public SessionStore(Context context) {
        this.prefs = context.getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
    }

    protected SessionStore(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public void salvar(Sessao sessao) {
        prefs.edit()
                .putString(TOKEN, sessao.token)
                .putString(EXPIRA, sessao.expiraEm)
                .putString(PAPEL, sessao.usuario.tipo)
                .putString(NOME, sessao.usuario.nome)
                .apply();
    }

    public String token() {
        return valida() ? prefs.getString(TOKEN, null) : null;
    }

    public boolean valida() {
        String token = prefs.getString(TOKEN, null);
        String expira = prefs.getString(EXPIRA, null);
        if (token == null || expira == null) {
            return false;
        }
        try {
            return Instant.parse(expira).isAfter(Instant.now());
        } catch (RuntimeException ex) {
            return false;
        }
    }

    public boolean isAdmin() {
        return "ADMIN".equals(prefs.getString(PAPEL, null));
    }

    public String nome() {
        return prefs.getString(NOME, "");
    }

    public void atualizarNome(String nome) {
        prefs.edit().putString(NOME, nome).apply();
    }

    public void limpar() {
        prefs.edit().clear().apply();
    }

    /** Chamado quando a API responde 401: a UI volta para o login. */
    public void notificarExpiracao() {
        limpar();
        sessaoExpirada.postValue(new Event<>(true));
    }

    public LiveData<Event<Boolean>> sessaoExpirada() {
        return sessaoExpirada;
    }
}
