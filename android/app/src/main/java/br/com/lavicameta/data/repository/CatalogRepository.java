package br.com.lavicameta.data.repository;

import br.com.lavicameta.data.local.SessionStore;
import br.com.lavicameta.data.remote.ApiCallback;
import br.com.lavicameta.data.remote.ApiService;
import br.com.lavicameta.domain.model.Maquina;
import br.com.lavicameta.domain.model.MaquinaDetalhe;
import br.com.lavicameta.util.Result;
import java.util.List;
import java.util.function.Consumer;

public class CatalogRepository {

    private final ApiService api;
    private final SessionStore sessao;

    public CatalogRepository(ApiService api, SessionStore sessao) {
        this.api = api;
        this.sessao = sessao;
    }

    public void maquinas(String filtro, Consumer<Result<List<Maquina>>> destino) {
        api.maquinas(filtro).enqueue(ApiCallback.autenticado(sessao, destino));
    }

    public void maquina(int id, Consumer<Result<MaquinaDetalhe>> destino) {
        api.maquina(id).enqueue(ApiCallback.autenticado(sessao, destino));
    }
}
