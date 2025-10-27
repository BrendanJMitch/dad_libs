package com.brendan.dadlibs.ui.wordlists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.engine.PartOfSpeech;
import com.brendan.dadlibs.entity.WordList;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class WordListDialog {

    Context context;

    public WordListDialog(Context context) {
        this.context = context;
    }

    public void show(){
        getDialog(context.getString(R.string.new_word_list), "", "", true).show();
    }

    public void show(WordList wordList, boolean editPosEnabled){
        getDialog(
                context.getString(R.string.edit_word_list),
                wordList.name,
                PartOfSpeech.getByLabel(wordList.partOfSpeech).toString(),
                editPosEnabled
        ).show();
    }

    private MaterialAlertDialogBuilder getDialog(
            String title, String wordListName, String posText, boolean posEnabled){

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_word_list, null);

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
        posDropdown.setText(posText);
        posLayout.setEnabled(posEnabled);
        posLayout.setHelperTextEnabled(!posEnabled);

        return new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    // Handle saving here
                    String name = nameInput.getText() != null ? nameInput.getText().toString() : "";
                    String partOfSpeech = posDropdown.getText() != null ? posDropdown.getText().toString() : "";
                })
                .setNegativeButton("Cancel", null);
    }
}
