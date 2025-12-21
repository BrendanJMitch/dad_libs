package com.brendan.dadlibs.ui.editor;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.engine.Placeholder;
import com.brendan.dadlibs.engine.Replacement;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class EditorFragment extends Fragment {
    private Long templateId;
    private EditorViewModel viewModel;
    private TextInputEditText titleInput;
    private TextInputEditText textInput;
    private View insertPlaceholderMenu;
    private View addPlaceholderButton;
    private View closeMenuButton;


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
        insertPlaceholderMenu = fragment.findViewById(R.id.insert_menu);
        addPlaceholderButton = fragment.findViewById(R.id.new_placeholder_button);
        closeMenuButton = fragment.findViewById(R.id.close_insert_menu);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EditorViewModel.class);

        addPlaceholderButton.setOnClickListener(v -> showInsertMenu());
        closeMenuButton.setOnClickListener(v -> hideInsertMenu());

        if (templateId == null)
            return;
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

    public String getDisplayString(Placeholder placeholder){
        return String.format(Locale.US, "%s %d %s",
                placeholder.wordList.singularName,
                placeholder.index,
                placeholder.inflection.getDisplayName());
    }

    private void showInsertMenu() {
        if (insertPlaceholderMenu.getVisibility() == View.VISIBLE)
            return;

        insertPlaceholderMenu.setVisibility(View.VISIBLE);
        addPlaceholderButton.setVisibility(View.GONE);
    }

    private void hideInsertMenu() {
        insertPlaceholderMenu.setVisibility(View.GONE);
        addPlaceholderButton.setVisibility(View.VISIBLE);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
