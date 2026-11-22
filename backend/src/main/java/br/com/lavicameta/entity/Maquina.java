package br.com.lavicameta.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "maquinas")
public class Maquina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMaquina tipo;

    @Column(name = "capacidade_kg", nullable = false, precision = 4, scale = 1)
    private BigDecimal capacidadeKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMaquina status;

    @Column(name = "minutos_restantes")
    private Integer minutosRestantes;

    @Column(length = 200)
    private String recursos;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected Maquina() {
    }

    public Maquina(String nome, TipoMaquina tipo, BigDecimal capacidadeKg, String recursos, Instant agora) {
        this.nome = nome;
        this.tipo = tipo;
        this.capacidadeKg = capacidadeKg;
        this.recursos = recursos;
        this.status = StatusMaquina.DISPONIVEL;
        this.criadoEm = agora;
        this.atualizadoEm = agora;
    }

    public void atualizarCadastro(String nome, TipoMaquina tipo, BigDecimal capacidadeKg, String recursos, Instant agora) {
        this.nome = nome;
        this.tipo = tipo;
        this.capacidadeKg = capacidadeKg;
        this.recursos = recursos;
        this.atualizadoEm = agora;
    }

    public void iniciarCiclo(int minutos, Instant agora) {
        this.status = StatusMaquina.EM_USO;
        this.minutosRestantes = minutos;
        this.atualizadoEm = agora;
    }

    public void atualizarMinutosRestantes(int minutos, Instant agora) {
        this.minutosRestantes = minutos;
        this.atualizadoEm = agora;
    }

    public void finalizarCiclo(Instant agora) {
        this.status = StatusMaquina.DISPONIVEL;
        this.minutosRestantes = null;
        this.atualizadoEm = agora;
    }

    public void alterarStatus(StatusMaquina novoStatus, Instant agora) {
        this.status = novoStatus;
        if (novoStatus != StatusMaquina.EM_USO) {
            this.minutosRestantes = null;
        }
        this.atualizadoEm = agora;
    }

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public TipoMaquina getTipo() { return tipo; }
    public BigDecimal getCapacidadeKg() { return capacidadeKg; }
    public StatusMaquina getStatus() { return status; }
    public Integer getMinutosRestantes() { return minutosRestantes; }
    public String getRecursos() { return recursos; }
}
