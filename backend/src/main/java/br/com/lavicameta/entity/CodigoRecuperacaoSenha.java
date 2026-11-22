package br.com.lavicameta.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "codigos_recuperacao_senha")
public class CodigoRecuperacaoSenha {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "codigo_hash", nullable = false, length = 64, columnDefinition = "char(64)")
    private String codigoHash;

    @Column(nullable = false)
    private int tentativas;

    @Column(name = "expira_em", nullable = false)
    private Instant expiraEm;

    @Column(name = "usado_em")
    private Instant usadoEm;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected CodigoRecuperacaoSenha() {
    }

    public CodigoRecuperacaoSenha(Usuario usuario, String codigoHash, Instant expiraEm, Instant agora) {
        this.id = UUID.randomUUID().toString();
        this.usuario = usuario;
        this.codigoHash = codigoHash;
        this.expiraEm = expiraEm;
        this.criadoEm = agora;
    }

    public boolean podeSerUsado(Instant agora, int maxTentativas) {
        return usadoEm == null && expiraEm.isAfter(agora) && tentativas < maxTentativas;
    }

    public void registrarTentativaInvalida() {
        tentativas++;
    }

    public void marcarUsado(Instant agora) {
        usadoEm = agora;
    }

    public void invalidar(Instant agora) {
        if (usadoEm == null) {
            usadoEm = agora;
        }
    }

    public Usuario getUsuario() { return usuario; }
    public String getCodigoHash() { return codigoHash; }
    public Instant getCriadoEm() { return criadoEm; }
}
