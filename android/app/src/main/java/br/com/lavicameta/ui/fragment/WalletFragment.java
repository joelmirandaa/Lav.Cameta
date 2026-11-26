package br.com.lavicameta.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import br.com.lavicameta.R;
import br.com.lavicameta.databinding.FragmentWalletBinding;
import br.com.lavicameta.databinding.ItemLinhaBinding;
import br.com.lavicameta.domain.model.Carteira;
import br.com.lavicameta.domain.model.Pagamento;
import br.com.lavicameta.ui.component.EstadoView;
import br.com.lavicameta.ui.component.StatusBadge;
import br.com.lavicameta.util.Formatters;
import br.com.lavicameta.util.UiText;
import br.com.lavicameta.viewmodel.WalletViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

/** Carteira: pagamentos e cartões tokenizados, sem dados completos de cartão (RF15, RN12). */
public class WalletFragment extends BaseFragment {

    private FragmentWalletBinding b;
    private WalletViewModel vm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentWalletBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        vm = viewModel(WalletViewModel.class);
        EstadoView estado = new EstadoView(view);
        b.atualizar.setColorSchemeResources(R.color.roxo);
        b.atualizar.setOnRefreshListener(vm::carregar);
        vm.mensagem().observe(getViewLifecycleOwner(), e -> {
            String texto = e.consume();
            if (texto != null) {
                Snackbar.make(b.getRoot(), texto, Snackbar.LENGTH_LONG).show();
            }
        });
        vm.carteira().observe(getViewLifecycleOwner(), resultado -> {
            b.atualizar.setRefreshing(false);
            if (resultado.isLoading()) {
                estado.carregando();
                b.conteudo.setVisibility(View.GONE);
            } else if (resultado.isError()) {
                estado.erro(resultado.message, vm::carregar);
                b.conteudo.setVisibility(View.GONE);
            } else {
                estado.esconder();
                b.conteudo.setVisibility(View.VISIBLE);
                preencher(resultado.data);
            }
        });
        vm.carregar();
    }

    private void preencher(Carteira carteira) {
        LayoutInflater inflater = getLayoutInflater();
        b.metodos.removeAllViews();
        if (carteira.metodosSalvos.isEmpty()) {
            vazio(b.metodos, getString(R.string.carteira_metodos_vazio));
        }
        for (Carteira.MetodoSalvo m : carteira.metodosSalvos) {
            ItemLinhaBinding linha = ItemLinhaBinding.inflate(inflater, b.metodos, true);
            linha.icone.setImageResource(R.drawable.ic_credit_card);
            linha.titulo.setText(getString(R.string.carteira_cartao_final,
                    m.bandeira == null ? "" : m.bandeira, m.ultimos4 == null ? "••••" : m.ultimos4));
            linha.subtitulo.setText(Formatters.dataHora(m.criadoEm));
            linha.acao.setVisibility(View.VISIBLE);
            linha.acao.setIconResource(R.drawable.ic_trash);
            linha.acao.setContentDescription(getString(R.string.acao_remover));
            linha.acao.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.carteira_remover_titulo)
                    .setNegativeButton(R.string.acao_cancelar, null)
                    .setPositiveButton(R.string.acao_remover, (d, w) -> vm.remover(m.id))
                    .show());
        }
        b.pagamentos.removeAllViews();
        if (carteira.pagamentos.isEmpty()) {
            vazio(b.pagamentos, getString(R.string.carteira_pagamentos_vazio));
        }
        for (Pagamento p : carteira.pagamentos) {
            ItemLinhaBinding linha = ItemLinhaBinding.inflate(inflater, b.pagamentos, true);
            linha.icone.setImageResource(p.isPix() ? R.drawable.ic_qr_code : R.drawable.ic_credit_card);
            linha.titulo.setText(UiText.metodo(requireContext(), p.metodo));
            linha.subtitulo.setText(Formatters.dataHora(p.pagoEm != null ? p.pagoEm : p.criadoEm));
            linha.valor.setText(Formatters.moeda(p.valor));
            linha.selo.setVisibility(View.VISIBLE);
            StatusBadge.aplicar(linha.selo, UiText.statusPagamento(requireContext(), p.status),
                    UiText.coresStatusPedido(requireContext(), corPagamento(p.status)));
        }
    }

    /** Reaproveita a paleta de status do pedido para o pagamento. */
    private static String corPagamento(String status) {
        switch (status) {
            case "APROVADO": return "CONCLUIDO";
            case "PENDENTE": return "EM_ANDAMENTO";
            case "ESTORNADO":
            case "ESTORNO_PENDENTE": return "ESTORNADO";
            default: return "CANCELADO";
        }
    }

    private void vazio(LinearLayout destino, String texto) {
        android.widget.TextView t = new android.widget.TextView(requireContext());
        t.setTextAppearance(R.style.Lav_Texto_Auxiliar);
        t.setText(texto);
        t.setPadding(0, getResources().getDimensionPixelSize(R.dimen.espaco_1), 0, 0);
        destino.addView(t);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
