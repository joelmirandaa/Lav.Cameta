package br.com.lavicameta;

import android.content.Context;
import br.com.lavicameta.data.local.SessionStore;
import br.com.lavicameta.data.remote.ApiClient;
import br.com.lavicameta.data.remote.ApiService;
import br.com.lavicameta.data.repository.AccountRepository;
import br.com.lavicameta.data.repository.AdminRepository;
import br.com.lavicameta.data.repository.AuthRepository;
import br.com.lavicameta.data.repository.CatalogRepository;
import br.com.lavicameta.data.repository.OrderRepository;

/** Injeção manual de dependências: uma instância de cada repositório por processo. */
public final class AppContainer {

    public final SessionStore sessao;
    public final AuthRepository auth;
    public final CatalogRepository catalogo;
    public final OrderRepository pedidos;
    public final AccountRepository conta;
    public final AdminRepository admin;

    AppContainer(Context context) {
        sessao = new SessionStore(context.getApplicationContext());
        ApiService api = ApiClient.criar(BuildConfig.API_BASE_URL, sessao);
        auth = new AuthRepository(api, sessao);
        catalogo = new CatalogRepository(api, sessao);
        pedidos = new OrderRepository(api, sessao);
        conta = new AccountRepository(api, sessao);
        admin = new AdminRepository(api, sessao);
    }
}
