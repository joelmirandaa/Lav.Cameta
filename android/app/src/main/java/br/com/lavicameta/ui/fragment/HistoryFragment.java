package br.com.lavicameta.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.com.lavicameta.R;
import br.com.lavicameta.databinding.FragmentListBinding;
import br.com.lavicameta.domain.model.Pedido;
import br.com.lavicameta.ui.activity.CycleActivity;
import br.com.lavicameta.ui.activity.PaymentActivity;
import br.com.lavicameta.ui.adapter.OrderAdapter;
import br.com.lavicameta.ui.component.EstadoView;
import br.com.lavicameta.viewmodel.HistoryViewModel;

/** Histórico de serviços (RF14, CT17). Pedidos em aberto reabrem pagamento ou acompanhamento. */
public class HistoryFragment extends BaseFragment {

    private FragmentListBinding b;
    private HistoryViewModel vm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentListBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        vm = viewModel(HistoryViewModel.class);
        EstadoView estado = new EstadoView(view);
        b.titulo.setText(R.string.historico_titulo);
        OrderAdapter adapter = new OrderAdapter(this::abrir);
        LinearLayoutManager gerente = new LinearLayoutManager(requireContext());
        b.lista.setLayoutManager(gerente);
        b.lista.setAdapter(adapter);
        b.lista.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                if (dy > 0 && gerente.findLastVisibleItemPosition() >= adapter.getItemCount() - 3) {
                    vm.carregarMais();
                }
            }
        });
        b.atualizar.setColorSchemeResources(R.color.agua);
        b.atualizar.setOnRefreshListener(vm::recarregar);
        vm.itens().observe(getViewLifecycleOwner(), resultado -> {
            b.atualizar.setRefreshing(false);
            if (resultado.isLoading()) {
                if (adapter.getItemCount() == 0) {
                    estado.carregando();
                }
            } else if (resultado.isError()) {
                estado.erro(resultado.message, vm::recarregar);
            } else if (resultado.data.isEmpty()) {
                adapter.submitList(resultado.data);
                estado.vazio(getString(R.string.historico_vazio), R.drawable.ic_history);
            } else {
                estado.esconder();
                adapter.submitList(resultado.data);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        vm.recarregar();
    }

    private void abrir(Pedido pedido) {
        if (pedido.aguardandoPagamento()) {
            startActivity(new Intent(requireContext(), PaymentActivity.class).putExtra(PaymentActivity.EXTRA_PEDIDO_ID, pedido.id));
        } else if (pedido.cicloAtual() != null || Pedido.PAGO.equals(pedido.status) || "ESTORNADO".equals(pedido.status)) {
            startActivity(new Intent(requireContext(), CycleActivity.class).putExtra(CycleActivity.EXTRA_PEDIDO_ID, pedido.id));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
