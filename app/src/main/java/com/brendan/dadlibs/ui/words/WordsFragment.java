package com.brendan.dadlibs.ui.words;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.entity.Word;

import java.util.List;

public class WordsFragment extends Fragment {

    private long wordListId;
    private WordAdapter wordAdapter;
    private WordsViewModel viewModel;
    private RecyclerView wordRecycler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            wordListId = getArguments().getLong("word_list_id");
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_word_lists, container, false);
        wordRecycler = fragment.findViewById(R.id.word_list_recycler);
        viewModel = new ViewModelProvider(this).get(WordsViewModel.class);

        wordAdapter = new WordAdapter(word -> {});
        wordRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        wordRecycler.setAdapter(wordAdapter);

        viewModel.updateWords(wordListId, new WordsViewModel.DataLoadedCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onLoaded(List<Word> words) {
                wordAdapter.setWords(words);
                wordAdapter.notifyDataSetChanged();
            }
        });

        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}