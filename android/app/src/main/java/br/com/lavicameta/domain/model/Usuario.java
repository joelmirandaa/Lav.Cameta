package br.com.lavicameta.domain.model;

public final class Usuario {
    public String id;
    public String nome;
    public String email;
    public String telefone;
    public String tipo;
    public boolean possuiSenha;
    public boolean vinculadoGoogle;

    public boolean isAdmin() {
        return "ADMIN".equals(tipo);
    }
}
