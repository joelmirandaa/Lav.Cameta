package br.com.lavicameta.domain.model;

import java.util.List;

public final class Carteira {
    public List<Pagamento> pagamentos;
    public List<MetodoSalvo> metodosSalvos;

    public static final class MetodoSalvo {
        public String id;
        public String bandeira;
        public String ultimos4;
        public String criadoEm;
    }
}
