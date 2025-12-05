package com.brendan.dadlibs.ui.words;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.engine.Inflection;
import com.brendan.dadlibs.engine.PartOfSpeech;
import com.brendan.dadlibs.entity.Word;
import com.brendan.dadlibs.entity.WordList;
import com.brendan.dadlibs.ui.wordlists.WordListDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WordDialog {

    public interface OnSaveListener {
        void onSave(String word, Map<Inflection, String> inflectedForms);
    }

    public interface OnInflectionUpdateCallback {
        void onUpdate(Map<Inflection, String> inflections);
    }

    private final Context context;
    private final WordsViewModel viewModel;
    private final Map<Inflection, TextInputEditText> inflectionInputs;
    private LayoutInflater inflater;
    private final AlertDialog dialog;
    private final TextInputEditText nameInput;
    private final TextInputLayout nameLayout;
    private final MaterialTextView inflectedFormsHeader;
    private final LinearLayout inflectedFormsLayout;
    private final String title;
    private Word word;


    public WordDialog(Context context, WordsViewModel viewModel, Word word) {
        this(context, viewModel, context.getString(R.string.new_word));
        this.word = word;
        nameInput.setText(word.word);
        viewModel.loadWordInflections(word, this::updateInflections);
    }

    public WordDialog(Context context, WordsViewModel viewModel) {
        this(context, viewModel, context.getString(R.string.new_word));
    }

    private WordDialog(Context context, WordsViewModel viewModel, String title) {
        this.context = context;
        this.viewModel = viewModel;
        this.title = title;
        this.inflectionInputs = new HashMap<>();

        inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_word, null);

        nameLayout = dialogView.findViewById(R.id.word_name_layout);
        nameInput = dialogView.findViewById(R.id.name_input);
        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.buildWordInflections(s.toString(), WordDialog.this::updateInflections);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        inflectedFormsHeader = dialogView.findViewById(R.id.inflected_forms_header);
        inflectedFormsLayout = dialogView.findViewById(R.id.inflected_forms_layout);
        inflectedFormsLayout.removeAllViews();
        updateInflectionEntries(viewModel.getPartOfSpeech());
        dialog = getDialog(dialogView);
    }

    private AlertDialog getDialog(View view){
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    public void show(){
        this.dialog.show();
    }

    private void updateInflectionEntries(PartOfSpeech partOfSpeech){
        for (Inflection inflection : partOfSpeech.getNonBaseInflections()){
            View inputView = inflater.inflate(
                    R.layout.inflected_form_input,
                    inflectedFormsLayout,
                    false);

            TextInputLayout inputLayout = inputView.findViewById(R.id.inflected_form_input_layout);
            TextInputEditText inputEditText = inputView.findViewById(R.id.inflected_form_input);
            inflectionInputs.put(inflection, inputEditText);

            inputLayout.setHint(inflection.getDisplayName());
            inputView.setTag(inflection);
            inflectedFormsLayout.addView(inputView);
        }
        if (!partOfSpeech.getNonBaseInflections().isEmpty()) {
            nameLayout.setHint(String.format(
                    context.getString(R.string.word_base_form),
                    partOfSpeech.getBaseInflection().getDisplayName()));
        } else {
            nameLayout.setHint(context.getString(R.string.new_word));
            inflectedFormsHeader.setVisibility(View.GONE);
        }
    }

    private void updateInflections(Map<Inflection, String> inflections){
        for (Map.Entry<Inflection, TextInputEditText> entry : inflectionInputs.entrySet()) {
            entry.getValue().setText(inflections.get(entry.getKey()));
        }
    }

    private void onSave(){}
}
