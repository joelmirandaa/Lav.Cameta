package br.com.lavicameta.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialCancellationException;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;
import br.com.lavicameta.BuildConfig;
import br.com.lavicameta.R;
import br.com.lavicameta.databinding.ActivityLoginBinding;
import br.com.lavicameta.ui.component.Ui;
import br.com.lavicameta.viewmodel.LoginViewModel;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.textfield.TextInputLayout;
import java.util.LinkedHashMap;
import java.util.Map;

/** T01 Login do cliente (RF02, RF03, UC02). Ponto de entrada do app. */
public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding b;
    private LoginViewModel vm;
    private Map<String, TextInputLayout> campos;

    @Override
    protected boolean exigeSessao() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = viewModel(LoginViewModel.class);
        if (vm.sessaoAtiva()) {
            abrirArea(vm.sessaoAdmin());
            return;
        }
        b = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        campos = new LinkedHashMap<>();
        campos.put(LoginViewModel.CAMPO_IDENTIFICADOR, b.campoIdentificador);
        campos.put(LoginViewModel.CAMPO_SENHA, b.campoSenha);

        Ui.mensagem(b.erro, getIntent().getStringExtra(EXTRA_MENSAGEM));
        b.entrar.setOnClickListener(v -> entrar());
        b.senha.setOnEditorActionListener((v, acao, e) -> {
            if (acao == EditorInfo.IME_ACTION_DONE) {
                entrar();
                return true;
            }
            return false;
        });
        b.esqueceuSenha.setOnClickListener(v -> startActivity(new Intent(this, RecoverPasswordActivity.class)));
        b.cadastrar.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        b.acessoAdmin.setOnClickListener(v -> startActivity(new Intent(this, AdminLoginActivity.class)));

        boolean googleConfigurado = !BuildConfig.GOOGLE_WEB_CLIENT_ID.isEmpty();
        b.grupoGoogle.setVisibility(googleConfigurado ? View.VISIBLE : View.GONE);
        b.entrarGoogle.setOnClickListener(v -> entrarComGoogle());

        vm.errosCampos().observe(this, erros -> Ui.errosLocais(this, erros, campos));
        vm.estado().observe(this, resultado -> {
            boolean carregando = resultado.isLoading();
            Ui.carregando(b.entrar, carregando);
            b.entrarGoogle.setEnabled(!carregando);
            if (resultado.isSuccess()) {
                abrirArea(false);
            } else if (resultado.isError()) {
                if (!Ui.errosServidor(resultado, campos)) {
                    Ui.mensagem(b.erro, resultado.message);
                }
            } else {
                Ui.mensagem(b.erro, null);
            }
        });
    }

    private void entrar() {
        Ui.mensagem(b.erro, null);
        vm.entrar(Ui.texto(b.campoIdentificador), Ui.texto(b.campoSenha), false);
    }

    private void entrarComGoogle() {
        Ui.mensagem(b.erro, null);
        GetGoogleIdOption opcao = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .build();
        GetCredentialRequest pedido = new GetCredentialRequest.Builder().addCredentialOption(opcao).build();
        CredentialManager.create(this).getCredentialAsync(this, pedido, null, ContextCompat.getMainExecutor(this),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse resposta) {
                        Credential credencial = resposta.getCredential();
                        if (credencial instanceof CustomCredential
                                && GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(credencial.getType())) {
                            vm.entrarComGoogle(GoogleIdTokenCredential.createFrom(credencial.getData()).getIdToken());
                        } else {
                            Ui.mensagem(b.erro, getString(R.string.google_falhou));
                        }
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        if (e instanceof NoCredentialException) {
                            Ui.mensagem(b.erro, getString(R.string.google_sem_conta));
                        } else if (!(e instanceof GetCredentialCancellationException)) {
                            Ui.mensagem(b.erro, getString(R.string.google_falhou));
                        }
                    }
                });
    }

    private void abrirArea(boolean admin) {
        Intent intent = new Intent(this, admin ? AdminReportsActivity.class : MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
