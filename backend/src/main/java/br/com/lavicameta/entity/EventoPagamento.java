package br.com.lavicameta.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "eventos_pagamento")
public class EventoPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String provedor;

    @Column(name = "evento_id", nullable = false, length = 100)
    private String eventoId;

    @Column(name = "transacao_id", nullable = false, length = 100)
    private String transacaoId;

    @Column(nullable = false, length = 40)
    private String tipo;

    @Column(name = "recebido_em", nullable = false)
    private Instant recebidoEm;

    protected EventoPagamento() {
    }

    public EventoPagamento(String provedor, String eventoId, String transacaoId, String tipo, Instant recebidoEm) {
        this.provedor = provedor;
        this.eventoId = eventoId;
        this.transacaoId = transacaoId;
        this.tipo = tipo;
        this.recebidoEm = recebidoEm;
    }
}
