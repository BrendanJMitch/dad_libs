package com.brendan.dadlibs.ui.init;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class LogoBounceView extends View {

    private float x, y, dx, dy;

    public LogoBounceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        dx = 5;
        dy = 7;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw logo bitmap at (x, y)
    }
}

