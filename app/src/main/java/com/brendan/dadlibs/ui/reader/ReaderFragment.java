package com.brendan.dadlibs.ui.reader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.brendan.dadlibs.R;

public class ReaderFragment extends Fragment {

    private Long templateId;
    private TextView titleText;
    private TextView storyText;

    private ReaderViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            templateId = getArguments().getLong("template_id");
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceData) {

        View fragment = inflater.inflate(R.layout.fragment_reader, container, false);
        titleText = fragment.findViewById(R.id.story_title);
        storyText = fragment.findViewById(R.id.story_text);
        fragment.findViewById(R.id.save_story_button).setOnClickListener(v ->
                new SaveStoryDialog(requireContext()).show(viewModel.getStory(), story ->
                        viewModel.saveStory(story)));
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ReaderViewModel.class);

        viewModel.updateTemplate(templateId, story -> {
            titleText.setText(story.name);
            storyText.setText(story.text);
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
