package com.brendan.dadlibs.ui.stories;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.entity.SavedStory;
import com.brendan.dadlibs.share.SavedStoryDto;
import com.brendan.dadlibs.share.ShareCacheHelper;
import com.brendan.dadlibs.share.ShareJson;
import com.brendan.dadlibs.share.ShareMapping;
import com.brendan.dadlibs.share.SharePayload;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SavedStoriesFragment extends Fragment {

    private SavedStoryAdapter storyAdapter;
    private SavedStoriesViewModel viewModel;
    private RecyclerView storyRecycler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_stories, container, false);
        storyRecycler = fragment.findViewById(R.id.saved_story_recycler);
        viewModel = new ViewModelProvider(this).get(SavedStoriesViewModel.class);
        storyAdapter = new SavedStoryAdapter(viewModel, new SavedStoryAdapter.SavedStoryClickListener() {
            @Override
            public void onCardClick(SavedStory savedStory) {
                launchStory(savedStory);
            }

            @Override
            public void onMenuClick(View v, SavedStory savedStory) {
                showPopup(v, savedStory);
            }
        });
        storyRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        storyRecycler.setAdapter(storyAdapter);

        viewModel.updateSavedStories(stories ->
                storyAdapter.setSavedStories(stories));
        return fragment;
    }

    private void showPopup(View v, SavedStory story){
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.menu_saved_story, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_delete) {
                deleteSavedStory(story);
                return true;
            }
            if (item.getItemId() == R.id.menu_share){
                shareSavedStory(story);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void launchStory(SavedStory story){
        Bundle args = new Bundle();
        args.putLong("saved_story_id", story.id);
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_storiesFragment_to_savedReaderFragment, args);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteSavedStory(SavedStory story) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_delete, null);

        TextView messageView = dialogView.findViewById(R.id.delete_message);

        messageView.setText(String.format(getString(R.string.confirm_delete_saved_story) , story.name));
        Context context = requireContext();
        new AlertDialog.Builder(context)
                .setView(dialogView)
                .setPositiveButton(context.getString(R.string.delete), (dialog, which) -> {
                    viewModel.deleteStory(story);
                    storyAdapter.removeSavedStory(story);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void shareSavedStory(SavedStory story) {
        SavedStoryDto dto = ShareMapping.getSavedStoryDto(story);
        SharePayload payload = new SharePayload(null, null, List.of(dto));
        String json = ShareJson.toJson(payload);
        Context context = requireContext();

        File jsonFile;
        try {
            jsonFile = ShareCacheHelper.writeShareFile(context, story.name, json);
        } catch (IOException e) {
            Toast.makeText(context, "Unable to export. Try again?", Toast.LENGTH_LONG).show();
            return;
        }

        Uri uri = FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".fileprovider",
                jsonFile);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(
                Intent.createChooser(intent, "Share Saved Stories"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
