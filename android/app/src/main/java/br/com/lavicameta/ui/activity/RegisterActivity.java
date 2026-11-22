package br.com.lavicameta.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import br.com.lavicameta.R;
import br.com.lavicameta.databinding.ActivityRegisterBinding;
import br.com.lavicameta.ui.component.Ui;
import br.com.lavicameta.viewmodel.RegisterViewModel;
import com.google.android.material.textfield.TextInputLayout;
import java.util.LinkedHashMap;
import java.util.Map;

/** Cadastro do cliente (RF01). Após criar a conta, abre a lista de máquinas. */
public class RegisterActivity extends BaseActivity {

    private ActivityRegisterBinding b;

    @Override
    protected boolean exigeSessao() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        configurarToolbar(b.barra.toolbar, "", true);
        RegisterViewModel vm = viewModel(RegisterViewModel.class);
        Map<String, TextInputLayout> campos = new LinkedHashMap<>();
        campos.put("nome", b.campoNome);
        campos.put("email", b.campoEmail);
        campos.put("telefone", b.campoTelefone);
        campos.put("senha", b.campoSenha);
        campos.put("confirmacao", b.campoConfirmacao);

        b.criarConta.setOnClickListener(v -> {
            Ui.mensagem(b.erro, null);
            vm.cadastrar(Ui.texto(b.campoNome), Ui.texto(b.campoEmail), Ui.texto(b.campoTelefone),
                    Ui.texto(b.campoSenha), Ui.texto(b.campoConfirmacao));
        });
        vm.errosCampos().observe(this, erros -> {
            Ui.errosLocais(this, erros, campos);
            if (erros.containsKey("email") && erros.get("email") == br.com.lavicameta.domain.usecase.FormValidator.Erro.CONTATO_OBRIGATORIO) {
                b.campoTelefone.setError(getString(R.string.erro_contato));
            }
        });
        vm.estado().observe(this, resultado -> {
            Ui.carregando(b.criarConta, resultado.isLoading());
            if (resultado.isSuccess()) {
                startActivity(new Intent(this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            } else if (resultado.isError()) {
                if ("EMAIL_EM_USO".equals(resultado.code)) {
                    b.campoEmail.setError(resultado.message);
                } else if ("TELEFONE_EM_USO".equals(resultado.code)) {
                    b.campoTelefone.setError(resultado.message);
                } else if (!Ui.errosServidor(resultado, campos)) {
                    Ui.mensagem(b.erro, resultado.message);
                }
            }
        });
    }
}
