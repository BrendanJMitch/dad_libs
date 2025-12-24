package com.brendan.dadlibs.ui.editor;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.engine.Placeholder;
import com.brendan.dadlibs.engine.Replacement;
import com.brendan.dadlibs.entity.WordList;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class EditorFragment extends Fragment {
    private Long templateId;
    private EditorViewModel viewModel;
    private TextInputEditText titleInput;
    private PlaceholderEditText textInput;
    private int placeholderColor;
    private ChipGroup insertPlaceholderGroup;
    private View insertPlaceholderMenu;
    private View addPlaceholderButton;
    private View closeMenuButton;
    private boolean keyboardWasOpen = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypedValue tv = new TypedValue();
        requireContext().getTheme().resolveAttribute(
                com.google.android.material.R.attr.colorSecondaryVariant, tv, true);
        this.placeholderColor = tv.data;

        if (getArguments() != null) {
            templateId = getArguments().getLong("template_id");
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceData) {

        View fragment = inflater.inflate(R.layout.fragment_editor, container, false);
        titleInput = fragment.findViewById(R.id.template_title_input);
        textInput = fragment.findViewById(R.id.template_text_input);
        insertPlaceholderGroup = fragment.findViewById(R.id.insert_placeholder_group);
        insertPlaceholderMenu = fragment.findViewById(R.id.insert_menu);
        addPlaceholderButton = fragment.findViewById(R.id.new_placeholder_button);
        closeMenuButton = fragment.findViewById(R.id.close_insert_menu);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EditorViewModel.class);

        addPlaceholderButton.setOnClickListener(v -> toInsertMode());
        closeMenuButton.setOnClickListener(v -> exitInsertMode());
        viewModel.loadWordLists(this::populateInsertMenu);

        viewModel.loadTemplate(templateId, template -> {
            titleInput.setText(template.name);
            SpannableStringBuilder builder = new SpannableStringBuilder(template.text);

            for (Replacement replacement : viewModel.getAllReplacements(template.text)) {
                builder.setSpan(
                        new PlaceholderSpan(getDisplayText(replacement.placeholder), placeholderColor),
                        replacement.startPos, replacement.startPos + replacement.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(
                        new PlaceholderClickSpan(
                                replacement.placeholder, this::onPlaceholderClick, this::onPlaceholderInsertClick),
                        replacement.startPos, replacement.startPos + replacement.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            textInput.setText(builder);
            textInput.setSelection(builder.length());
        });
        textInput.setMovementMethod(new ClickOnlyMovementMethod());
        textInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                textChanged(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void textChanged(Editable s) {
        viewModel.setTemplateText(s.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private String getDisplayText(Placeholder placeholder){
        return String.format(Locale.US, "%s %d %s",
                placeholder.wordList.singularName,
                placeholder.index,
                placeholder.inflection.getDisplayName());
    }

    private void populateInsertMenu(List<WordList> lists) {
        insertPlaceholderGroup.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (WordList list : lists) {
            Chip chip = (Chip) inflater.inflate(
                    R.layout.placeholder_chip,
                    insertPlaceholderGroup,
                    false
            );

            chip.setText(list.singularName);
            chip.setOnClickListener(v -> {
                insertPlaceholder(viewModel.getDefaultPlaceholder(list));
                exitInsertMode();
            });

            insertPlaceholderGroup.addView(chip);
        }
    }


    private void toInsertMode() {
        if (insertPlaceholderMenu.getVisibility() == View.VISIBLE)
            return;

        insertPlaceholderMenu.setVisibility(View.VISIBLE);
        addPlaceholderButton.setVisibility(View.GONE);
        Editable spannable = textInput.getText();
        if (spannable == null) return;

        ModeSwitching[] spans = spannable.getSpans(0, spannable.length(), ModeSwitching.class);
        for (ModeSwitching span : spans) {
            span.toInsertMode();
        }
        hideKeyboard();
        forceRedraw();
    }

    private void exitInsertMode() {
        insertPlaceholderMenu.setVisibility(View.GONE);
        addPlaceholderButton.setVisibility(View.VISIBLE);
        Editable spannable = textInput.getText();
        if (spannable == null) return;

        ModeSwitching[] spans = spannable.getSpans(0, spannable.length(), ModeSwitching.class);
        for (ModeSwitching span : spans) {
            span.exitInsertMode();
        }
        restoreKeyboard();
        forceRedraw();
    }

    private void forceRedraw(){
        Editable text = textInput.getText();
        if (text == null) return;

        int start = Selection.getSelectionStart(text);
        int end   = Selection.getSelectionEnd(text);

        textInput.setText(text);

        if (start >= 0 && end >= 0) {
            textInput.setSelection(start, end);
        }
    }

    private void hideKeyboard() {
        keyboardWasOpen = isKeyboardOpen();
        textInput.setShowSoftInputOnFocus(false);
        InputMethodManager imm =
                (InputMethodManager) requireContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textInput.getWindowToken(), 0);
    }

    private void restoreKeyboard(){
        textInput.setShowSoftInputOnFocus(true);
        if (keyboardWasOpen){
            keyboardWasOpen = false;
            InputMethodManager imm =
                    (InputMethodManager) requireContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(textInput, 0);
        }
    }

    private boolean isKeyboardOpen() {
        View root = requireView();
        WindowInsetsCompat insets = ViewCompat.getRootWindowInsets(root);
        return insets != null && insets.isVisible(WindowInsetsCompat.Type.ime());
    }

    private void insertPlaceholder(Placeholder placeholder) {
        Editable text = textInput.getText();
        if (text == null) return;

        String underlying = viewModel.getUnderlyingString(placeholder);
        String display = getDisplayText(placeholder);

        int start = Math.max(0, Math.min(textInput.getSelectionStart(), textInput.getSelectionEnd()));
        int end = Math.max(textInput.getSelectionStart(), textInput.getSelectionEnd());
        text.replace(start, end, underlying);

        int insertEnd = start + underlying.length();
        text.setSpan(
                new PlaceholderSpan(display, placeholderColor),
                start, insertEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(
                new PlaceholderClickSpan(placeholder, this::onPlaceholderClick, this::onPlaceholderInsertClick),
                start, insertEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        startSelectionOverrideTimer(insertEnd);
    }

    private void startSelectionOverrideTimer(int curserPos){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                textInput.setSelection(curserPos);
            }
        }, 100);
    }

    private void onPlaceholderClick(Placeholder placeholder){

    }

    private void onPlaceholderInsertClick(Placeholder placeholder){
        insertPlaceholder(placeholder);
        exitInsertMode();
    }

}
