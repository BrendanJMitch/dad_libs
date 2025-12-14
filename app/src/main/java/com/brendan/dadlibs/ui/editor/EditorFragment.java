package com.brendan.dadlibs.ui.editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.ui.reader.ReaderViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class EditorFragment extends Fragment {
    private Long templateId;
    private EditorViewModel viewModel;
    private TextInputEditText titleInput;
    private TextInputEditText textInput;

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
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EditorViewModel.class);
        viewModel.loadTemplate(templateId, template -> {
            titleInput.setText(template.name);
            textInput.setText(template.text);
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
