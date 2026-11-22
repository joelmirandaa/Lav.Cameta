package br.com.lavicameta.ui.activity;

import android.os.Bundle;
import android.widget.Toast;
import br.com.lavicameta.R;
import br.com.lavicameta.databinding.ActivityAdminMachineFormBinding;
import br.com.lavicameta.domain.model.Maquina;
import br.com.lavicameta.ui.component.Ui;
import br.com.lavicameta.viewmodel.AdminMachineFormViewModel;
import com.google.android.material.textfield.TextInputLayout;
import java.util.LinkedHashMap;
import java.util.Map;

/** Cadastro/edição de máquina (UC15). */
public class AdminMachineFormActivity extends BaseActivity {

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_NOME = "nome";
    public static final String EXTRA_TIPO = "tipo";
    public static final String EXTRA_CAPACIDADE = "capacidade";
    public static final String EXTRA_RECURSOS = "recursos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAdminMachineFormBinding b = ActivityAdminMachineFormBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        Integer id = getIntent().hasExtra(EXTRA_ID) ? getIntent().getIntExtra(EXTRA_ID, 0) : null;
        configurarToolbar(b.barra.toolbar, getString(id == null ? R.string.admin_form_novo : R.string.admin_form_editar), true);
        AdminMachineFormViewModel vm = viewModel(AdminMachineFormViewModel.class);

        if (savedInstanceState == null) {
            b.nome.setText(getIntent().getStringExtra(EXTRA_NOME));
            b.capacidade.setText(getIntent().getStringExtra(EXTRA_CAPACIDADE));
            b.recursos.setText(getIntent().getStringExtra(EXTRA_RECURSOS));
            String tipo = getIntent().getStringExtra(EXTRA_TIPO);
            b.tipo.check("SECADORA".equals(tipo) ? R.id.tipoSecadora : R.id.tipoLavadora);
        }
        Map<String, TextInputLayout> campos = new LinkedHashMap<>();
        campos.put("nome", b.campoNome);
        campos.put("capacidade", b.campoCapacidade);
        campos.put("capacidadeKg", b.campoCapacidade);
        campos.put("recursos", b.campoRecursos);

        b.salvar.setOnClickListener(v -> {
            Ui.mensagem(b.erro, null);
            String tipo = b.tipo.getCheckedButtonId() == R.id.tipoSecadora ? "SECADORA" : Maquina.LAVADORA;
            vm.salvar(id, Ui.texto(b.campoNome), tipo, Ui.texto(b.campoCapacidade), Ui.texto(b.campoRecursos));
        });
        vm.errosCampos().observe(this, erros -> {
            Map<String, TextInputLayout> locais = new LinkedHashMap<>(campos);
            locais.remove("capacidadeKg");
            Ui.errosLocais(this, erros, locais);
        });
        vm.salvamento().observe(this, resultado -> {
            Ui.carregando(b.salvar, resultado.isLoading());
            if (resultado.isSuccess()) {
                Toast.makeText(this, R.string.admin_maquina_salva, Toast.LENGTH_SHORT).show();
                finish();
            } else if (resultado.isError()) {
                if ("NOME_EM_USO".equals(resultado.code)) {
                    b.campoNome.setError(resultado.message);
                } else if (!Ui.errosServidor(resultado, campos)) {
                    Ui.mensagem(b.erro, resultado.message);
                }
            }
        });
    }
}
