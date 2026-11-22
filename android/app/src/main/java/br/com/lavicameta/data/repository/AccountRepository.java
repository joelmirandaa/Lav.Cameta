package br.com.lavicameta.data.repository;

import br.com.lavicameta.data.local.SessionStore;
import br.com.lavicameta.data.remote.ApiCallback;
import br.com.lavicameta.data.remote.ApiService;
import br.com.lavicameta.domain.model.Carteira;
import br.com.lavicameta.domain.model.Usuario;
import br.com.lavicameta.util.Result;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/** Perfil (RF16) e carteira (RF15) do cliente. */
public class AccountRepository {

    private final ApiService api;
    private final SessionStore sessao;

    public AccountRepository(ApiService api, SessionStore sessao) {
        this.api = api;
        this.sessao = sessao;
    }

    public void perfil(Consumer<Result<Usuario>> destino) {
        api.perfil().enqueue(ApiCallback.autenticado(sessao, destino));
    }

    public void atualizarPerfil(String nome, String email, String telefone, Consumer<Result<Usuario>> destino) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("nome", nome);
        corpo.put("email", AuthRepository.vazioParaNulo(email));
        corpo.put("telefone", AuthRepository.vazioParaNulo(telefone));
        api.atualizarPerfil(corpo).enqueue(ApiCallback.autenticado(sessao, resultado -> {
            if (resultado.isSuccess() && resultado.data != null) {
                sessao.atualizarNome(resultado.data.nome);
            }
            destino.accept(resultado);
        }));
    }

    public void alterarSenha(String senhaAtual, String novaSenha, Consumer<Result<Void>> destino) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("senhaAtual", AuthRepository.vazioParaNulo(senhaAtual));
        corpo.put("novaSenha", novaSenha);
        api.alterarSenha(corpo).enqueue(ApiCallback.autenticado(sessao, destino));
    }

    public void carteira(Consumer<Result<Carteira>> destino) {
        api.carteira().enqueue(ApiCallback.autenticado(sessao, destino));
    }

    public void removerMetodo(String id, Consumer<Result<Void>> destino) {
        api.removerMetodo(id).enqueue(ApiCallback.autenticado(sessao, destino));
    }
}
