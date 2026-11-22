package br.com.lavicameta.ui.component;

import android.content.res.ColorStateList;
import android.widget.TextView;

/** Aplica cor de texto e fundo claro a um selo de status (sempre acompanhado de texto). */
public final class StatusBadge {

    private StatusBadge() {
    }

    public static void aplicar(TextView selo, String texto, int[] cores) {
        selo.setText(texto);
        selo.setTextColor(cores[0]);
        selo.setBackgroundTintList(ColorStateList.valueOf(cores[1]));
    }
}
