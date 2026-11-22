package br.com.lavicameta.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

/** Cartão tokenizado pelo gateway (RNF06/RN12): nunca guarda número completo. */
@Entity
@Table(name = "metodos_pagamento")
public class MetodoPagamentoSalvo {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "token_gateway", nullable = false, length = 150)
    private String tokenGateway;

    @Column(length = 20)
    private String bandeira;

    @Column(length = 4, columnDefinition = "char(4)")
    private String ultimos4;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected MetodoPagamentoSalvo() {
    }

    public String getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public String getBandeira() { return bandeira; }
    public String getUltimos4() { return ultimos4; }
    public Instant getCriadoEm() { return criadoEm; }
}
