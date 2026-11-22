package br.com.lavicameta.data.repository;

import br.com.lavicameta.data.local.SessionStore;
import br.com.lavicameta.data.remote.ApiCallback;
import br.com.lavicameta.data.remote.ApiService;
import br.com.lavicameta.domain.model.Sessao;
import br.com.lavicameta.util.Result;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/** Autenticação de clientes e administradores; persiste a sessão em caso de sucesso. */
public class AuthRepository {

    private final ApiService api;
    private final SessionStore sessao;

    public AuthRepository(ApiService api, SessionStore sessao) {
        this.api = api;
        this.sessao = sessao;
    }

    public void login(String identificador, String senha, Consumer<Result<Sessao>> destino) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("identificador", identificador);
        corpo.put("senha", senha);
        api.login(corpo).enqueue(ApiCallback.publico(salvando(destino)));
    }

    public void loginAdmin(String identificador, String senha, Consumer<Result<Sessao>> destino) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("identificador", identificador);
        corpo.put("senha", senha);
        api.loginAdmin(corpo).enqueue(ApiCallback.publico(salvando(destino)));
    }

    public void loginGoogle(String idToken, Consumer<Result<Sessao>> destino) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("idToken", idToken);
        api.loginGoogle(corpo).enqueue(ApiCallback.publico(salvando(destino)));
    }

    public void registrar(String nome, String email, String telefone, String senha, Consumer<Result<Sessao>> destino) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("nome", nome);
        corpo.put("email", vazioParaNulo(email));
        corpo.put("telefone", vazioParaNulo(telefone));
        corpo.put("senha", senha);
        api.registrar(corpo).enqueue(ApiCallback.publico(salvando(destino)));
    }

    public void solicitarRecuperacao(String email, Consumer<Result<Map<String, String>>> destino) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("email", email);
        api.solicitarRecuperacao(corpo).enqueue(ApiCallback.publico(destino));
    }

    public void redefinirSenha(String email, String codigo, String novaSenha, Consumer<Result<Map<String, String>>> destino) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("email", email);
        corpo.put("codigo", codigo);
        corpo.put("novaSenha", novaSenha);
        api.redefinirSenha(corpo).enqueue(ApiCallback.publico(destino));
    }

    public boolean sessaoValida() {
        return sessao.valida();
    }

    public boolean sessaoAdmin() {
        return sessao.isAdmin();
    }

    public void sair() {
        sessao.limpar();
    }

    private Consumer<Result<Sessao>> salvando(Consumer<Result<Sessao>> destino) {
        return resultado -> {
            if (resultado.isSuccess() && resultado.data != null) {
                sessao.salvar(resultado.data);
            }
            destino.accept(resultado);
        };
    }

    static String vazioParaNulo(String valor) {
        return valor == null || valor.trim().isEmpty() ? null : valor.trim();
    }
}
