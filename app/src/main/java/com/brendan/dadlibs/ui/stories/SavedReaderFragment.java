package com.brendan.dadlibs.ui.stories;

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

public class SavedReaderFragment extends Fragment {

    private Long savedStoryId;
    private TextView titleText;
    private TextView storyText;

    private SavedReaderViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            savedStoryId = getArguments().getLong("saved_story_id");
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceData) {

        View fragment = inflater.inflate(R.layout.fragment_saved_story_reader, container, false);
        titleText = fragment.findViewById(R.id.story_title);
        storyText = fragment.findViewById(R.id.story_text);

        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SavedReaderViewModel.class);

        viewModel.updateSavedStory(savedStoryId, story -> {
            titleText.setText(story.name);
            storyText.setText(story.text);
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
