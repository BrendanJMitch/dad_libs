package com.brendan.dadlibs.ui.editor;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.MenuProvider;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.engine.Inflection;
import com.brendan.dadlibs.engine.PartOfSpeech;
import com.brendan.dadlibs.engine.Placeholder;
import com.brendan.dadlibs.engine.Replacement;
import com.brendan.dadlibs.entity.Template;
import com.brendan.dadlibs.entity.WordList;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
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

    MenuProvider menuProvider = new MenuProvider() {
        @Override
        public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
            inflater.inflate(R.menu.menu_editor_app_bar, menu);
        }

        @Override
        public boolean onMenuItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.action_done) {
                saveButtonPressed();
                return true;
            } else if (item.getItemId() == android.R.id.home){
                backArrowPressed();
                return true;
            }
            return false;
        }
    };

    OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            backArrowPressed();
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().addMenuProvider(menuProvider, getViewLifecycleOwner());
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(), backPressedCallback);
        viewModel = new ViewModelProvider(this).get(EditorViewModel.class);

        addPlaceholderButton.setOnClickListener(v -> toInsertMode());
        closeMenuButton.setOnClickListener(v -> exitInsertMode());
        viewModel.loadWordLists(this::populateInsertMenu);

        viewModel.loadTemplate(templateId, this::onTemplateLoaded);
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
        titleInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setTemplateName(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void textChanged(Editable s) {
        viewModel.setTemplateText(s.toString());
    }

    private void onTemplateLoaded(Template template){
        titleInput.setText(template.name);
        SpannableStringBuilder builder = new SpannableStringBuilder(template.text);

        for (Replacement replacement : viewModel.getAllReplacements(template.text)) {
            String display;
            try {
                display = getDisplayText(replacement.placeholder);
            } catch (IllegalArgumentException e){
                continue;
            }
            builder.setSpan(
                    new PlaceholderSpan(display, placeholderColor),
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
    }

    private String getDisplayText(Placeholder placeholder){
        try {
            return String.format(Locale.US, "%s %d %s",
                    placeholder.wordList.singularName,
                    placeholder.index,
                    placeholder.inflection.getDisplayName());
        } catch (NullPointerException e){
            throw new IllegalArgumentException("Invalid placeholder");
        }
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

    private void saveButtonPressed(){
        viewModel.setTemplateName(titleInput.getText().toString());
        viewModel.saveTemplate();
        navigateBack();
    }

    private void backArrowPressed(){
        if (viewModel.hasUnsavedChanges()){
            showSaveDialog();
        } else {
            navigateBack();
        }
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
        int start = Math.max(0, Math.min(textInput.getSelectionStart(), textInput.getSelectionEnd()));
        int end = Math.max(textInput.getSelectionStart(), textInput.getSelectionEnd());
        insertPlaceholder(placeholder, start, end);
    }

    private void insertPlaceholder(Placeholder placeholder, int start, int end) {
        Editable text = textInput.getText();
        if (text == null) return;
        String display;
        try {
            display = getDisplayText(placeholder);
        } catch (IllegalArgumentException e){
            return;
        }

        String underlying = viewModel.getUnderlyingString(placeholder);
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

    private void navigateBack(){
        NavHostFragment.findNavController(EditorFragment.this).popBackStack();
    }

    private void showSaveDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Save changes?")
                .setMessage("You have unsaved changes. Do you want to keep them?")
                .setPositiveButton("Save", (dialog, which) ->
                        saveButtonPressed())
                .setNegativeButton("Discard", (dialog, which) ->
                        navigateBack())
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void startSelectionOverrideTimer(int curserPos){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                textInput.setSelection(curserPos);
            }
        }, 100);
    }

    private void onPlaceholderClick(Placeholder placeholder, PlaceholderClickSpan span){
        //TODO: Anchor this to the correct placeholder
        List<Inflection> options = PartOfSpeech.getByLabel(placeholder.wordList.partOfSpeech)
                .getInflections();
        Editable text = Objects.requireNonNull(textInput.getText());
        int spanStart = text.getSpanStart(span);
        int spanEnd = text.getSpanEnd(span);

        PopupMenu popup = new PopupMenu(requireContext(), createAnchorView(spanStart, spanEnd));
        Menu menu = popup.getMenu();

        for (int i = 0; i < options.size(); i++) {
            Inflection inflection = options.get(i);
            menu.add(Menu.NONE, i, i, inflection.getDisplayName());
        }

        popup.setOnMenuItemClickListener(item -> {
            Placeholder newPlaceholder= new Placeholder(
                    placeholder.wordList, options.get(item.getItemId()), placeholder.index);
            insertPlaceholder(newPlaceholder, spanStart, spanEnd);
            return true;
        });

        popup.show();
    }

    private View createAnchorView(int start, int end){
        Layout layout = textInput.getLayout();
        if (layout == null) return textInput;

        int[] location = new int[2];
        textInput.getLocationOnScreen(location);

        int line = layout.getLineForOffset(start);
        Rect bounds = new Rect(
                (int) (location[0] + layout.getPrimaryHorizontal(start)),
                (location[1] + layout.getLineTop(line)),
                (int) (location[0] + layout.getPrimaryHorizontal(end)),
                (location[1] + layout.getLineBottom(line)));

        View anchor = new View(requireContext());
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(1, 1);
        params.leftMargin = bounds.centerX();
        params.topMargin  = bounds.bottom;

        ViewGroup root = (ViewGroup) requireActivity()
                .getWindow()
                .getDecorView();

        root.addView(anchor, params);
        return anchor;
    }

    private void onPlaceholderInsertClick(Placeholder placeholder){
        insertPlaceholder(placeholder);
        exitInsertMode();
    }

}
