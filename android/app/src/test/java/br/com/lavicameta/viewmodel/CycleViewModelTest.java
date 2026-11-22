package br.com.lavicameta.viewmodel;

import static org.junit.Assert.assertNotEquals;

import br.com.lavicameta.domain.model.Ciclo;
import br.com.lavicameta.domain.model.Pedido;
import java.util.ArrayList;
import org.junit.Test;

public class CycleViewModelTest {

    @Test
    public void mudancaDeEtapaAlteraChaveDeEstado() {
        Pedido p = new Pedido();
        p.status = Pedido.EM_ANDAMENTO;
        p.ciclos = new ArrayList<>();
        Ciclo lavagem = new Ciclo();
        lavagem.etapa = "LAVAGEM";
        lavagem.status = Ciclo.EM_ANDAMENTO;
        p.ciclos.add(lavagem);
        String antes = CycleViewModel.chaveEstado(p);
        lavagem.status = "CONCLUIDO";
        p.podeIniciarSecagem = true;
        assertNotEquals(antes, CycleViewModel.chaveEstado(p));
    }
}
