package br.com.lavicameta.ui.component;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.lavicameta.R;

/** Controla o bloco reutilizável de carregando / vazio / erro (layout view_estado). */
public final class EstadoView {

    private final View raiz;
    private final View carregando;
    private final ImageView icone;
    private final TextView mensagem;
    private final View tentar;

    public EstadoView(View container) {
        raiz = container.findViewById(R.id.estado);
        carregando = raiz.findViewById(R.id.estadoCarregando);
        icone = raiz.findViewById(R.id.estadoIcone);
        mensagem = raiz.findViewById(R.id.estadoMensagem);
        tentar = raiz.findViewById(R.id.estadoTentar);
    }

    public void carregando() {
        raiz.setVisibility(View.VISIBLE);
        carregando.setVisibility(View.VISIBLE);
        icone.setVisibility(View.GONE);
        mensagem.setVisibility(View.GONE);
        tentar.setVisibility(View.GONE);
    }

    public void vazio(String texto, int iconeRes) {
        raiz.setVisibility(View.VISIBLE);
        carregando.setVisibility(View.GONE);
        icone.setVisibility(View.VISIBLE);
        icone.setImageResource(iconeRes);
        mensagem.setVisibility(View.VISIBLE);
        mensagem.setText(texto);
        tentar.setVisibility(View.GONE);
    }

    public void erro(String texto, Runnable aoTentar) {
        raiz.setVisibility(View.VISIBLE);
        carregando.setVisibility(View.GONE);
        icone.setVisibility(View.VISIBLE);
        icone.setImageResource(R.drawable.ic_circle_alert);
        mensagem.setVisibility(View.VISIBLE);
        mensagem.setText(texto);
        tentar.setVisibility(aoTentar == null ? View.GONE : View.VISIBLE);
        tentar.setOnClickListener(v -> aoTentar.run());
    }

    public void esconder() {
        raiz.setVisibility(View.GONE);
    }
}
