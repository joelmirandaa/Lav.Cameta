package br.com.lavicameta.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import br.com.lavicameta.R;
import br.com.lavicameta.databinding.ItemAdminMachineBinding;
import br.com.lavicameta.domain.model.Maquina;
import br.com.lavicameta.ui.component.StatusBadge;
import br.com.lavicameta.util.Formatters;
import br.com.lavicameta.util.UiText;
import java.util.Objects;
import java.util.function.Consumer;

public class AdminMachineAdapter extends ListAdapter<Maquina, AdminMachineAdapter.Holder> {

    private final Consumer<Maquina> aoEditar;
    private final Consumer<Maquina> aoAlternar;

    public AdminMachineAdapter(Consumer<Maquina> aoEditar, Consumer<Maquina> aoAlternar) {
        super(new DiffUtil.ItemCallback<Maquina>() {
            @Override
            public boolean areItemsTheSame(@NonNull Maquina a, @NonNull Maquina b) {
                return a.id == b.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Maquina a, @NonNull Maquina b) {
                return Objects.equals(a.nome, b.nome) && Objects.equals(a.status, b.status) && a.reservada == b.reservada
                        && Objects.equals(a.tipo, b.tipo) && Objects.equals(a.capacidadeKg, b.capacidadeKg)
                        && Objects.equals(a.minutosRestantes, b.minutosRestantes);
            }
        });
        this.aoEditar = aoEditar;
        this.aoAlternar = aoAlternar;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemAdminMachineBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Maquina m = getItem(position);
        Context c = holder.b.getRoot().getContext();
        holder.b.nome.setText(m.nome);
        holder.b.descricao.setText(UiText.tipoMaquina(c, m.tipo) + " · " + Formatters.capacidade(m.capacidadeKg));
        holder.b.icone.setImageResource(m.isLavadora() ? R.drawable.ic_washing_machine : R.drawable.ic_wind);
        StatusBadge.aplicar(holder.b.status, UiText.statusMaquina(c, m), UiText.coresStatusMaquina(c, m));
        boolean emManutencao = Maquina.MANUTENCAO.equals(m.status);
        holder.b.manutencao.setText(emManutencao ? R.string.acao_liberar : R.string.acao_colocar_manutencao);
        boolean podeAlternar = !Maquina.EM_USO.equals(m.status);
        holder.b.manutencao.setEnabled(podeAlternar);
        holder.b.manutencao.setAlpha(podeAlternar ? 1f : 0.4f);
        holder.b.editar.setOnClickListener(v -> aoEditar.accept(m));
        holder.b.manutencao.setOnClickListener(v -> aoAlternar.accept(m));
    }

    static final class Holder extends RecyclerView.ViewHolder {
        final ItemAdminMachineBinding b;

        Holder(ItemAdminMachineBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
