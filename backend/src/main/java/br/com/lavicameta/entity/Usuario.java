package br.com.lavicameta.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(length = 150)
    private String email;

    @Column(length = 20)
    private String telefone;

    @Column(name = "senha_hash")
    private String senhaHash;

    @Column(name = "google_id", length = 100)
    private String googleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipo;

    @Column(name = "tentativas_login", nullable = false)
    private int tentativasLogin;

    @Column(name = "bloqueado_ate")
    private Instant bloqueadoAte;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected Usuario() {
    }

    public Usuario(String nome, String email, String telefone, TipoUsuario tipo, Instant agora) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.tipo = tipo;
        this.criadoEm = agora;
        this.atualizadoEm = agora;
    }

    public boolean estaBloqueado(Instant agora) {
        return bloqueadoAte != null && bloqueadoAte.isAfter(agora);
    }

    /** Registra falha de login; ao atingir o limite, bloqueia temporariamente (UC02/CT03). */
    public void registrarFalhaLogin(int maxTentativas, Duration bloqueio, Instant agora) {
        tentativasLogin++;
        if (tentativasLogin >= maxTentativas) {
            bloqueadoAte = agora.plus(bloqueio);
            tentativasLogin = 0;
        }
        atualizadoEm = agora;
    }

    public void registrarLoginComSucesso(Instant agora) {
        if (tentativasLogin != 0 || bloqueadoAte != null) {
            tentativasLogin = 0;
            bloqueadoAte = null;
            atualizadoEm = agora;
        }
    }

    public void definirSenhaHash(String senhaHash, Instant agora) {
        this.senhaHash = senhaHash;
        this.tentativasLogin = 0;
        this.bloqueadoAte = null;
        this.atualizadoEm = agora;
    }

    public void vincularGoogle(String googleId, Instant agora) {
        this.googleId = googleId;
        this.atualizadoEm = agora;
    }

    public void atualizarPerfil(String nome, String email, String telefone, Instant agora) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.atualizadoEm = agora;
    }

    public boolean isAdmin() {
        return tipo == TipoUsuario.ADMIN;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getSenhaHash() { return senhaHash; }
    public String getGoogleId() { return googleId; }
    public TipoUsuario getTipo() { return tipo; }
    public int getTentativasLogin() { return tentativasLogin; }
    public Instant getBloqueadoAte() { return bloqueadoAte; }
    public Instant getCriadoEm() { return criadoEm; }
}
