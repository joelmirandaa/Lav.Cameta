package br.com.lavicameta.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import br.com.lavicameta.R;

/** LinearLayout com largura máxima, para centralizar formulários em tablets (guia, seção 8). */
public class MaxWidthLinearLayout extends LinearLayout {

    private final int larguraMaxima;

    public MaxWidthLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MaxWidthLinearLayout);
        larguraMaxima = a.getDimensionPixelSize(R.styleable.MaxWidthLinearLayout_larguraMaxima, Integer.MAX_VALUE);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int largura = View.MeasureSpec.getSize(widthMeasureSpec);
        if (largura > larguraMaxima) {
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(larguraMaxima, View.MeasureSpec.getMode(widthMeasureSpec));
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
