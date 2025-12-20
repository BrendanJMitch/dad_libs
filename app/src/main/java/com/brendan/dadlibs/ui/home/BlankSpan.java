package com.brendan.dadlibs.ui.home;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BlankSpan extends ReplacementSpan {

    private final int lineColor;
    private final String lengthEquivalent = " ".repeat(6);

    public BlankSpan(int lineColor){
        this.lineColor = lineColor;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text,
                       int start, int end, @Nullable Paint.FontMetricsInt fm) {
        return (int) paint.measureText(lengthEquivalent);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text,
                     int start, int end, float x, int top, int y, int bottom,
                     @NonNull Paint paint) {
        int oldColor = paint.getColor();

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        paint.setColor(lineColor);

        float lineEnd = x + paint.measureText(lengthEquivalent);
        canvas.drawLine(x, bottom, lineEnd, bottom, paint);

        paint.setColor(oldColor);
    }
}
