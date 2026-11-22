package br.com.lavicameta.domain.model;

public final class Ciclo {
    public static final String EM_ANDAMENTO = "EM_ANDAMENTO";

    public String id;
    public String etapa;
    public String status;
    public Integer maquinaId;
    public String maquinaNome;
    public String inicioEm;
    public String fimPrevistoEm;
    public String fimEm;
    public int minutosRestantes;

    public boolean emAndamento() {
        return EM_ANDAMENTO.equals(status);
    }
}
