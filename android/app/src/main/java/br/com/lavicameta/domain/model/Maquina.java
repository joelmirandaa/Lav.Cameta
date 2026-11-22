package br.com.lavicameta.domain.model;

import java.math.BigDecimal;
import java.util.List;

public final class Maquina {
    public static final String LAVADORA = "LAVADORA";
    public static final String DISPONIVEL = "DISPONIVEL";
    public static final String EM_USO = "EM_USO";
    public static final String MANUTENCAO = "MANUTENCAO";

    public int id;
    public String nome;
    public String tipo;
    public BigDecimal capacidadeKg;
    public String status;
    public Integer minutosRestantes;
    public boolean reservada;
    public boolean contratavel;
    public List<String> recursos;

    public boolean isLavadora() {
        return LAVADORA.equals(tipo);
    }
}
