package br.com.lavicameta.data.repository;

import br.com.lavicameta.data.local.SessionStore;
import br.com.lavicameta.data.remote.ApiCallback;
import br.com.lavicameta.data.remote.ApiService;
import br.com.lavicameta.domain.model.Pagamento;
import br.com.lavicameta.domain.model.Pagina;
import br.com.lavicameta.domain.model.Pedido;
import br.com.lavicameta.util.Result;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class OrderRepository {

    private final ApiService api;
    private final SessionStore sessao;

    public OrderRepository(ApiService api, SessionStore sessao) {
        this.api = api;
        this.sessao = sessao;
    }

    public void criar(int maquinaId, String servico, Consumer<Result<Pedido>> destino) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("maquinaId", maquinaId);
        corpo.put("servico", servico);
        api.criarPedido(corpo).enqueue(ApiCallback.autenticado(sessao, destino));
    }

    public void obter(String pedidoId, Consumer<Result<Pedido>> destino) {
        api.pedido(pedidoId).enqueue(ApiCallback.autenticado(sessao, destino));
    }

    public void cancelar(String pedidoId, Consumer<Result<Pedido>> destino) {
        api.cancelarPedido(pedidoId).enqueue(ApiCallback.autenticado(sessao, destino));
    }

    public void checkout(String pedidoId, String metodo, Consumer<Result<Pagamento>> destino) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("metodo", metodo);
        api.checkout(pedidoId, corpo).enqueue(ApiCallback.autenticado(sessao, destino));
    }

    public void iniciarSecagem(String pedidoId, Consumer<Result<Pedido>> destino) {
        api.iniciarSecagem(pedidoId).enqueue(ApiCallback.autenticado(sessao, destino));
    }

    public void historico(int pagina, int tamanho, Consumer<Result<Pagina<Pedido>>> destino) {
        api.historico(pagina, tamanho).enqueue(ApiCallback.autenticado(sessao, destino));
    }
}
