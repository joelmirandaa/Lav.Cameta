package br.com.lavicameta.ui.component;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import br.com.lavicameta.domain.usecase.FormValidator;
import br.com.lavicameta.util.Result;
import br.com.lavicameta.util.UiText;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec;
import com.google.android.material.progressindicator.IndeterminateDrawable;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Map;

/** Utilidades de apresentação compartilhadas pelas telas (estados de botão, erros de campo). */
public final class Ui {

    private Ui() {
    }

    /** Botão em carregamento: desabilitado e com ícone giratório (guia, seção 5). */
    public static void carregando(MaterialButton botao, boolean ativo) {
        botao.setEnabled(!ativo);
        if (ativo) {
            if (botao.getTag() == null) {
                botao.setTag(botao.getIcon() == null ? Boolean.FALSE : botao.getIcon());
            }
            Context c = botao.getContext();
            CircularProgressIndicatorSpec spec = new CircularProgressIndicatorSpec(c, null, 0,
                    com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator_ExtraSmall);
            spec.indicatorColors = new int[] {botao.getCurrentTextColor()};
            botao.setIcon(IndeterminateDrawable.createCircularDrawable(c, spec));
        } else if (botao.getTag() != null) {
            Object anterior = botao.getTag();
            botao.setIcon(anterior instanceof android.graphics.drawable.Drawable
                    ? (android.graphics.drawable.Drawable) anterior : null);
            botao.setTag(null);
        }
    }

    public static void mensagem(TextView alerta, String texto) {
        if (texto == null || texto.isEmpty()) {
            alerta.setVisibility(View.GONE);
        } else {
            alerta.setText(texto);
            alerta.setVisibility(View.VISIBLE);
        }
    }

    /** Erros de validação local, por nome de campo. */
    public static void errosLocais(Context c, Map<String, FormValidator.Erro> erros, Map<String, TextInputLayout> campos) {
        for (Map.Entry<String, TextInputLayout> campo : campos.entrySet()) {
            campo.getValue().setError(UiText.erroCampo(c, erros == null ? null : erros.get(campo.getKey())));
        }
        focarPrimeiroErro(campos);
    }

    /** Erros de validação vindos da API ({@code campos}); retorna true se algum foi exibido. */
    public static boolean errosServidor(Result<?> resultado, Map<String, TextInputLayout> campos) {
        boolean algum = false;
        for (Map.Entry<String, String> erro : resultado.fieldErrors.entrySet()) {
            TextInputLayout campo = campos.get(erro.getKey());
            if (campo != null) {
                campo.setError(erro.getValue());
                algum = true;
            }
        }
        if (algum) {
            focarPrimeiroErro(campos);
        }
        return algum;
    }

    private static void focarPrimeiroErro(Map<String, TextInputLayout> campos) {
        for (TextInputLayout campo : campos.values()) {
            if (campo.getError() != null && campo.getEditText() != null) {
                campo.getEditText().requestFocus();
                return;
            }
        }
    }

    public static String texto(TextInputLayout campo) {
        return campo.getEditText() == null || campo.getEditText().getText() == null ? ""
                : campo.getEditText().getText().toString();
    }
}
