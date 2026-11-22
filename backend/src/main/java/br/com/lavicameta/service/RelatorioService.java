package br.com.lavicameta.service;

import br.com.lavicameta.config.LavicametaProperties;
import br.com.lavicameta.dto.RelatorioDtos.Periodo;
import br.com.lavicameta.dto.RelatorioDtos.PontoSerie;
import br.com.lavicameta.dto.RelatorioDtos.RelatorioResponse;
import br.com.lavicameta.entity.Pedido;
import br.com.lavicameta.exception.ApiException;
import br.com.lavicameta.repository.PagamentoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Relatórios financeiros mensal e anual do administrador (RF18–RF21, RN09–RN11). */
@Service
public class RelatorioService {

    private static final String[] MESES = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
    private static final int DIAS_SEMANA = 7;

    private final PagamentoRepository pagamentos;
    private final Clock clock;
    private final ZoneId zona;

    public RelatorioService(PagamentoRepository pagamentos, Clock clock, LavicametaProperties propriedades) {
        this.pagamentos = pagamentos;
        this.clock = clock;
        this.zona = propriedades.zona();
    }

    @Transactional(readOnly = true)
    public RelatorioResponse gerar(Periodo periodo, Integer ano, Integer mes) {
        LocalDate hoje = LocalDate.now(clock.withZone(zona));
        int anoRef = ano == null ? hoje.getYear() : ano;
        if (anoRef < 2000 || anoRef > hoje.getYear() + 1) {
            throw ApiException.invalido("PERIODO_INVALIDO", "Ano inválido.");
        }
        if (periodo == Periodo.ANO) {
            return anual(anoRef);
        }
        int mesRef = mes == null ? hoje.getMonthValue() : mes;
        if (mesRef < 1 || mesRef > 12) {
            throw ApiException.invalido("PERIODO_INVALIDO", "Mês inválido.");
        }
        return mensal(YearMonth.of(anoRef, mesRef));
    }

    private RelatorioResponse mensal(YearMonth mes) {
        Instant inicio = inicio(mes.atDay(1));
        Instant fim = inicio(mes.plusMonths(1).atDay(1));
        int semanas = (mes.lengthOfMonth() + DIAS_SEMANA - 1) / DIAS_SEMANA;
        BigDecimal[] porSemana = zeros(semanas);
        BigDecimal ganhos = BigDecimal.ZERO;
        for (Object[] linha : pagamentos.listarGanhos(inicio, fim, Pedido.STATUS_PAGOS)) {
            int dia = ((Instant) linha[0]).atZone(zona).getDayOfMonth();
            BigDecimal valor = (BigDecimal) linha[1];
            porSemana[(dia - 1) / DIAS_SEMANA] = porSemana[(dia - 1) / DIAS_SEMANA].add(valor);
            ganhos = ganhos.add(valor);
        }
        List<PontoSerie> serie = new ArrayList<>();
        for (int i = 0; i < semanas; i++) {
            serie.add(new PontoSerie("Sem " + (i + 1), porSemana[i]));
        }
        YearMonth anterior = mes.minusMonths(1);
        BigDecimal ganhosAnterior = somarGanhos(inicio(anterior.atDay(1)), inicio);
        return montar(Periodo.MES, mes.getYear(), mes.getMonthValue(), ganhos, ganhosAnterior, serie, inicio, fim);
    }

    private RelatorioResponse anual(int ano) {
        Instant inicio = inicio(LocalDate.of(ano, 1, 1));
        Instant fim = inicio(LocalDate.of(ano + 1, 1, 1));
        BigDecimal[] porMes = zeros(12);
        BigDecimal ganhos = BigDecimal.ZERO;
        for (Object[] linha : pagamentos.listarGanhos(inicio, fim, Pedido.STATUS_PAGOS)) {
            int mes = ((Instant) linha[0]).atZone(zona).getMonthValue();
            BigDecimal valor = (BigDecimal) linha[1];
            porMes[mes - 1] = porMes[mes - 1].add(valor);
            ganhos = ganhos.add(valor);
        }
        List<PontoSerie> serie = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            serie.add(new PontoSerie(MESES[i], porMes[i]));
        }
        BigDecimal ganhosAnterior = somarGanhos(inicio(LocalDate.of(ano - 1, 1, 1)), inicio);
        return montar(Periodo.ANO, ano, null, ganhos, ganhosAnterior, serie, inicio, fim);
    }

    private RelatorioResponse montar(Periodo periodo, int ano, Integer mes, BigDecimal ganhos, BigDecimal anterior,
                                     List<PontoSerie> serie, Instant inicio, Instant fim) {
        return new RelatorioResponse(periodo, ano, mes, ganhos, anterior, variacao(ganhos, anterior), serie, ganhos,
                pagamentos.somarConfirmados(inicio, fim), pagamentos.contarConfirmados(inicio, fim), clock.instant());
    }

    /** RN10: (atual − anterior) / anterior × 100; nula quando não há base comparável. */
    static BigDecimal variacao(BigDecimal atual, BigDecimal anterior) {
        if (anterior == null || anterior.signum() == 0) {
            return null;
        }
        return atual.subtract(anterior).multiply(BigDecimal.valueOf(100)).divide(anterior, 1, RoundingMode.HALF_UP);
    }

    private BigDecimal somarGanhos(Instant inicio, Instant fim) {
        return pagamentos.listarGanhos(inicio, fim, Pedido.STATUS_PAGOS).stream()
                .map(l -> (BigDecimal) l[1])
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Instant inicio(LocalDate data) {
        return ZonedDateTime.of(data.atStartOfDay(), zona).toInstant();
    }

    private static BigDecimal[] zeros(int tamanho) {
        BigDecimal[] valores = new BigDecimal[tamanho];
        Arrays.fill(valores, BigDecimal.ZERO);
        return valores;
    }
}
