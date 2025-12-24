package com.brendan.dadlibs.ui.editor;

import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

import com.brendan.dadlibs.engine.Placeholder;

import java.util.function.Consumer;

public class PlaceholderClickSpan extends ClickableSpan implements ModeSwitching {

    private final Placeholder placeholder;
    private final Consumer<Placeholder> onInsertClick;
    private final Consumer<Placeholder> onClick;
    private boolean insertMode = false;

    public PlaceholderClickSpan(Placeholder placeholder, Consumer<Placeholder> onClick,
                                Consumer<Placeholder> onInsertClick){
        this.placeholder = placeholder;
        this.onClick = onClick;
        this.onInsertClick = onInsertClick;
    }

    @Override
    public void onClick(@NonNull View widget) {
        if (insertMode){
            onInsertClick.accept(placeholder);
            insertMode = false;
        } else {
            onClick.accept(placeholder);
        }
    }

    @Override
    public void toInsertMode(){
        insertMode = true;
    }

    @Override
    public void exitInsertMode(){
        insertMode = false;
    }
}
