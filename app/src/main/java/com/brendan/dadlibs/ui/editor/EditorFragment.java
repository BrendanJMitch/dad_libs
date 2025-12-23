package com.brendan.dadlibs.ui.editor;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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

public class EditorFragment extends Fragment {
    private Long templateId;
    private EditorViewModel viewModel;
    private TextInputEditText titleInput;
    private TextInputEditText textInput;
    private ChipGroup insertPlaceholderGroup;
    private View insertPlaceholderMenu;
    private View addPlaceholderButton;
    private View closeMenuButton;
    private boolean keyboardWasOpen = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            templateId = getArguments().getLong("template_id");
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceData) {

        View fragment = inflater.inflate(R.layout.fragment_editor, container, false);
        titleInput = fragment.findViewById(R.id.template_title_input);
        textInput = fragment.findViewById(R.id.template_text_input);
        setPartTextFactories(textInput);
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
                        new PlaceholderSpan(getDisplayString(replacement.placeholder), requireContext()),
                        replacement.startPos, replacement.startPos + replacement.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            textInput.setText(builder);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private String getDisplayString(Placeholder placeholder){
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
                //insertPlaceholder(list.placeholder);
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

        PlaceholderSpan[] spans = spannable.getSpans(0, spannable.length(), PlaceholderSpan.class);
        for (PlaceholderSpan span : spans) {
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

        PlaceholderSpan[] spans = spannable.getSpans(0, spannable.length(), PlaceholderSpan.class);
        for (PlaceholderSpan span : spans) {
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

    /**
     * This is a hack to overcome a performance issue that comes from using an EditText with more
     * than a few Spans in it. See
     * <a href="https://rsookram.github.io/2016/04/15/slow-textview-is-slow.html">this article</a>
     * for details of the problem and the workaround implemented here.
     *
     * @param editText: the EditText need
     */
    private void setPartTextFactories(EditText editText) {

        editText.setEditableFactory(new Editable.Factory() {
            @Override
            public Editable newEditable(CharSequence source) {
                return new EditorSpannableStringBuilder(source);
            }
        });
        editText.setSpannableFactory(new Spannable.Factory() {
            @Override
            public Spannable newSpannable(CharSequence source) {
                return new EditorSpannableStringBuilder(source);
            }
        });
    }
}
