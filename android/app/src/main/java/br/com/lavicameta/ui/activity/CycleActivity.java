package br.com.lavicameta.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import br.com.lavicameta.R;
import br.com.lavicameta.databinding.ActivityCycleBinding;
import br.com.lavicameta.domain.model.Ciclo;
import br.com.lavicameta.domain.model.Pedido;
import br.com.lavicameta.ui.component.EstadoView;
import br.com.lavicameta.ui.component.Ui;
import br.com.lavicameta.util.Formatters;
import br.com.lavicameta.util.NotificationHelper;
import br.com.lavicameta.util.UiText;
import br.com.lavicameta.viewmodel.CycleViewModel;
import java.time.Duration;
import java.time.Instant;

/** Acompanhamento do ciclo (UC08): tempo restante, aviso de término e secagem do combo. */
public class CycleActivity extends BaseActivity {

    public static final String EXTRA_PEDIDO_ID = "pedidoId";

    private ActivityCycleBinding b;
    private CycleViewModel vm;
    private String pedidoId;
    private final ActivityResultLauncher<String> permissao =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), concedida -> { });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityCycleBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        configurarToolbar(b.barra.toolbar, getString(R.string.ciclo_titulo), true);
        pedidoId = getIntent().getStringExtra(EXTRA_PEDIDO_ID);
        vm = viewModel(CycleViewModel.class);
        EstadoView estado = new EstadoView(b.getRoot());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            permissao.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
        b.voltarInicio.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
        b.iniciarSecagem.setOnClickListener(v -> {
            Ui.mensagem(b.erro, null);
            vm.iniciarSecagem();
        });
        vm.pedido().observe(this, resultado -> {
            if (resultado.isLoading()) {
                estado.carregando();
                b.rolagem.setVisibility(View.GONE);
            } else if (resultado.isError()) {
                estado.erro(resultado.message, () -> vm.iniciar(pedidoId));
                b.rolagem.setVisibility(View.GONE);
            } else {
                estado.esconder();
                b.rolagem.setVisibility(View.VISIBLE);
                mostrar(resultado.data);
            }
        });
        vm.secagem().observe(this, resultado -> {
            Ui.carregando(b.iniciarSecagem, resultado.isLoading());
            if (resultado.isError()) {
                Ui.mensagem(b.erro, resultado.message);
            }
        });
        vm.aviso().observe(this, evento -> {
            CycleViewModel.Aviso aviso = evento.consume();
            if (aviso != null) {
                Intent abrir = new Intent(this, CycleActivity.class).putExtra(EXTRA_PEDIDO_ID, pedidoId)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NotificationHelper.notificar(this, pedidoId.hashCode(), getString(R.string.app_name),
                        getString(aviso == CycleViewModel.Aviso.PEDIDO_CONCLUIDO
                                ? R.string.notificacao_concluido : R.string.notificacao_lavagem), abrir);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        vm.iniciar(pedidoId);
    }

    @Override
    protected void onStop() {
        super.onStop();
        vm.pausar();
    }

    private void mostrar(Pedido p) {
        Ciclo ciclo = p.cicloAtual();
        b.painelTempo.setVisibility(View.GONE);
        b.progresso.setVisibility(View.GONE);
        b.iniciarSecagem.setVisibility(View.GONE);
        b.aviso.setVisibility(View.GONE);
        b.icone.setImageResource(ciclo != null && "SECAGEM".equals(ciclo.etapa) ? R.drawable.ic_wind : R.drawable.ic_washing_machine);
        b.icone.setImageTintList(ContextCompat.getColorStateList(this, R.color.roxo));
        b.maquina.setText(ciclo == null ? getString(R.string.ciclo_maquina, p.maquinaNome)
                : getString(R.string.ciclo_maquina, ciclo.maquinaNome));

        if ("ESTORNADO".equals(p.status)) {
            b.icone.setImageResource(R.drawable.ic_circle_alert);
            b.icone.setImageTintList(ContextCompat.getColorStateList(this, R.color.alerta));
            b.titulo.setText(R.string.ciclo_estornado);
            b.subtitulo.setText(R.string.ciclo_estornado_ajuda);
            return;
        }
        if (Pedido.CONCLUIDO.equals(p.status)) {
            b.icone.setImageResource(R.drawable.ic_circle_check);
            b.icone.setImageTintList(ContextCompat.getColorStateList(this, R.color.sucesso));
            b.titulo.setText(R.string.ciclo_concluido);
            b.subtitulo.setText(R.string.ciclo_concluido_msg);
            return;
        }
        if (p.podeIniciarSecagem) {
            b.icone.setImageResource(R.drawable.ic_circle_check);
            b.icone.setImageTintList(ContextCompat.getColorStateList(this, R.color.sucesso));
            b.titulo.setText(R.string.ciclo_lavagem_concluida);
            b.subtitulo.setText(R.string.ciclo_secagem_instrucao);
            b.iniciarSecagem.setVisibility(View.VISIBLE);
            return;
        }
        if (ciclo != null && ciclo.emAndamento()) {
            b.titulo.setText(getString(R.string.ciclo_em_andamento, UiText.etapa(this, ciclo.etapa)));
            b.painelTempo.setVisibility(View.VISIBLE);
            b.minutos.setText(getString(R.string.ciclo_minutos, ciclo.minutosRestantes));
            b.subtitulo.setText(getString(R.string.ciclo_termina_as, Formatters.hora(ciclo.fimPrevistoEm)));
            b.progresso.setVisibility(View.VISIBLE);
            b.progresso.setProgressCompat(progresso(ciclo), true);
            return;
        }
        // Pago, aguardando a confirmação de início da máquina.
        b.titulo.setText(R.string.ciclo_iniciando);
        b.subtitulo.setText(R.string.pagamento_inicio_automatico);
        b.progresso.setVisibility(View.VISIBLE);
        b.progresso.setIndeterminate(true);
    }

    private static int progresso(Ciclo ciclo) {
        try {
            long total = Duration.between(Instant.parse(ciclo.inicioEm), Instant.parse(ciclo.fimPrevistoEm)).getSeconds();
            long passado = Duration.between(Instant.parse(ciclo.inicioEm), Instant.now()).getSeconds();
            return total <= 0 ? 100 : (int) Math.min(100, Math.max(0, passado * 100 / total));
        } catch (RuntimeException ex) {
            return 0;
        }
    }
}
