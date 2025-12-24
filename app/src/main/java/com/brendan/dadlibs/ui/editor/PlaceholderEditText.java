package com.brendan.dadlibs.ui.editor;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;

public class PlaceholderEditText extends AppCompatEditText {

    private boolean hasTouched = false;

    public PlaceholderEditText(Context context) {
        super(context);
        init();
    }

    public PlaceholderEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlaceholderEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public InputConnection onCreateInputConnection(@NonNull EditorInfo outAttrs) {
        return new SpanInputConnection(
                super.onCreateInputConnection(outAttrs),
                true
        );
    }

    private class SpanInputConnection extends InputConnectionWrapper {

        SpanInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            Editable text = getText();
            int cursor = getSelectionStart();

            if (beforeLength == 1 && afterLength == 0 && cursor > 0) {
                PlaceholderSpan[] spans =
                        text.getSpans(cursor - 1, cursor, PlaceholderSpan.class);

                if (spans.length > 0) {
                    int start = text.getSpanStart(spans[0]);
                    int end   = text.getSpanEnd(spans[0]);
                    text.delete(start, end);
                    return true;
                }
            }

            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }

    private void init(){
        setFactories();
        //addTouchListener();
    }


    private void addTouchListener(){
        setOnTouchListener((v, event) -> {
//            if (event.getAction() == MotionEvent.ACTION_UP){
//                if (hasTouched) {
//                    return true;
//                } else {
//                    hasTouched = true;
//                    return false;
//                }
//            }
            return false; // allow normal behavior
        });
    }

    /**
     * This is a hack to overcome a performance issue that comes from using an EditText with more
     * than a few Spans in it. See
     * <a href="https://rsookram.github.io/2016/04/15/slow-textview-is-slow.html">this article</a>
     * for details of the problem and the workaround implemented here.
     */
    private void setFactories() {

        setEditableFactory(new Editable.Factory() {
            @Override
            public Editable newEditable(CharSequence source) {
                return new EditorSpannableStringBuilder(source);
            }
        });
        setSpannableFactory(new Spannable.Factory() {
            @Override
            public Spannable newSpannable(CharSequence source) {
                return new EditorSpannableStringBuilder(source);
            }
        });
    }
}
