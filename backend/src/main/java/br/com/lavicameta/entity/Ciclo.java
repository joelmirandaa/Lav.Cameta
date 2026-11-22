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
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ciclos")
public class Ciclo {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "maquina_id", nullable = false)
    private Maquina maquina;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtapaCiclo etapa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCiclo status;

    @Column(name = "inicio_em", nullable = false)
    private Instant inicioEm;

    @Column(name = "fim_previsto_em", nullable = false)
    private Instant fimPrevistoEm;

    @Column(name = "fim_em")
    private Instant fimEm;

    protected Ciclo() {
    }

    public Ciclo(Pedido pedido, Maquina maquina, EtapaCiclo etapa, int duracaoMin, Instant agora) {
        this.id = UUID.randomUUID().toString();
        this.pedido = pedido;
        this.maquina = maquina;
        this.etapa = etapa;
        this.status = StatusCiclo.EM_ANDAMENTO;
        this.inicioEm = agora;
        this.fimPrevistoEm = agora.plus(Duration.ofMinutes(duracaoMin));
    }

    public int minutosRestantes(Instant agora) {
        if (status != StatusCiclo.EM_ANDAMENTO || !fimPrevistoEm.isAfter(agora)) {
            return 0;
        }
        long segundos = Duration.between(agora, fimPrevistoEm).getSeconds();
        return (int) Math.ceil(segundos / 60.0);
    }

    public void concluir(Instant agora) {
        this.status = StatusCiclo.CONCLUIDO;
        this.fimEm = agora;
    }

    public void falhar(Instant agora) {
        this.status = StatusCiclo.FALHOU;
        this.fimEm = agora;
    }

    public String getId() { return id; }
    public Pedido getPedido() { return pedido; }
    public Maquina getMaquina() { return maquina; }
    public EtapaCiclo getEtapa() { return etapa; }
    public StatusCiclo getStatus() { return status; }
    public Instant getInicioEm() { return inicioEm; }
    public Instant getFimPrevistoEm() { return fimPrevistoEm; }
    public Instant getFimEm() { return fimEm; }
}
