package br.com.lavicameta.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import br.com.lavicameta.R;
import br.com.lavicameta.databinding.ActivityAdminReportsBinding;
import br.com.lavicameta.databinding.DialogPeriodBinding;
import br.com.lavicameta.domain.model.Relatorio;
import br.com.lavicameta.ui.component.ChartView;
import br.com.lavicameta.ui.component.EstadoView;
import br.com.lavicameta.ui.component.StatusBadge;
import br.com.lavicameta.util.Formatters;
import br.com.lavicameta.viewmodel.AdminReportsViewModel;
import androidx.core.content.ContextCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;

/** T06 Relatórios – Mês e T07 Relatórios – Ano (RF18–RF21). */
public class AdminReportsActivity extends BaseActivity {

    private ActivityAdminReportsBinding b;
    private AdminReportsViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!container().sessao.isAdmin()) {
            irParaLogin(null);
            return;
        }
        b = ActivityAdminReportsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        configurarToolbar(b.barra.toolbar, getString(R.string.relatorios_titulo), false);
        b.barra.toolbar.inflateMenu(R.menu.menu_admin);
        b.barra.toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.acao_periodo) {
                escolherPeriodo();
            } else if (id == R.id.acao_maquinas) {
                startActivity(new Intent(this, AdminMachinesActivity.class));
            } else if (id == R.id.acao_precos) {
                startActivity(new Intent(this, AdminPricesActivity.class));
            } else if (id == R.id.acao_sair) {
                container().auth.sair();
                startActivity(new Intent(this, AdminLoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
            return true;
        });
        vm = viewModel(AdminReportsViewModel.class);
        EstadoView estado = new EstadoView(b.getRoot());

        TabLayout.Tab aba = b.abas.getTabAt(AdminReportsViewModel.ANO.equals(vm.periodo()) ? 1 : 0);
        if (aba != null) {
            aba.select();
        }
        b.abas.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vm.alterarPeriodo(tab.getPosition() == 1 ? AdminReportsViewModel.ANO : AdminReportsViewModel.MES);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        b.periodo.setOnClickListener(v -> escolherPeriodo());
        b.atualizar.setColorSchemeResources(R.color.agua);
        b.atualizar.setOnRefreshListener(vm::carregar);

        vm.relatorio().observe(this, resultado -> {
            b.atualizar.setRefreshing(false);
            b.periodo.setText(rotuloPeriodo());
            if (resultado.isLoading()) {
                estado.carregando();
                b.conteudo.setVisibility(View.GONE);
            } else if (resultado.isError()) {
                estado.erro(resultado.message, vm::carregar);
                b.conteudo.setVisibility(View.GONE);
            } else {
                estado.esconder();
                b.conteudo.setVisibility(View.VISIBLE);
                mostrar(resultado.data);
            }
        });
        if (vm.relatorio().getValue() == null) {
            vm.carregar();
        }
    }

    private String rotuloPeriodo() {
        if (AdminReportsViewModel.ANO.equals(vm.periodo())) {
            return String.valueOf(vm.ano());
        }
        String mes = Month.of(vm.mes()).getDisplayName(TextStyle.FULL, Formatters.PT_BR);
        return Character.toUpperCase(mes.charAt(0)) + mes.substring(1) + " de " + vm.ano();
    }

    private void mostrar(Relatorio r) {
        boolean mensal = "MES".equals(r.periodo);
        b.rotuloGanhos.setText(mensal ? R.string.ganhos_mes : R.string.ganhos_ano);
        b.ganhos.setText(Formatters.moeda(r.ganhos));
        String variacao = Formatters.percentual(r.variacaoPercentual);
        if (variacao == null) {
            StatusBadge.aplicar(b.variacao, getString(R.string.variacao_sem_base), cores(R.color.texto_secundario, R.color.fundo_secao));
        } else {
            boolean alta = r.variacaoPercentual.signum() >= 0;
            StatusBadge.aplicar(b.variacao, getString(mensal ? R.string.variacao_vs_mes : R.string.variacao_vs_ano, variacao),
                    alta ? cores(R.color.sucesso, R.color.sucesso_fundo) : cores(R.color.erro, R.color.erro_fundo));
        }
        b.tituloGrafico.setText(mensal ? R.string.grafico_semanas : R.string.grafico_meses);
        b.grafico.definir(r.serie, mensal ? ChartView.Tipo.LINHA : ChartView.Tipo.BARRAS);
        b.tituloResumo.setText(mensal ? R.string.resumo_mes : R.string.resumo_ano);
        b.rotuloTotal.setText(mensal ? R.string.total_mes : R.string.total_ano);
        b.total.setText(Formatters.moeda(r.totalPeriodo));
        b.pagamentos.setText(getString(R.string.pagamentos_quantidade,
                Formatters.moeda(r.totalPagamentosConfirmados), (int) r.quantidadePagamentos));
        b.atualizadoEm.setText(getString(R.string.atualizado_em, Formatters.dataHora(r.atualizadoEm)));
    }

    private int[] cores(int texto, int fundo) {
        return new int[] {ContextCompat.getColor(this, texto), ContextCompat.getColor(this, fundo)};
    }

    /** UC13/UC14 alternativo: escolher outro período pelo calendário. */
    private void escolherPeriodo() {
        DialogPeriodBinding d = DialogPeriodBinding.inflate(LayoutInflater.from(this));
        int anoAtual = LocalDate.now(Formatters.ZONA).getYear();
        String[] meses = new String[12];
        for (int i = 0; i < 12; i++) {
            meses[i] = Month.of(i + 1).getDisplayName(TextStyle.SHORT, Formatters.PT_BR);
        }
        d.mes.setMinValue(1);
        d.mes.setMaxValue(12);
        d.mes.setDisplayedValues(meses);
        d.mes.setValue(vm.mes());
        d.ano.setMinValue(anoAtual - 10);
        d.ano.setMaxValue(anoAtual);
        d.ano.setValue(Math.min(vm.ano(), anoAtual));
        d.ano.setWrapSelectorWheel(false);
        d.grupoMes.setVisibility(AdminReportsViewModel.ANO.equals(vm.periodo()) ? View.GONE : View.VISIBLE);
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.escolher_periodo)
                .setView(d.getRoot())
                .setNegativeButton(R.string.acao_cancelar, null)
                .setPositiveButton(R.string.acao_ok, (dialogo, w) -> vm.escolherData(d.ano.getValue(), d.mes.getValue()))
                .show();
    }
}
