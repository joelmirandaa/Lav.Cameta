package br.com.lavicameta.ui.activity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import br.com.lavicameta.R;
import br.com.lavicameta.databinding.ActivityMainBinding;
import br.com.lavicameta.ui.fragment.HistoryFragment;
import br.com.lavicameta.ui.fragment.MachinesFragment;
import br.com.lavicameta.ui.fragment.ProfileFragment;
import br.com.lavicameta.ui.fragment.WalletFragment;

/** Área do cliente com barra inferior: Início, Histórico, Carteira e Perfil (T02 e telas previstas). */
public class MainActivity extends BaseActivity {

    private static final String ABA = "aba";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        b.navegacao.inflateMenu(R.menu.menu_cliente);
        b.navegacao.setOnItemSelectedListener(item -> {
            mostrar(item.getItemId());
            return true;
        });
        b.navegacao.setOnItemReselectedListener(item -> { });
        int aba = savedInstanceState == null ? R.id.nav_inicio : savedInstanceState.getInt(ABA, R.id.nav_inicio);
        if (savedInstanceState == null) {
            mostrar(aba);
        }
        b.navegacao.setSelectedItemId(aba);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ABA, ((com.google.android.material.bottomnavigation.BottomNavigationView)
                findViewById(R.id.navegacao)).getSelectedItemId());
    }

    private void mostrar(int itemId) {
        String tag = String.valueOf(itemId);
        if (getSupportFragmentManager().findFragmentByTag(tag) != null
                && getSupportFragmentManager().findFragmentByTag(tag).isVisible()) {
            return;
        }
        Fragment fragmento;
        if (itemId == R.id.nav_historico) {
            fragmento = new HistoryFragment();
        } else if (itemId == R.id.nav_carteira) {
            fragmento = new WalletFragment();
        } else if (itemId == R.id.nav_perfil) {
            fragmento = new ProfileFragment();
        } else {
            fragmento = new MachinesFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.conteudo, fragmento, tag)
                .commit();
    }

    /** Usado pelo perfil ao sair da conta. */
    public void sair() {
        container().auth.sair();
        irParaLogin(null);
    }
}
