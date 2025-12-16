package com.brendan.dadlibs.ui.words;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.engine.Inflection;
import com.brendan.dadlibs.engine.PartOfSpeech;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WordDialog {

    public interface OnSaveCallback {
        void onSave(String word, Map<Inflection, String> inflectedForms);
    }

    protected final Context context;
    protected final WordsViewModel viewModel;
    protected final Map<Inflection, TextInputEditText> inflectionInputs;
    protected final Map<Inflection, TextInputLayout> inflectionLayouts;
    protected final LayoutInflater inflater;
    protected final AlertDialog dialog;
    protected final TextInputEditText nameInput;
    protected final TextInputLayout nameLayout;
    protected final MaterialTextView inflectedFormsHeader;
    protected final LinearLayout inflectedFormsLayout;
    protected final OnSaveCallback onSaveCallback;

    protected WordDialog(Context context, WordsViewModel viewModel, OnSaveCallback onSaveCallback) {
        this.context = context;
        this.viewModel = viewModel;
        this.inflectionInputs = new HashMap<>();
        this.inflectionLayouts = new HashMap<>();
        this.onSaveCallback = onSaveCallback;

        inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_word, null);

        nameLayout = dialogView.findViewById(R.id.word_name_layout);
        nameInput = dialogView.findViewById(R.id.name_input);
        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                textChanged(s);
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

    protected AlertDialog getDialog(View view){
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.new_word))
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener(dlg -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this::onSaveButtonPressed);
        });

        return dialog;
    }

    protected void textChanged(Editable s) {
        viewModel.buildWordInflections(s.toString(), WordDialog.this::updateInflections);
    }

    public void show(){
        this.dialog.show();
    }

    protected void updateInflectionEntries(PartOfSpeech partOfSpeech){
        for (Inflection inflection : partOfSpeech.getNonBaseInflections()){
            View inputView = inflater.inflate(
                    R.layout.inflected_form_input,
                    inflectedFormsLayout,
                    false);

            TextInputLayout inputLayout = inputView.findViewById(R.id.inflected_form_input_layout);
            TextInputEditText inputEditText = inputView.findViewById(R.id.inflected_form_input);
            inflectionInputs.put(inflection, inputEditText);
            inflectionLayouts.put(inflection, inputLayout);

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

    protected void updateInflections(Map<Inflection, String> inflections){
        for (Map.Entry<Inflection, TextInputEditText> entry : inflectionInputs.entrySet()) {
            entry.getValue().setText(inflections.get(entry.getKey()));
        }
    }

    protected void onSaveButtonPressed(View v) {
        boolean shouldDismiss = true;
        Editable word = nameInput.getText();
        if (word == null || word.toString().trim().isEmpty()) {
            nameLayout.setErrorEnabled(true);
            nameLayout.setError(String.format(
                    context.getString(R.string.unspecified_form_error),
                    viewModel.getPartOfSpeech().getBaseInflection().getDisplayName().toLowerCase()));
            shouldDismiss = false;
        } else {
            nameLayout.setErrorEnabled(false);
        }
        for (Map.Entry<Inflection, TextInputEditText> entry : inflectionInputs.entrySet()) {
            Editable inflectedForm = entry.getValue().getText();
            TextInputLayout layout = Objects.requireNonNull(inflectionLayouts.get(entry.getKey()));
            if (inflectedForm == null || inflectedForm.toString().trim().isEmpty()) {
                layout.setErrorEnabled(true);
                layout.setError(String.format(
                        context.getString(R.string.unspecified_form_error),
                        entry.getKey().getDisplayName().toLowerCase()));
                shouldDismiss = false;
            } else {
                layout.setErrorEnabled(false);
            }
        }
        if (shouldDismiss){
            onSaveCallback.onSave(word.toString(), getInflectedForms());
            dialog.dismiss();
        }
    }

    protected Map<Inflection, String> getInflectedForms(){
        Map<Inflection, String> inflectedForms = new HashMap<>();
        for (Map.Entry<Inflection, TextInputEditText> entry : inflectionInputs.entrySet()) {
            String text = Objects.requireNonNull(entry.getValue().getText()).toString();
            inflectedForms.put(entry.getKey(), text);
        }
        inflectedForms.put(
                viewModel.getPartOfSpeech().getBaseInflection(),
                Objects.requireNonNull(nameInput.getText()).toString());
        return inflectedForms;
    }
}
