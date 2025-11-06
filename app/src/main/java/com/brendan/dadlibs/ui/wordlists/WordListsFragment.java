package com.brendan.dadlibs.ui.wordlists;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.engine.PartOfSpeech;
import com.brendan.dadlibs.entity.WordList;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class WordListsFragment extends Fragment {

    private WordListAdapter wordListAdapter;
    private WordListsViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_word_lists, container, false);
        FloatingActionButton newWordListButton = fragment.findViewById(R.id.new_word_list_button);
        RecyclerView wordListRecycler = fragment.findViewById(R.id.word_list_recycler);

        wordListAdapter = new WordListAdapter(new WordListAdapter.OnClickListener() {
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
            new WordListDialog(requireContext()).show((String name, PartOfSpeech partofSpeech) -> {
                WordList wordList = new WordList(name, false, partofSpeech.label);
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
            return false;
        });
        popup.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void editWordList(WordList wordList){
        new WordListDialog(requireContext()).show(wordList, viewModel.isEmpty(wordList), (String name, PartOfSpeech partofSpeech) -> {
            wordList.partOfSpeech = partofSpeech.label;
            wordList.name = name;
            wordListAdapter.notifyDataSetChanged();
            viewModel.updateWordList(wordList);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteWordList(WordList wordList) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_delete, null);

        TextView messageView = dialogView.findViewById(R.id.delete_message);

        messageView.setText(String.format(getString(R.string.confirm_delete_word_list) , wordList.name));

        new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteWordList(wordList);
                    wordListAdapter.removeWordList(wordList);
                    wordListAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}