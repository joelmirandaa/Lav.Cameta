package br.com.lavicameta.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class RelatorioServiceTest {

    @Test
    void variacaoSegueRn10ConformeCt22() {
        assertEquals(new BigDecimal("12.5"), RelatorioService.variacao(new BigDecimal("1247.50"), new BigDecimal("1108.90")));
    }

    @Test
    void variacaoNegativa() {
        assertEquals(new BigDecimal("-50.0"), RelatorioService.variacao(new BigDecimal("50"), new BigDecimal("100")));
    }

    @Test
    void semBaseComparavelQuandoPeriodoAnteriorZerado() {
        assertNull(RelatorioService.variacao(new BigDecimal("10"), BigDecimal.ZERO));
    }
}
