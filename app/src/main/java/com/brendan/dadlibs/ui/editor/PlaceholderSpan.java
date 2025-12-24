package com.brendan.dadlibs.ui.editor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PlaceholderSpan extends ReplacementSpan implements ModeSwitching {

    private final String label;
    private String underlyingText;
    private final int lineColor = Color.BLACK;
    private final float chipHeight = 105;
    private final int textColor;
    private final float padding;
    private boolean insertMode = false;

    public PlaceholderSpan(String label, int textColor) {
        this.label = label;
        this.textColor = textColor;
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
        underlyingText = text.toString();

        if (insertMode) {
            float radius = (chipHeight) / 2;
            paint.setColor(textColor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(getRectCenteredOnText(paint, y, top, bottom, x), radius, radius, paint);

            paint.setColor(Color.WHITE);
            canvas.drawText(label, x + padding, y, paint);

        } else {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2f);
            paint.setColor(lineColor);
            float lineY = getLineYCoordinate(paint, y);
            canvas.drawLine(lineStart, lineY, lineEnd, lineY, paint);

            paint.setColor(textColor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText(label, x + padding, y, paint);
        }

        paint.setColor(oldColor);
    }

    @Override
    public void toInsertMode(){
        insertMode = true;
    }

    @Override
    public void exitInsertMode(){
        insertMode = false;
    }

    private RectF getRectCenteredOnText(Paint paint, int baseline, int top, int bottom, float x){
        float textHeight = paint.getTextSize();
        float center = baseline - (textHeight * 0.4f); // This looks about right
        float width = paint.measureText(label) + padding * 2f;
        float rectBottom = center + (chipHeight / 2);
        float rectTop = center - (chipHeight / 2);
        return new RectF(x, rectTop, x + width, rectBottom);
    }

    private float getLineYCoordinate(Paint paint, int baseline){
        float textHeight = paint.getTextSize();
        return baseline + (textHeight * 0.5f); // This looks about right
    }
}
