package br.com.lavicameta.domain.model;

import java.util.List;

public final class Pagina<T> {
    public List<T> itens;
    public int pagina;
    public int tamanho;
    public long total;
    public boolean temProxima;
}
