package br.com.lavicameta.data.repository;

import br.com.lavicameta.data.local.SessionStore;
import br.com.lavicameta.data.remote.ApiCallback;
import br.com.lavicameta.data.remote.ApiService;
import br.com.lavicameta.domain.model.Maquina;
import br.com.lavicameta.domain.model.Relatorio;
import br.com.lavicameta.domain.model.Servico;
import br.com.lavicameta.util.Result;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/** Relatórios e gestão de máquinas/preços (área administrativa). */
public class AdminRepository {

    private final ApiService api;
    private final SessionStore sessao;

    public AdminRepository(ApiService api, SessionStore sessao) {
        this.api = api;
        this.sessao = sessao;
    }

    public void relatorio(String periodo, int ano, Integer mes, Consumer<Result<Relatorio>> destino) {
        api.relatorio(periodo, ano, mes).enqueue(ApiCallback.autenticado(sessao, destino));
    }

    public void maquinas(Consumer<Result<List<Maquina>>> destino) {
        api.adminMaquinas().enqueue(ApiCallback.autenticado(sessao, destino));
    }

    public void salvarMaquina(Integer id, String nome, String tipo, BigDecimal capacidade, String recursos,
                              Consumer<Result<Maquina>> destino) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("nome", nome);
        corpo.put("tipo", tipo);
        corpo.put("capacidadeKg", capacidade);
        corpo.put("recursos", AuthRepository.vazioParaNulo(recursos));
        if (id == null) {
            api.criarMaquina(corpo).enqueue(ApiCallback.autenticado(sessao, destino));
        } else {
            api.atualizarMaquina(id, corpo).enqueue(ApiCallback.autenticado(sessao, destino));
        }
    }

    public void alterarStatus(int id, String status, Consumer<Result<Maquina>> destino) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("status", status);
        api.alterarStatusMaquina(id, corpo).enqueue(ApiCallback.autenticado(sessao, destino));
    }

    public void servicos(Consumer<Result<List<Servico>>> destino) {
        api.adminServicos().enqueue(ApiCallback.autenticado(sessao, destino));
    }

    public void atualizarServico(Servico servico, BigDecimal preco, int duracao, Consumer<Result<Servico>> destino) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("descricao", servico.descricao);
        corpo.put("preco", preco);
        corpo.put("duracaoMin", duracao);
        api.atualizarServico(servico.id, corpo).enqueue(ApiCallback.autenticado(sessao, destino));
    }
}
