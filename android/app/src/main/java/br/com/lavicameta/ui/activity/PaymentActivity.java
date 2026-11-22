package br.com.lavicameta.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.activity.OnBackPressedCallback;
import androidx.browser.customtabs.CustomTabsIntent;
import br.com.lavicameta.R;
import br.com.lavicameta.databinding.ActivityPaymentBinding;
import br.com.lavicameta.domain.model.Pagamento;
import br.com.lavicameta.domain.model.Pedido;
import br.com.lavicameta.ui.component.EstadoView;
import br.com.lavicameta.ui.component.Ui;
import br.com.lavicameta.util.Formatters;
import br.com.lavicameta.util.QrCodeGenerator;
import br.com.lavicameta.util.UiText;
import br.com.lavicameta.viewmodel.PaymentViewModel;
import br.com.lavicameta.viewmodel.PaymentViewModel.Desfecho;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

/**
 * T04 Pagamento (RF10–RF12). O app apenas exibe a cobrança; a confirmação é decidida pelo backend
 * a partir do gateway. O retorno do checkout não é tratado como prova de pagamento.
 */
public class PaymentActivity extends BaseActivity {

    public static final String EXTRA_PEDIDO_ID = "pedidoId";

    private ActivityPaymentBinding b;
    private PaymentViewModel vm;
    private String pedidoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        configurarToolbar(b.barra.toolbar, getString(R.string.pagamento_titulo), true);
        pedidoId = getIntent().getStringExtra(EXTRA_PEDIDO_ID);
        vm = viewModel(PaymentViewModel.class);
        EstadoView estado = new EstadoView(b.getRoot());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                confirmarCancelamento();
            }
        });
        b.metodos.addOnButtonCheckedListener((grupo, id, marcado) -> {
            if (marcado) {
                atualizarBotaoPagamento();
                b.painelPix.setVisibility(View.GONE);
                b.painelCartao.setVisibility(View.GONE);
                b.painelAguardando.setVisibility(View.GONE);
                Ui.mensagem(b.erro, null);
            }
        });
        b.gerar.setOnClickListener(v -> {
            Ui.mensagem(b.erro, null);
            vm.pagar(metodoSelecionado());
        });
        b.copiar.setOnClickListener(v -> copiarPix());
        b.abrirCheckout.setOnClickListener(v -> abrirCheckout());

        vm.pedido().observe(this, resultado -> {
            if (resultado.isLoading()) {
                estado.carregando();
                b.rolagem.setVisibility(View.GONE);
            } else if (resultado.isError()) {
                estado.erro(resultado.message, this::recreate);
                b.rolagem.setVisibility(View.GONE);
            } else {
                estado.esconder();
                b.rolagem.setVisibility(View.VISIBLE);
                preencherResumo(resultado.data);
            }
        });
        vm.segundosRestantes().observe(this, s ->
                b.tempo.setText(getString(R.string.pagamento_tempo_restante, Formatters.contagem(s))));
        vm.cobranca().observe(this, resultado -> {
            Ui.carregando(b.gerar, resultado.isLoading());
            if (resultado.isError()) {
                Ui.mensagem(b.erro, resultado.message);
            } else if (resultado.isSuccess()) {
                mostrarCobranca(resultado.data);
            }
        });
        vm.mensagem().observe(this, e -> {
            String texto = e.consume();
            if (texto != null) {
                Snackbar.make(b.getRoot(), texto, Snackbar.LENGTH_LONG).show();
            }
        });
        vm.desfecho().observe(this, e -> {
            Desfecho desfecho = e.consume();
            if (desfecho != null) {
                tratarDesfecho(desfecho);
            }
        });
        atualizarBotaoPagamento();
        vm.iniciar(pedidoId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        vm.retomarAcompanhamento();
    }

    @Override
    protected void onPause() {
        super.onPause();
        vm.pausarAcompanhamento();
    }

    private void preencherResumo(Pedido p) {
        b.resumoServico.setText(UiText.servico(this, p.servico));
        b.resumoMaquina.setText(getString(R.string.ciclo_maquina, p.maquinaNome));
        b.total.setText(Formatters.moeda(p.valorTotal));
        if (p.pagamento != null && Pagamento.PENDENTE.equals(p.pagamento.status)
                && (vm.cobranca().getValue() == null)) {
            // Retomando um pedido já com cobrança pendente (ex.: aberto pelo histórico).
            b.metodos.check(idDoMetodo(p.pagamento.metodo));
            mostrarCobranca(p.pagamento);
        }
    }

    private void mostrarCobranca(Pagamento pagamento) {
        b.painelPix.setVisibility(View.GONE);
        b.painelCartao.setVisibility(View.GONE);
        if (Pagamento.RECUSADO.equals(pagamento.status)) {
            b.painelAguardando.setVisibility(View.GONE);
            Ui.mensagem(b.erro, pagamento.motivo == null ? getString(R.string.pagamento_recusado_msg)
                    : pagamento.motivo + ". " + getString(R.string.pagamento_recusado_msg));
            return;
        }
        if (!Pagamento.PENDENTE.equals(pagamento.status)) {
            return;
        }
        b.painelAguardando.setVisibility(View.VISIBLE);
        if (pagamento.isPix() && pagamento.codigoPix != null) {
            b.painelPix.setVisibility(View.VISIBLE);
            b.codigoPix.setText(pagamento.codigoPix);
            int tamanho = getResources().getDimensionPixelSize(R.dimen.qr_code);
            Bitmap qr = QrCodeGenerator.gerar(pagamento.codigoPix, tamanho);
            b.qrCode.setImageBitmap(qr);
            b.qrCode.setContentDescription(getString(R.string.pix_qr_descricao, Formatters.moeda(pagamento.valor)));
            b.qrCode.setOnClickListener(pagamento.checkoutUrl == null ? null : v -> abrirUrl(pagamento.checkoutUrl));
        } else if (pagamento.checkoutUrl != null) {
            b.painelCartao.setVisibility(View.VISIBLE);
        }
        b.rolagem.post(() -> b.rolagem.smoothScrollTo(0, b.painelAguardando.getBottom()));
    }

    private void tratarDesfecho(Desfecho desfecho) {
        switch (desfecho) {
            case CONFIRMADO:
                startActivity(new Intent(this, CycleActivity.class).putExtra(CycleActivity.EXTRA_PEDIDO_ID, pedidoId));
                finish();
                break;
            case EXPIRADO:
                new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.pagamento_expirado_titulo)
                        .setMessage(R.string.pagamento_expirado_msg)
                        .setCancelable(false)
                        .setPositiveButton(R.string.acao_ok, (d, w) -> voltarParaInicio())
                        .show();
                break;
            case ESTORNADO:
                startActivity(new Intent(this, CycleActivity.class).putExtra(CycleActivity.EXTRA_PEDIDO_ID, pedidoId));
                finish();
                break;
            case CANCELADO:
            default:
                Snackbar.make(b.getRoot(), R.string.pagamento_cancelado_msg, Snackbar.LENGTH_SHORT).show();
                voltarParaInicio();
                break;
        }
    }

    private void voltarParaInicio() {
        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    private void confirmarCancelamento() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.pagamento_cancelar_titulo)
                .setMessage(R.string.pagamento_cancelar_msg)
                .setNegativeButton(R.string.pagamento_continuar, null)
                .setPositiveButton(R.string.pagamento_cancelar_confirma, (d, w) -> vm.cancelar())
                .show();
    }

    private String metodoSelecionado() {
        int id = b.metodos.getCheckedButtonId();
        if (id == R.id.metodoCredito) {
            return "CARTAO_CREDITO";
        }
        if (id == R.id.metodoDebito) {
            return "CARTAO_DEBITO";
        }
        return "PIX";
    }

    private int idDoMetodo(String metodo) {
        if ("CARTAO_CREDITO".equals(metodo)) {
            return R.id.metodoCredito;
        }
        if ("CARTAO_DEBITO".equals(metodo)) {
            return R.id.metodoDebito;
        }
        return R.id.metodoPix;
    }

    private void atualizarBotaoPagamento() {
        b.gerar.setText("PIX".equals(metodoSelecionado()) ? R.string.acao_gerar_pix : R.string.acao_pagar_cartao);
    }

    private void copiarPix() {
        ClipboardManager area = getSystemService(ClipboardManager.class);
        area.setPrimaryClip(ClipData.newPlainText("PIX", b.codigoPix.getText()));
        Snackbar.make(b.getRoot(), R.string.pix_copiado, Snackbar.LENGTH_SHORT).show();
    }

    private void abrirCheckout() {
        if (vm.cobranca().getValue() != null && vm.cobranca().getValue().data != null) {
            abrirUrl(vm.cobranca().getValue().data.checkoutUrl);
        } else if (vm.pedido().getValue() != null && vm.pedido().getValue().data != null
                && vm.pedido().getValue().data.pagamento != null) {
            abrirUrl(vm.pedido().getValue().data.pagamento.checkoutUrl);
        }
    }

    private void abrirUrl(String url) {
        if (url != null) {
            new CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(url));
        }
    }
}
