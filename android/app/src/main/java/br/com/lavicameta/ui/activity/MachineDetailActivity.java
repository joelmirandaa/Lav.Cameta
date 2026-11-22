package br.com.lavicameta.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import br.com.lavicameta.R;
import br.com.lavicameta.databinding.ActivityMachineDetailBinding;
import br.com.lavicameta.databinding.ItemServiceBinding;
import br.com.lavicameta.domain.model.Maquina;
import br.com.lavicameta.domain.model.MaquinaDetalhe;
import br.com.lavicameta.domain.model.Pedido;
import br.com.lavicameta.domain.model.Servico;
import br.com.lavicameta.ui.component.EstadoView;
import br.com.lavicameta.ui.component.StatusBadge;
import br.com.lavicameta.ui.component.Ui;
import br.com.lavicameta.util.Formatters;
import br.com.lavicameta.util.UiText;
import br.com.lavicameta.viewmodel.MachineDetailViewModel;
import java.util.ArrayList;
import java.util.List;

/** T03 Detalhe da máquina e escolha do serviço (RF07–RF09, UC05). */
public class MachineDetailActivity extends BaseActivity {

    public static final String EXTRA_MAQUINA_ID = "maquinaId";

    private ActivityMachineDetailBinding b;
    private MachineDetailViewModel vm;
    private final List<ItemServiceBinding> cartoes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMachineDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        configurarToolbar(b.barra.toolbar, "", true);
        EstadoView estado = new EstadoView(b.getRoot());
        vm = viewModel(MachineDetailViewModel.class);

        b.continuar.setOnClickListener(v -> {
            Ui.mensagem(b.erro, null);
            vm.continuar();
        });
        vm.detalhe().observe(this, resultado -> {
            if (resultado.isLoading()) {
                if (b.nome.length() == 0) {
                    estado.carregando();
                    b.rolagem.setVisibility(View.GONE);
                    b.rodape.setVisibility(View.GONE);
                }
            } else if (resultado.isError()) {
                estado.erro(resultado.message, vm::recarregar);
                b.rolagem.setVisibility(View.GONE);
                b.rodape.setVisibility(View.GONE);
            } else {
                estado.esconder();
                b.rolagem.setVisibility(View.VISIBLE);
                b.rodape.setVisibility(View.VISIBLE);
                preencher(resultado.data);
            }
        });
        vm.selecionado().observe(this, this::marcarSelecionado);
        vm.criacao().observe(this, resultado -> {
            Ui.carregando(b.continuar, resultado.isLoading());
            if (!resultado.isLoading()) {
                b.continuar.setEnabled(vm.selecionado().getValue() != null && contratavel());
            }
            if (resultado.isError()) {
                Ui.mensagem(b.erro, resultado.message);
            }
        });
        vm.pedidoCriado().observe(this, evento -> {
            Pedido pedido = evento.consume();
            if (pedido != null) {
                startActivity(new Intent(this, PaymentActivity.class).putExtra(PaymentActivity.EXTRA_PEDIDO_ID, pedido.id));
            }
        });
        if (savedInstanceState == null) {
            vm.carregar(getIntent().getIntExtra(EXTRA_MAQUINA_ID, -1));
        }
    }

    private boolean contratavel() {
        return vm.detalhe().getValue() != null && vm.detalhe().getValue().data != null
                && vm.detalhe().getValue().data.maquina.contratavel;
    }

    private void preencher(MaquinaDetalhe detalhe) {
        Maquina m = detalhe.maquina;
        b.barra.toolbar.setTitle(UiText.tipoMaquina(this, m.tipo));
        b.nome.setText(m.nome);
        b.icone.setImageResource(m.isLavadora() ? R.drawable.ic_washing_machine : R.drawable.ic_wind);
        StatusBadge.aplicar(b.status, UiText.statusMaquina(this, m), UiText.coresStatusMaquina(this, m));
        b.capacidade.setText(Formatters.capacidade(m.capacidadeKg));
        b.recursos.setText(m.recursos == null || m.recursos.isEmpty()
                ? getString(R.string.sem_recursos) : "• " + String.join("\n• ", m.recursos));
        b.indisponivel.setVisibility(m.contratavel ? View.GONE : View.VISIBLE);

        b.servicos.removeAllViews();
        cartoes.clear();
        for (Servico s : detalhe.servicos) {
            ItemServiceBinding cartao = ItemServiceBinding.inflate(getLayoutInflater(), b.servicos, true);
            cartao.nome.setText(UiText.servico(this, s.nome));
            cartao.descricao.setText(s.descricao + " · " + getString(R.string.servico_duracao, s.duracaoMin));
            cartao.preco.setText(Formatters.moeda(s.preco));
            cartao.cartao.setEnabled(m.contratavel);
            cartao.cartao.setAlpha(m.contratavel ? 1f : 0.5f);
            cartao.cartao.setTag(s);
            cartao.cartao.setOnClickListener(v -> vm.selecionar(s));
            cartoes.add(cartao);
        }
        marcarSelecionado(vm.selecionado().getValue());
    }

    private void marcarSelecionado(Servico selecionado) {
        int larguraBorda = getResources().getDisplayMetrics().densityDpi / 80;
        for (ItemServiceBinding cartao : cartoes) {
            Servico s = (Servico) cartao.cartao.getTag();
            boolean marcado = selecionado != null && selecionado.id == s.id;
            cartao.cartao.setChecked(marcado);
            cartao.cartao.setStrokeWidth(marcado ? Math.max(2, larguraBorda) : 1);
            cartao.cartao.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(this,
                    marcado ? R.color.info_fundo : R.color.branco));
        }
        b.continuar.setEnabled(selecionado != null && contratavel());
    }
}
