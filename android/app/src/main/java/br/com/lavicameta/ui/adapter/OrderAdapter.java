package br.com.lavicameta.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import br.com.lavicameta.databinding.ItemOrderBinding;
import br.com.lavicameta.domain.model.Pedido;
import br.com.lavicameta.ui.component.StatusBadge;
import br.com.lavicameta.util.Formatters;
import br.com.lavicameta.util.UiText;
import java.util.Objects;
import java.util.function.Consumer;

public class OrderAdapter extends ListAdapter<Pedido, OrderAdapter.Holder> {

    private final Consumer<Pedido> aoTocar;

    public OrderAdapter(Consumer<Pedido> aoTocar) {
        super(new DiffUtil.ItemCallback<Pedido>() {
            @Override
            public boolean areItemsTheSame(@NonNull Pedido a, @NonNull Pedido b) {
                return a.id.equals(b.id);
            }

            @Override
            public boolean areContentsTheSame(@NonNull Pedido a, @NonNull Pedido b) {
                return Objects.equals(a.status, b.status);
            }
        });
        this.aoTocar = aoTocar;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Pedido p = getItem(position);
        Context c = holder.b.getRoot().getContext();
        holder.b.servico.setText(UiText.servico(c, p.servico));
        holder.b.valor.setText(Formatters.moeda(p.valorTotal));
        holder.b.detalhes.setText(p.maquinaNome + " · " + Formatters.dataHora(p.criadoEm));
        StatusBadge.aplicar(holder.b.status, UiText.statusPedido(c, p.status), UiText.coresStatusPedido(c, p.status));
        holder.b.getRoot().setOnClickListener(v -> aoTocar.accept(p));
    }

    static final class Holder extends RecyclerView.ViewHolder {
        final ItemOrderBinding b;

        Holder(ItemOrderBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
