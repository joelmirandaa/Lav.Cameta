package br.com.lavicameta.ui.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import br.com.lavicameta.R;
import br.com.lavicameta.domain.model.Relatorio;
import br.com.lavicameta.util.Formatters;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Gráfico simples dos relatórios: linha por semana (T06) ou barras por mês (T07).
 * Desenho próprio para evitar dependência externa; cores da paleta oficial.
 */
public class ChartView extends View {

    public enum Tipo { LINHA, BARRAS }

    private final Paint grade = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint serie = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pontos = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint rotulo = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint valorMaximo = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path caminho = new Path();
    private final RectF barra = new RectF();
    private final float densidade;
    private List<Relatorio.Ponto> dados = new ArrayList<>();
    private Tipo tipo = Tipo.LINHA;

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        densidade = getResources().getDisplayMetrics().density;
        int agua = ContextCompat.getColor(context, R.color.agua);
        grade.setColor(ContextCompat.getColor(context, R.color.borda));
        grade.setStrokeWidth(densidade);
        serie.setColor(agua);
        serie.setStrokeWidth(3 * densidade);
        serie.setStyle(Paint.Style.STROKE);
        serie.setStrokeCap(Paint.Cap.ROUND);
        serie.setStrokeJoin(Paint.Join.ROUND);
        pontos.setColor(agua);
        rotulo.setColor(ContextCompat.getColor(context, R.color.texto_secundario));
        rotulo.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        rotulo.setTextAlign(Paint.Align.CENTER);
        rotulo.setTypeface(ResourcesCompat.getFont(context, R.font.outfit));
        valorMaximo.set(rotulo);
        valorMaximo.setTextAlign(Paint.Align.LEFT);
    }

    public void definir(List<Relatorio.Ponto> novos, Tipo novoTipo) {
        dados = novos == null ? new ArrayList<>() : novos;
        tipo = novoTipo;
        StringBuilder descricao = new StringBuilder();
        for (Relatorio.Ponto p : dados) {
            descricao.append(p.rotulo).append(' ').append(Formatters.moeda(p.valor)).append("; ");
        }
        setContentDescription(getContext().getString(R.string.grafico_descricao, descricao.toString()));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dados.isEmpty()) {
            return;
        }
        float esquerda = getPaddingLeft();
        float topo = getPaddingTop() + 18 * densidade;
        float direita = getWidth() - getPaddingRight();
        float base = getHeight() - getPaddingBottom() - 22 * densidade;
        float altura = base - topo;
        double maximo = 0;
        for (Relatorio.Ponto p : dados) {
            maximo = Math.max(maximo, valor(p));
        }
        double escala = maximo <= 0 ? 1 : maximo;

        for (int i = 0; i <= 3; i++) {
            float y = topo + altura * i / 3f;
            canvas.drawLine(esquerda, y, direita, y, grade);
        }
        canvas.drawText(Formatters.moeda(BigDecimal.valueOf(maximo)), esquerda, topo - 6 * densidade, valorMaximo);

        int n = dados.size();
        float passo = (direita - esquerda) / n;
        caminho.reset();
        for (int i = 0; i < n; i++) {
            Relatorio.Ponto p = dados.get(i);
            float centro = esquerda + passo * i + passo / 2f;
            float y = base - (float) (valor(p) / escala) * altura;
            if (tipo == Tipo.BARRAS) {
                float meia = Math.min(passo * 0.32f, 14 * densidade);
                barra.set(centro - meia, y, centro + meia, base);
                canvas.drawRoundRect(barra, 4 * densidade, 4 * densidade, pontos);
            } else {
                if (i == 0) {
                    caminho.moveTo(centro, y);
                } else {
                    caminho.lineTo(centro, y);
                }
                canvas.drawCircle(centro, y, 4 * densidade, pontos);
            }
            canvas.drawText(p.rotulo, centro, base + 16 * densidade, rotulo);
        }
        if (tipo == Tipo.LINHA) {
            canvas.drawPath(caminho, serie);
        }
    }

    private static double valor(Relatorio.Ponto p) {
        return p.valor == null ? 0 : p.valor.doubleValue();
    }
}
