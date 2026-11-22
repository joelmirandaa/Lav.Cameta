package br.com.lavicameta.domain.model;

import java.math.BigDecimal;
import java.util.List;

public final class Pedido {
    public static final String AGUARDANDO_PAGAMENTO = "AGUARDANDO_PAGAMENTO";
    public static final String PAGO = "PAGO";
    public static final String EM_ANDAMENTO = "EM_ANDAMENTO";
    public static final String CONCLUIDO = "CONCLUIDO";

    public String id;
    public String status;
    public String servico;
    public String servicoDescricao;
    public BigDecimal valorTotal;
    public Integer maquinaId;
    public String maquinaNome;
    public String criadoEm;
    public String expiraEm;
    public long segundosParaExpirar;
    public Pagamento pagamento;
    public List<Ciclo> ciclos;
    public boolean podeIniciarSecagem;

    public boolean aguardandoPagamento() {
        return AGUARDANDO_PAGAMENTO.equals(status);
    }

    public boolean pagoOuEmAndamento() {
        return PAGO.equals(status) || EM_ANDAMENTO.equals(status) || CONCLUIDO.equals(status);
    }

    public Ciclo cicloAtual() {
        if (ciclos == null || ciclos.isEmpty()) {
            return null;
        }
        return ciclos.get(ciclos.size() - 1);
    }
}
