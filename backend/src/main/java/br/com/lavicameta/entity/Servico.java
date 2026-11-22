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

@Entity
@Table(name = "servicos")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoServico nome;

    @Column(nullable = false, length = 200)
    private String descricao;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal preco;

    @Column(name = "duracao_min", nullable = false)
    private int duracaoMin;

    protected Servico() {
    }

    public Servico(TipoServico nome, String descricao, BigDecimal preco, int duracaoMin) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.duracaoMin = duracaoMin;
    }

    public void atualizar(String descricao, BigDecimal preco, int duracaoMin) {
        this.descricao = descricao;
        this.preco = preco;
        this.duracaoMin = duracaoMin;
    }

    public Integer getId() { return id; }
    public TipoServico getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public BigDecimal getPreco() { return preco; }
    public int getDuracaoMin() { return duracaoMin; }
}
