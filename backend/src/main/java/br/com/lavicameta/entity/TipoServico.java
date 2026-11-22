package br.com.lavicameta.entity;

public enum TipoServico {
    LAVAR,
    SECAR,
    LAVAR_SECAR;

    /** Tipo de máquina em que o serviço é contratado (a primeira etapa do combo é a lavagem). */
    public TipoMaquina tipoMaquinaInicial() {
        return this == SECAR ? TipoMaquina.SECADORA : TipoMaquina.LAVADORA;
    }

    public EtapaCiclo etapaInicial() {
        return this == SECAR ? EtapaCiclo.SECAGEM : EtapaCiclo.LAVAGEM;
    }

    public boolean incluiSecagemPosterior() {
        return this == LAVAR_SECAR;
    }
}
