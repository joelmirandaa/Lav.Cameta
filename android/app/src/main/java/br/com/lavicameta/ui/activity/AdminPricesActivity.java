package br.com.lavicameta.ui.activity;

import android.os.Bundle;
import br.com.lavicameta.R;
import br.com.lavicameta.databinding.ActivityAdminPricesBinding;
import br.com.lavicameta.databinding.ItemPriceBinding;
import br.com.lavicameta.domain.model.Servico;
import br.com.lavicameta.ui.component.EstadoView;
import br.com.lavicameta.util.UiText;
import br.com.lavicameta.viewmodel.AdminPricesViewModel;
import com.google.android.material.snackbar.Snackbar;

/** Preços e durações dos serviços (RN01, UC15). */
public class AdminPricesActivity extends BaseActivity {

    private ActivityAdminPricesBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityAdminPricesBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        configurarToolbar(b.barra.toolbar, getString(R.string.admin_precos_titulo), true);
        AdminPricesViewModel vm = viewModel(AdminPricesViewModel.class);
        EstadoView estado = new EstadoView(b.getRoot());

        vm.mensagem().observe(this, e -> {
            String texto = e.consume();
            if (texto != null) {
                Snackbar.make(b.getRoot(), texto, Snackbar.LENGTH_LONG).show();
            }
        });
        vm.salvo().observe(this, e -> {
            if (e.consume() != null) {
                Snackbar.make(b.getRoot(), R.string.admin_preco_salvo, Snackbar.LENGTH_SHORT).show();
            }
        });
        vm.servicos().observe(this, resultado -> {
            if (resultado.isLoading()) {
                if (b.servicos.getChildCount() == 0) {
                    estado.carregando();
                }
            } else if (resultado.isError()) {
                estado.erro(resultado.message, vm::carregar);
            } else {
                estado.esconder();
                b.servicos.removeAllViews();
                for (Servico s : resultado.data) {
                    ItemPriceBinding item = ItemPriceBinding.inflate(getLayoutInflater(), b.servicos, true);
                    item.nome.setText(UiText.servico(this, s.nome));
                    item.descricao.setText(s.descricao);
                    item.preco.setText(s.preco.setScale(2).toPlainString().replace('.', ','));
                    item.duracao.setText(String.valueOf(s.duracaoMin));
                    item.salvar.setOnClickListener(v -> {
                        item.campoPreco.setError(null);
                        if (!vm.salvar(s, String.valueOf(item.preco.getText()), String.valueOf(item.duracao.getText()))) {
                            item.campoPreco.setError(getString(R.string.erro_preco_duracao));
                        }
                    });
                }
            }
        });
        vm.carregar();
    }
}
