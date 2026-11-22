package br.com.lavicameta.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import br.com.lavicameta.AppContainer;
import br.com.lavicameta.LavicametaApp;
import br.com.lavicameta.R;
import br.com.lavicameta.viewmodel.ViewModelFactory;
import com.google.android.material.appbar.MaterialToolbar;

/** Base das telas autenticadas: fábrica de ViewModels e retorno ao login quando a sessão expira. */
public abstract class BaseActivity extends AppCompatActivity {

    public static final String EXTRA_MENSAGEM = "mensagem";

    protected AppContainer container() {
        return ((LavicametaApp) getApplication()).container();
    }

    protected <T extends ViewModel> T viewModel(Class<T> classe) {
        return new ViewModelProvider(this, new ViewModelFactory(container())).get(classe);
    }

    /** Telas que exigem login sobrescrevem para true. */
    protected boolean exigeSessao() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (exigeSessao()) {
            container().sessao.sessaoExpirada().observe(this, evento -> {
                if (evento != null && evento.consume() != null) {
                    irParaLogin(getString(R.string.sessao_expirada));
                }
            });
        }
    }

    /** Android 15+ desenha de borda a borda: reserva o espaço das barras do sistema e do teclado. */
    @Override
    public void onContentChanged() {
        super.onContentChanged();
        View raiz = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(raiz, (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime()
                    | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (exigeSessao() && !container().sessao.valida()) {
            irParaLogin(getString(R.string.sessao_expirada));
        }
    }

    protected void irParaLogin(String mensagem) {
        Intent intent = new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (mensagem != null) {
            intent.putExtra(EXTRA_MENSAGEM, mensagem);
        }
        startActivity(intent);
        finish();
    }

    protected void configurarToolbar(MaterialToolbar toolbar, CharSequence titulo, boolean voltar) {
        toolbar.setTitle(titulo);
        if (voltar) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
            toolbar.setNavigationContentDescription(R.string.acao_voltar);
            toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }
    }
}
