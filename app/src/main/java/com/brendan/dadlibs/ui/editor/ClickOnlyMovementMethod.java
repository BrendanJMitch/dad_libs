package com.brendan.dadlibs.ui.editor;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

public class ClickOnlyMovementMethod extends ScrollingMovementMethod {

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer,
                                MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            ClickableSpan[] links;
            if (y < 0 || y > layout.getHeight()) {
                links = null;
            } else {
                int line = layout.getLineForVertical(y);
                if (x < layout.getLineLeft(line) || x > layout.getLineRight(line)) {
                    links = null;
                } else {
                    int off = layout.getOffsetForHorizontal(line, x);
                    links = buffer.getSpans(off, off, ClickableSpan.class);
                }
            }

            if (links != null && links.length != 0) {
                ClickableSpan link = links[0];
                if (action == MotionEvent.ACTION_UP) {
                    link.onClick(widget);
                }
                return true;
            }
        }
        int start = Selection.getSelectionStart(buffer);
        int end = Selection.getSelectionEnd(buffer);

        boolean result = super.onTouchEvent(widget, buffer, event);

        if (action == MotionEvent.ACTION_MOVE){
            Selection.setSelection(buffer, start, end);
        }
        return result;
    }
}

