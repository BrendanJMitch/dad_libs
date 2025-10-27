package com.brendan.dadlibs.ui.wordlists;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.entity.WordList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordListViewHolder> {

    public static class WordListViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView preview;
        ImageView menuButton;

        public WordListViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.word_list_title);
            preview = itemView.findViewById(R.id.word_list_preview);
            menuButton = itemView.findViewById(R.id.word_list_menu);
        }
    }

    public interface OnClickListener {
        void onCardClick(WordList wordList);
        void onMenuClick(View v, WordList wordList);
    }

    private List<WordList> wordLists;
    private final Map<Long, String> previews;
    private final OnClickListener onClickListener;

    public WordListAdapter(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.wordLists = new ArrayList<>();
        this.previews = new TreeMap<>();
    }


    @NonNull
    @Override
    public WordListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.word_list_card, parent, false);
        return new WordListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WordListViewHolder holder, int index) {
        WordList wordList = wordLists.get(index);

        holder.title.setText(wordList.name);
        holder.preview.setText(previews.get(wordList.id));

        holder.itemView.setOnClickListener(v -> onClickListener.onCardClick(wordList));
        holder.menuButton.setOnClickListener(v -> onClickListener.onMenuClick(v, wordList));
    }

    @Override
    public int getItemCount() {
        if (wordLists != null)
            return wordLists.size();
        else
            return 0;
    }

    public void addWordList(WordList wordList, String preview){
        this.wordLists.add(wordList);
        this.previews.put(wordList.id, preview);
    }

    public void removeWordList(WordList wordList){
        this.wordLists.remove(wordList);
    }
}