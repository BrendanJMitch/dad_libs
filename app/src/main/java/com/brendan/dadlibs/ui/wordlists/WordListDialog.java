package com.brendan.dadlibs.ui.wordlists;

import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AlertDialog;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.engine.PartOfSpeech;
import com.brendan.dadlibs.entity.WordList;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class WordListDialog {

    public interface OnSaveListener{
        void onSave(String name, PartOfSpeech partOfSpeech);
    }

    private final Context context;

    public WordListDialog(Context context) {
        this.context = context;
    }

    public void show(OnSaveListener onSaveListener){
        getDialog(context.getString(R.string.new_word_list), "", null, true, onSaveListener).show();
    }

    public void show(WordList wordList, boolean editPosEnabled, OnSaveListener onSaveListener){
        getDialog(
                context.getString(R.string.edit_word_list),
                wordList.name,
                PartOfSpeech.getByLabel(wordList.partOfSpeech).toString(),
                editPosEnabled,
                onSaveListener
        ).show();
    }

    private AlertDialog getDialog(
            String title, String wordListName, String posName, boolean posEnabled, OnSaveListener onSaveListener) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_word_list, null);

        TextInputLayout nameLayout = dialogView.findViewById(R.id.word_list_name_layout);
        TextInputEditText nameInput = dialogView.findViewById(R.id.name_input);
        TextInputLayout posLayout = dialogView.findViewById(R.id.part_of_speech_layout);
        AutoCompleteTextView posDropdown = dialogView.findViewById(R.id.part_of_speech_dropdown);

        ArrayAdapter<PartOfSpeech> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_dropdown_item_1line,
                PartOfSpeech.values()
        );
        posDropdown.setAdapter(adapter);
        nameInput.setText(wordListName);
        posLayout.setEnabled(posEnabled);
        posLayout.setHelperTextEnabled(!posEnabled);
        posDropdown.setText(posName);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(dialogView)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dlg -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                Editable name = nameInput.getText();
                Editable posText = posDropdown.getText();
                boolean shouldDismiss = true;
                if (name == null || name.toString().trim().isEmpty()) {
                    nameLayout.setErrorEnabled(true);
                    nameLayout.setError("Please give your word list a name!");
                    shouldDismiss = false;
                } else {
                    nameLayout.setErrorEnabled(false);
                }
                if (posText == null || PartOfSpeech.getByDisplayName(posText.toString()) == null) {
                    posLayout.setErrorEnabled(true);
                    posLayout.setError("Please select part of speech!");
                    shouldDismiss = false;
                } else {
                    posLayout.setErrorEnabled(false);
                }
                if (shouldDismiss){
                    onSaveListener.onSave(name.toString().trim(), PartOfSpeech.getByDisplayName(posText.toString()));
                    dialog.dismiss();
                }
            });
        });
        return dialog;
    }
}
