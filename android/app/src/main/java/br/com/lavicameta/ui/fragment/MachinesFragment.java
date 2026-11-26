package br.com.lavicameta.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import br.com.lavicameta.R;
import br.com.lavicameta.databinding.FragmentMachinesBinding;
import br.com.lavicameta.domain.model.Maquina;
import br.com.lavicameta.ui.activity.MachineDetailActivity;
import br.com.lavicameta.ui.adapter.MachineAdapter;
import br.com.lavicameta.ui.component.EstadoView;
import br.com.lavicameta.viewmodel.MachinesViewModel;
import com.google.android.material.snackbar.Snackbar;

/** T02 Escolha uma máquina. */
public class MachinesFragment extends BaseFragment {

    private FragmentMachinesBinding b;
    private MachinesViewModel vm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentMachinesBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        vm = viewModel(MachinesViewModel.class);
        EstadoView estado = new EstadoView(view);
        String nome = nomeUsuario();
        b.saudacao.setText(nome.isEmpty() ? "" : getString(R.string.ola_nome, nome.split(" ")[0]));

        MachineAdapter adapter = new MachineAdapter(this::abrir);
        b.lista.setLayoutManager(new GridLayoutManager(requireContext(),
                getResources().getInteger(R.integer.colunas_maquinas)));
        b.lista.setAdapter(adapter);
        b.atualizar.setColorSchemeResources(R.color.roxo);
        b.atualizar.setOnRefreshListener(vm::atualizar);

        marcarFiltro(vm.filtro());
        b.filtros.setOnCheckedStateChangeListener((grupo, ids) -> {
            if (ids.isEmpty()) {
                return;
            }
            int id = ids.get(0);
            vm.filtrar(id == R.id.filtroLavar ? MachinesViewModel.LAVAR
                    : id == R.id.filtroSecar ? MachinesViewModel.SECAR : MachinesViewModel.AMBOS);
        });

        vm.maquinas().observe(getViewLifecycleOwner(), resultado -> {
            b.atualizar.setRefreshing(false);
            if (resultado.isLoading()) {
                if (adapter.getItemCount() == 0) {
                    estado.carregando();
                } else {
                    b.atualizar.setRefreshing(true);
                }
            } else if (resultado.isError()) {
                adapter.submitList(java.util.Collections.emptyList());
                estado.erro(resultado.message, vm::atualizar);
            } else if (resultado.data.isEmpty()) {
                adapter.submitList(resultado.data);
                estado.vazio(getString(R.string.maquinas_vazio), R.drawable.ic_washing_machine);
            } else {
                estado.esconder();
                adapter.submitList(resultado.data);
            }
        });
    }

    private void marcarFiltro(String filtro) {
        int id = MachinesViewModel.LAVAR.equals(filtro) ? R.id.filtroLavar
                : MachinesViewModel.SECAR.equals(filtro) ? R.id.filtroSecar : R.id.filtroAmbos;
        b.filtros.check(id);
    }

    private void abrir(Maquina maquina) {
        if (!maquina.contratavel) {
            // UC04 exceção: "Em uso" ou "Manutenção" não pode ser selecionada.
            Snackbar.make(b.getRoot(), getString(R.string.maquina_indisponivel_toque, maquina.nome), Snackbar.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(requireContext(), MachineDetailActivity.class)
                .putExtra(MachineDetailActivity.EXTRA_MAQUINA_ID, maquina.id));
    }

    @Override
    public void onStart() {
        super.onStart();
        vm.iniciarAtualizacaoAutomatica();
    }

    @Override
    public void onStop() {
        super.onStop();
        vm.pararAtualizacaoAutomatica();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
