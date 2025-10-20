package com.brendan.dadlibs.ui.wordlists;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.entity.WordList;

import java.util.List;

public class WordListsFragment extends Fragment {

    private WordListAdapter wordListAdapter;
    private WordListsViewModel viewModel;
    private RecyclerView wordListRecycler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_word_lists, container, false);
        wordListRecycler = fragment.findViewById(R.id.word_list_recycler);
        viewModel = new ViewModelProvider(this).get(WordListsViewModel.class);

        wordListAdapter = new WordListAdapter(wordList -> {
            Bundle args = new Bundle();
            args.putLong("word_list_id", wordList.id);
            args.putString("word_list_name", wordList.name);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_wordListsFragment_to_wordsFragment, args);
        }, wordList -> {});
        wordListRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        wordListRecycler.setAdapter(wordListAdapter);

        viewModel.updateWordLists(new WordListsViewModel.DataLoadedCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onLoaded(List<WordList> wordLists) {
                wordListAdapter.setWordLists(wordLists);
                wordListAdapter.notifyDataSetChanged();
            }
        });

        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}