package com.brendan.dadlibs.ui.words;

import android.content.Context;
import android.text.Editable;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.entity.Word;

public class EditWordDialog extends WordDialog {

    public interface OnDeleteCallback {
        void onDelete();
    }

    private boolean suppressTextWatcher;
    private final OnDeleteCallback onDeleteCallback;


    public EditWordDialog(Context context, WordsViewModel viewModel, OnSaveCallback onSaveCallback, OnDeleteCallback onDeleteCallback, Word word) {
        super(context, viewModel, onSaveCallback);
        this.onDeleteCallback = onDeleteCallback;
        suppressTextWatcher = true;
        nameInput.setText(word.word);
        suppressTextWatcher = false;
        viewModel.loadWordInflections(word, this::updateInflections);
    }

    @Override
    protected void textChanged(Editable s) {
        if (!suppressTextWatcher)
            viewModel.buildWordInflections(s.toString(), super::updateInflections);
    }

    @Override
    protected AlertDialog getDialog(View view){
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.edit_word))
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .setNeutralButton(R.string.delete, null)
                .create();

        dialog.setOnShowListener(dlg -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this::onSaveButtonPressed);
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
                this.onDeleteCallback.onDelete();
                dialog.dismiss();
            });
        });

        return dialog;
    }
}
