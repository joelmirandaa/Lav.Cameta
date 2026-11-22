package br.com.lavicameta.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import br.com.lavicameta.R;
import br.com.lavicameta.databinding.FragmentProfileBinding;
import br.com.lavicameta.domain.model.Usuario;
import br.com.lavicameta.ui.activity.MainActivity;
import br.com.lavicameta.ui.component.EstadoView;
import br.com.lavicameta.ui.component.Ui;
import br.com.lavicameta.viewmodel.ProfileViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import java.util.LinkedHashMap;
import java.util.Map;

/** Perfil do cliente (RF16, UC11). */
public class ProfileFragment extends BaseFragment {

    private FragmentProfileBinding b;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentProfileBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        ProfileViewModel vm = viewModel(ProfileViewModel.class);
        EstadoView estado = new EstadoView(view);
        Map<String, TextInputLayout> campos = new LinkedHashMap<>();
        campos.put("nome", b.campoNome);
        campos.put("email", b.campoEmail);
        campos.put("telefone", b.campoTelefone);
        campos.put("senhaAtual", b.campoSenhaAtual);
        campos.put("novaSenha", b.campoNovaSenha);
        campos.put("confirmacao", b.campoConfirmacao);

        b.salvar.setOnClickListener(v -> {
            Ui.mensagem(b.mensagemPerfil, null);
            vm.salvar(Ui.texto(b.campoNome), Ui.texto(b.campoEmail), Ui.texto(b.campoTelefone));
        });
        b.alterarSenha.setOnClickListener(v -> {
            Ui.mensagem(b.mensagemSenha, null);
            vm.alterarSenha(Ui.texto(b.campoSenhaAtual), Ui.texto(b.campoNovaSenha), Ui.texto(b.campoConfirmacao));
        });
        b.sair.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.sair_confirmar)
                .setNegativeButton(R.string.acao_cancelar, null)
                .setPositiveButton(R.string.acao_sair, (d, w) -> ((MainActivity) requireActivity()).sair())
                .show());

        vm.errosCampos().observe(getViewLifecycleOwner(), erros -> Ui.errosLocais(requireContext(), erros, campos));
        vm.perfil().observe(getViewLifecycleOwner(), resultado -> {
            if (resultado.isLoading() && b.campoNome.getEditText().length() == 0) {
                estado.carregando();
                b.conteudo.setVisibility(View.GONE);
            } else if (resultado.isError()) {
                estado.erro(resultado.message, vm::carregar);
                b.conteudo.setVisibility(View.GONE);
            } else if (resultado.isSuccess()) {
                estado.esconder();
                b.conteudo.setVisibility(View.VISIBLE);
                preencher(resultado.data);
            }
        });
        vm.salvamento().observe(getViewLifecycleOwner(), resultado -> {
            Ui.carregando(b.salvar, resultado.isLoading());
            if (resultado.isError() && !Ui.errosServidor(resultado, campos)) {
                if ("EMAIL_EM_USO".equals(resultado.code)) {
                    b.campoEmail.setError(resultado.message);
                } else if ("TELEFONE_EM_USO".equals(resultado.code)) {
                    b.campoTelefone.setError(resultado.message);
                } else {
                    Ui.mensagem(b.mensagemPerfil, resultado.message);
                }
            }
        });
        vm.trocaSenha().observe(getViewLifecycleOwner(), resultado -> {
            Ui.carregando(b.alterarSenha, resultado.isLoading());
            if (resultado.isError()) {
                if ("SENHA_ATUAL_INCORRETA".equals(resultado.code)) {
                    b.campoSenhaAtual.setError(resultado.message);
                } else if (!Ui.errosServidor(resultado, campos)) {
                    Ui.mensagem(b.mensagemSenha, resultado.message);
                }
            } else if (resultado.isSuccess()) {
                b.senhaAtual.setText("");
                b.novaSenha.setText("");
                b.confirmacao.setText("");
            }
        });
        vm.sucesso().observe(getViewLifecycleOwner(), evento -> {
            String tipo = evento.consume();
            if (tipo != null) {
                Snackbar.make(b.getRoot(), "senha".equals(tipo) ? R.string.perfil_senha_alterada : R.string.perfil_salvo,
                        Snackbar.LENGTH_SHORT).show();
            }
        });
        if (vm.perfil().getValue() == null) {
            vm.carregar();
        }
    }

    private void preencher(Usuario u) {
        b.nome.setText(u.nome);
        b.email.setText(u.email);
        b.telefone.setText(u.telefone);
        b.campoSenhaAtual.setVisibility(u.possuiSenha ? View.VISIBLE : View.GONE);
        b.semSenha.setVisibility(u.possuiSenha ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
