package br.com.lavicameta.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
public class Pedido {

    /** Pedidos cujo valor compõe os ganhos (RN09). */
    public static final Set<StatusPedido> STATUS_PAGOS =
            EnumSet.of(StatusPedido.PAGO, StatusPedido.EM_ANDAMENTO, StatusPedido.CONCLUIDO);

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "maquina_id", nullable = false)
    private Maquina maquina;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    @Column(name = "valor_total", nullable = false, precision = 8, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "expira_em", nullable = false)
    private Instant expiraEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected Pedido() {
    }

    /** Cria o pedido com o preço vigente fixado (RF09) e a reserva de 5 minutos (RN03). */
    public Pedido(Usuario usuario, Maquina maquina, Servico servico, Instant agora, Instant expiraEm) {
        this.id = UUID.randomUUID().toString();
        this.usuario = usuario;
        this.maquina = maquina;
        this.servico = servico;
        this.valorTotal = servico.getPreco();
        this.status = StatusPedido.AGUARDANDO_PAGAMENTO;
        this.criadoEm = agora;
        this.expiraEm = expiraEm;
        this.atualizadoEm = agora;
    }

    public boolean aguardandoPagamento() {
        return status == StatusPedido.AGUARDANDO_PAGAMENTO;
    }

    public boolean reservaVigente(Instant agora) {
        return aguardandoPagamento() && expiraEm.isAfter(agora);
    }

    public void confirmarPagamento(Instant agora) {
        exigirStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
        mudarPara(StatusPedido.PAGO, agora);
    }

    public void iniciarAndamento(Instant agora) {
        mudarPara(StatusPedido.EM_ANDAMENTO, agora);
    }

    public void concluir(Instant agora) {
        mudarPara(StatusPedido.CONCLUIDO, agora);
    }

    public void cancelar(Instant agora) {
        exigirStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
        mudarPara(StatusPedido.CANCELADO, agora);
    }

    public void expirar(Instant agora) {
        exigirStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
        mudarPara(StatusPedido.EXPIRADO, agora);
    }

    public void estornar(Instant agora) {
        mudarPara(StatusPedido.ESTORNADO, agora);
    }

    private void exigirStatus(StatusPedido esperado) {
        if (status != esperado) {
            throw new IllegalStateException("Pedido " + id + " está em " + status + ", esperado " + esperado);
        }
    }

    private void mudarPara(StatusPedido novo, Instant agora) {
        this.status = novo;
        this.atualizadoEm = agora;
    }

    public String getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Maquina getMaquina() { return maquina; }
    public Servico getServico() { return servico; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public StatusPedido getStatus() { return status; }
    public Instant getCriadoEm() { return criadoEm; }
    public Instant getExpiraEm() { return expiraEm; }
}
