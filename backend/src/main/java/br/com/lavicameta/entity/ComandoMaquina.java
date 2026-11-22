package br.com.lavicameta.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "comandos_maquina")
public class ComandoMaquina {

    public enum Tipo { INICIAR }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ciclo_id", length = 36)
    private String cicloId;

    @Column(name = "maquina_id", nullable = false)
    private Integer maquinaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tipo comando;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusComando status;

    @Column(name = "chave_idempotencia", nullable = false, length = 120)
    private String chaveIdempotencia;

    @Column(length = 300)
    private String erro;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected ComandoMaquina() {
    }

    public ComandoMaquina(String cicloId, Integer maquinaId, Tipo comando, StatusComando status,
                          String chaveIdempotencia, String erro, Instant agora) {
        this.cicloId = cicloId;
        this.maquinaId = maquinaId;
        this.comando = comando;
        this.status = status;
        this.chaveIdempotencia = chaveIdempotencia;
        this.erro = erro == null ? null : erro.substring(0, Math.min(erro.length(), 300));
        this.criadoEm = agora;
    }
}
