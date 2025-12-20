package com.brendan.dadlibs.ui.editor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.style.ReplacementSpan;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PlaceholderSpan extends ReplacementSpan {

    private final String label;
    private final int lineColor = Color.BLACK;
    private final int textColor;
    private final float padding;

    public PlaceholderSpan(String label, Context context) {
        this.label = label;
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorSecondaryVariant, tv, true);
        this.textColor = tv.data;
        this.padding = 32f; // px padding around text
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text,
                       int start, int end, @Nullable Paint.FontMetricsInt fm) {
        // Measure width of the label + padding
        float textWidth = paint.measureText(label);
        return (int) (textWidth + padding * 2);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text,
                     int start, int end, float x, int top, int y, int bottom,
                     @NonNull Paint paint) {
        int oldColor = paint.getColor();
        float lineStart = x;
        float lineEnd = x + paint.measureText(label) + padding * 2f;

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);   // pixels
        paint.setColor(lineColor);

        canvas.drawLine(lineStart, bottom, lineEnd, bottom, paint);

        // Draw text centered vertically
        paint.setColor(textColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(label, x + padding, y, paint);

        // Restore paint
        paint.setColor(oldColor);
    }
}
