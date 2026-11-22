package br.com.lavicameta.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import br.com.lavicameta.R;
import br.com.lavicameta.databinding.ItemMachineBinding;
import br.com.lavicameta.domain.model.Maquina;
import br.com.lavicameta.ui.component.StatusBadge;
import br.com.lavicameta.util.Formatters;
import br.com.lavicameta.util.UiText;
import java.util.Objects;
import java.util.function.Consumer;

/** Cartões das máquinas com status textual (Disponível / Em uso · min / Manutenção / Reservada). */
public class MachineAdapter extends ListAdapter<Maquina, MachineAdapter.Holder> {

    private final Consumer<Maquina> aoTocar;

    public MachineAdapter(Consumer<Maquina> aoTocar) {
        super(new DiffUtil.ItemCallback<Maquina>() {
            @Override
            public boolean areItemsTheSame(@NonNull Maquina a, @NonNull Maquina b) {
                return a.id == b.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Maquina a, @NonNull Maquina b) {
                return Objects.equals(a.nome, b.nome) && Objects.equals(a.status, b.status)
                        && Objects.equals(a.minutosRestantes, b.minutosRestantes) && a.reservada == b.reservada
                        && Objects.equals(a.capacidadeKg, b.capacidadeKg);
            }
        });
        this.aoTocar = aoTocar;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemMachineBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Maquina m = getItem(position);
        Context c = holder.b.getRoot().getContext();
        holder.b.nome.setText(m.nome);
        holder.b.descricao.setText(UiText.tipoMaquina(c, m.tipo) + " · " + Formatters.capacidade(m.capacidadeKg));
        holder.b.icone.setImageResource(m.isLavadora() ? R.drawable.ic_washing_machine : R.drawable.ic_wind);
        String status = UiText.statusMaquina(c, m);
        StatusBadge.aplicar(holder.b.status, status, UiText.coresStatusMaquina(c, m));
        holder.b.seta.setAlpha(m.contratavel ? 1f : 0.3f);
        holder.b.cartao.setContentDescription(m.nome + ", " + UiText.tipoMaquina(c, m.tipo) + ", " + status);
        holder.b.cartao.setOnClickListener(v -> aoTocar.accept(m));
    }

    static final class Holder extends RecyclerView.ViewHolder {
        final ItemMachineBinding b;

        Holder(ItemMachineBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
