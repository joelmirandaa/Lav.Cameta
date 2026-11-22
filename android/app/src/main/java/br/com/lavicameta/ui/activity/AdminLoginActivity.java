package br.com.lavicameta.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import br.com.lavicameta.R;
import br.com.lavicameta.databinding.ActivityAdminLoginBinding;
import br.com.lavicameta.ui.component.Ui;
import br.com.lavicameta.viewmodel.LoginViewModel;
import com.google.android.material.textfield.TextInputLayout;
import java.util.LinkedHashMap;
import java.util.Map;

/** T05 Acesso administrativo separado do cliente (RF17, RNF10). */
public class AdminLoginActivity extends BaseActivity {

    private ActivityAdminLoginBinding b;
    private LoginViewModel vm;

    @Override
    protected boolean exigeSessao() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityAdminLoginBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        configurarToolbar(b.barra.toolbar, getString(R.string.acesso_administrativo), true);
        vm = viewModel(LoginViewModel.class);
        Map<String, TextInputLayout> campos = new LinkedHashMap<>();
        campos.put(LoginViewModel.CAMPO_IDENTIFICADOR, b.campoIdentificador);
        campos.put(LoginViewModel.CAMPO_SENHA, b.campoSenha);

        b.entrar.setOnClickListener(v -> entrar());
        b.senha.setOnEditorActionListener((v, acao, e) -> {
            if (acao == EditorInfo.IME_ACTION_DONE) {
                entrar();
                return true;
            }
            return false;
        });
        vm.errosCampos().observe(this, erros -> Ui.errosLocais(this, erros, campos));
        vm.estado().observe(this, resultado -> {
            Ui.carregando(b.entrar, resultado.isLoading());
            if (resultado.isSuccess()) {
                startActivity(new Intent(this, AdminReportsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            } else if (resultado.isError() && !Ui.errosServidor(resultado, campos)) {
                Ui.mensagem(b.erro, resultado.message);
            }
        });
    }

    private void entrar() {
        Ui.mensagem(b.erro, null);
        vm.entrar(Ui.texto(b.campoIdentificador), Ui.texto(b.campoSenha), true);
    }
}
