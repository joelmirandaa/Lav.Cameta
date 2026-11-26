package br.com.lavicameta.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import br.com.lavicameta.R;
import br.com.lavicameta.databinding.ActivityAdminMachinesBinding;
import br.com.lavicameta.domain.model.Maquina;
import br.com.lavicameta.ui.adapter.AdminMachineAdapter;
import br.com.lavicameta.ui.component.EstadoView;
import br.com.lavicameta.viewmodel.AdminMachinesViewModel;
import com.google.android.material.snackbar.Snackbar;

/** Gestão de máquinas (UC15): cadastrar, editar e alternar manutenção. */
public class AdminMachinesActivity extends BaseActivity {

    private AdminMachinesViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAdminMachinesBinding b = ActivityAdminMachinesBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        configurarToolbar(b.barra.toolbar, getString(R.string.admin_maquinas_titulo), true);
        vm = viewModel(AdminMachinesViewModel.class);
        EstadoView estado = new EstadoView(b.getRoot());
        AdminMachineAdapter adapter = new AdminMachineAdapter(this::editar, vm::alternarManutencao);
        b.lista.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.colunas_maquinas)));
        b.lista.setAdapter(adapter);
        b.atualizar.setColorSchemeResources(R.color.roxo);
        b.atualizar.setOnRefreshListener(vm::carregar);
        b.nova.setOnClickListener(v -> editar(null));
        vm.mensagem().observe(this, e -> {
            String texto = e.consume();
            if (texto != null) {
                Snackbar.make(b.getRoot(), texto, Snackbar.LENGTH_LONG).show();
            }
        });
        vm.maquinas().observe(this, resultado -> {
            b.atualizar.setRefreshing(false);
            if (resultado.isLoading()) {
                if (adapter.getItemCount() == 0) {
                    estado.carregando();
                }
            } else if (resultado.isError()) {
                estado.erro(resultado.message, vm::carregar);
            } else if (resultado.data.isEmpty()) {
                adapter.submitList(resultado.data);
                estado.vazio(getString(R.string.admin_maquinas_vazio), R.drawable.ic_washing_machine);
            } else {
                estado.esconder();
                adapter.submitList(resultado.data);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        vm.carregar();
    }

    private void editar(Maquina maquina) {
        Intent intent = new Intent(this, AdminMachineFormActivity.class);
        if (maquina != null) {
            intent.putExtra(AdminMachineFormActivity.EXTRA_ID, maquina.id)
                    .putExtra(AdminMachineFormActivity.EXTRA_NOME, maquina.nome)
                    .putExtra(AdminMachineFormActivity.EXTRA_TIPO, maquina.tipo)
                    .putExtra(AdminMachineFormActivity.EXTRA_CAPACIDADE, maquina.capacidadeKg.toPlainString())
                    .putExtra(AdminMachineFormActivity.EXTRA_RECURSOS,
                            maquina.recursos == null ? "" : String.join(", ", maquina.recursos));
        }
        startActivity(intent);
    }
}
