package br.com.lavicameta.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import org.junit.Test;

public class FormattersTest {

    @Test
    public void moedaEmReais() {
        assertEquals("R$ 18,00", Formatters.moeda(new BigDecimal("18")));
        assertEquals("R$ 1.247,50", Formatters.moeda(new BigDecimal("1247.5")));
    }

    @Test
    public void percentualComSinal() {
        assertEquals("+12,5%", Formatters.percentual(new BigDecimal("12.5")));
        assertEquals("−3,0%", Formatters.percentual(new BigDecimal("-3")));
        assertNull(Formatters.percentual(null));
    }

    @Test
    public void dataNoFusoDeBelem() {
        assertEquals("21/11/2026 às 21:30", Formatters.dataHora("2026-11-22T00:30:00Z"));
        assertEquals("4:05", Formatters.contagem(245));
    }
}
