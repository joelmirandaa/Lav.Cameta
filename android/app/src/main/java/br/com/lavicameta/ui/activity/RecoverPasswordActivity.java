package br.com.lavicameta.ui.activity;

import android.os.Bundle;
import android.view.View;
import br.com.lavicameta.R;
import br.com.lavicameta.databinding.ActivityRecoverPasswordBinding;
import br.com.lavicameta.ui.component.Ui;
import br.com.lavicameta.viewmodel.RecoverPasswordViewModel;
import br.com.lavicameta.viewmodel.RecoverPasswordViewModel.Etapa;
import com.google.android.material.textfield.TextInputLayout;
import java.util.LinkedHashMap;
import java.util.Map;

/** Recuperação de senha por código enviado ao e-mail (RF04, UC03). */
public class RecoverPasswordActivity extends BaseActivity {

    private ActivityRecoverPasswordBinding b;

    @Override
    protected boolean exigeSessao() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityRecoverPasswordBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        configurarToolbar(b.barra.toolbar, "", true);
        RecoverPasswordViewModel vm = viewModel(RecoverPasswordViewModel.class);
        Map<String, TextInputLayout> campos = new LinkedHashMap<>();
        campos.put("email", b.campoEmail);
        campos.put("codigo", b.campoCodigo);
        campos.put("senha", b.campoSenha);
        campos.put("confirmacao", b.campoConfirmacao);

        b.enviarCodigo.setOnClickListener(v -> {
            Ui.mensagem(b.erro, null);
            vm.solicitar(Ui.texto(b.campoEmail));
        });
        b.redefinir.setOnClickListener(v -> {
            Ui.mensagem(b.erro, null);
            vm.redefinir(Ui.texto(b.campoCodigo), Ui.texto(b.campoSenha), Ui.texto(b.campoConfirmacao));
        });
        b.reenviar.setOnClickListener(v -> vm.voltarParaSolicitacao());
        b.irLogin.setOnClickListener(v -> finish());

        vm.errosCampos().observe(this, erros -> Ui.errosLocais(this, erros, campos));
        vm.etapa().observe(this, etapa -> {
            b.etapaSolicitar.setVisibility(etapa == Etapa.SOLICITAR ? View.VISIBLE : View.GONE);
            b.etapaRedefinir.setVisibility(etapa == Etapa.REDEFINIR ? View.VISIBLE : View.GONE);
            b.etapaConcluido.setVisibility(etapa == Etapa.CONCLUIDO ? View.VISIBLE : View.GONE);
            if (etapa == Etapa.REDEFINIR) {
                b.codigoEnviado.setText(getString(R.string.recuperar_codigo_enviado, vm.email()));
            }
        });
        vm.estado().observe(this, resultado -> {
            Ui.carregando(b.enviarCodigo, resultado.isLoading());
            Ui.carregando(b.redefinir, resultado.isLoading());
            if (resultado.isError() && !Ui.errosServidor(resultado, campos)) {
                Ui.mensagem(b.erro, resultado.message);
            }
        });
    }
}
