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
import java.util.UUID;

@Entity
@Table(name = "pagamentos")
public class Pagamento {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPagamento metodo;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status;

    @Column(nullable = false, length = 30)
    private String provedor;

    @Column(name = "codigo_pix", columnDefinition = "text")
    private String codigoPix;

    @Column(name = "checkout_url", length = 500)
    private String checkoutUrl;

    @Column(name = "transacao_id", length = 100)
    private String transacaoId;

    @Column(length = 255)
    private String motivo;

    @Column(name = "pago_em")
    private Instant pagoEm;

    @Column(name = "estornado_em")
    private Instant estornadoEm;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected Pagamento() {
    }

    public Pagamento(Pedido pedido, MetodoPagamento metodo, String provedor, Instant agora) {
        this.id = UUID.randomUUID().toString();
        this.pedido = pedido;
        this.metodo = metodo;
        this.valor = pedido.getValorTotal();
        this.provedor = provedor;
        this.status = StatusPagamento.PENDENTE;
        this.criadoEm = agora;
        this.atualizadoEm = agora;
    }

    public void registrarCobranca(String transacaoId, String codigoPix, String checkoutUrl, Instant agora) {
        this.transacaoId = transacaoId;
        this.codigoPix = codigoPix;
        this.checkoutUrl = checkoutUrl;
        this.atualizadoEm = agora;
    }

    public boolean pendente() {
        return status == StatusPagamento.PENDENTE;
    }

    public void aprovar(Instant pagoEm, Instant agora) {
        this.status = StatusPagamento.APROVADO;
        this.pagoEm = pagoEm;
        this.atualizadoEm = agora;
    }

    public void recusar(String motivo, Instant agora) {
        this.status = StatusPagamento.RECUSADO;
        this.motivo = motivo;
        this.atualizadoEm = agora;
    }

    public void expirar(Instant agora) {
        this.status = StatusPagamento.EXPIRADO;
        this.motivo = "Prazo de pagamento encerrado";
        this.atualizadoEm = agora;
    }

    public void solicitarEstorno(String motivo, Instant agora) {
        this.status = StatusPagamento.ESTORNO_PENDENTE;
        this.motivo = motivo;
        this.atualizadoEm = agora;
    }

    public void confirmarEstorno(Instant agora) {
        this.status = StatusPagamento.ESTORNADO;
        this.estornadoEm = agora;
        this.atualizadoEm = agora;
    }

    public String getId() { return id; }
    public Pedido getPedido() { return pedido; }
    public MetodoPagamento getMetodo() { return metodo; }
    public BigDecimal getValor() { return valor; }
    public StatusPagamento getStatus() { return status; }
    public String getProvedor() { return provedor; }
    public String getCodigoPix() { return codigoPix; }
    public String getCheckoutUrl() { return checkoutUrl; }
    public String getTransacaoId() { return transacaoId; }
    public String getMotivo() { return motivo; }
    public Instant getPagoEm() { return pagoEm; }
    public Instant getCriadoEm() { return criadoEm; }
}
