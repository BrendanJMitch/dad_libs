package com.brendan.dadlibs.ui.wordlists;

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
import com.brendan.dadlibs.data.entity.WordList;
import com.brendan.dadlibs.data.relation.WordListWithWords;
import com.brendan.dadlibs.engine.PartOfSpeech;
import com.brendan.dadlibs.share.ShareCacheHelper;
import com.brendan.dadlibs.share.ShareJson;
import com.brendan.dadlibs.share.ShareMapping;
import com.brendan.dadlibs.share.SharePayload;
import com.brendan.dadlibs.share.WordListDto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class WordListsFragment extends Fragment {

    private WordListAdapter wordListAdapter;
    private WordListsViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_word_lists, container, false);
        FloatingActionButton newWordListButton = fragment.findViewById(R.id.new_word_list_button);
        RecyclerView wordListRecycler = fragment.findViewById(R.id.word_list_recycler);

        wordListAdapter = new WordListAdapter(new WordListAdapter.WordListClickListener() {
            @Override
            public void onCardClick(WordList wordList) {
                openWordList(wordList);
            }

            @Override
            public void onMenuClick(View v, WordList wordList) {
                showPopup(v, wordList);
            }
        });
        wordListRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        wordListRecycler.setAdapter(wordListAdapter);

        newWordListButton.setOnClickListener(v -> {
            new WordListDialog(requireContext()).show((String name, String singularName, PartOfSpeech partofSpeech) -> {
                WordList wordList = new WordList(name, singularName, false, partofSpeech.label);
                viewModel.insertWordList(wordList, this::onWordListsLoaded);
            });
        });

        viewModel = new ViewModelProvider(this).get(WordListsViewModel.class);
        viewModel.getAllWordLists(this::onWordListsLoaded);
        return fragment;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void onWordListsLoaded(List<WordList> wordLists) {
        wordListAdapter.clear();
        for (WordList wordList : wordLists)
            wordListAdapter.addWordList(wordList, viewModel.getPreview(wordList));
        wordListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void openWordList(WordList wordList){
        Bundle args = new Bundle();
        args.putLong("word_list_id", wordList.id);
        args.putString("word_list_name", wordList.name);
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_wordListsFragment_to_wordsFragment, args);
    }

    private void showPopup(View v, WordList wordList){
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.menu_word_list, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_edit) {
                editWordList(wordList);
                return true;
            }
            if (item.getItemId() == R.id.menu_delete) {
                deleteWordList(wordList);
                return true;
            }
            if (item.getItemId() == R.id.menu_copy) {
                copyWordList(wordList);
                return true;
            }
            if (item.getItemId() == R.id.menu_share) {
                viewModel.getWordListWithWords(wordList.id, this::shareWordList);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void editWordList(WordList wordList){
        new WordListDialog(requireContext()).show(wordList, viewModel.isEmpty(wordList),
                (String name, String singularName, PartOfSpeech partofSpeech) -> {
                    wordList.partOfSpeech = partofSpeech.label;
                    wordList.name = name;
                    wordList.singularName = singularName;
                    wordListAdapter.notifyDataSetChanged();
                    viewModel.updateWordList(wordList);
                });
    }

    private void copyWordList(WordList wordList){
        viewModel.copyWordList(
                wordList,
                String.format("%s (copy)", wordList.name),
                String.format("%s (copy)", wordList.singularName),
                this::onWordListsLoaded);
    }

    private void shareWordList(WordListWithWords wordList){
        WordListDto dto = ShareMapping.getWordListDto(wordList);
        SharePayload payload = new SharePayload(null, List.of(dto), null);
        String json = ShareJson.toJson(payload);
        Context context = requireContext();

        File jsonFile;
        try {
            jsonFile = ShareCacheHelper.writeShareFile(context, wordList.wordList.name, json);
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
                Intent.createChooser(intent, "Share Word Lists"));
    }

    private void deleteWordList(WordList wordList) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_delete, null);

        TextView messageView = dialogView.findViewById(R.id.delete_message);

        messageView.setText(String.format(getString(R.string.confirm_delete_word_list) , wordList.name));

        new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteWordList(wordList);
                    wordListAdapter.removeWordList(wordList);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}