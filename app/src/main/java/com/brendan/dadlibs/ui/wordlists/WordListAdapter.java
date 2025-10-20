package com.brendan.dadlibs.ui.wordlists;

import android.util.Log;
import android.util.TypedValue;
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

    public interface OnWordListClickListener {
        void onClick(WordList wordList);
    }

    private List<WordList> wordLists;
    private final OnWordListClickListener wordListClickListener;
    private final OnWordListClickListener menuClickListener;

    public WordListAdapter(OnWordListClickListener wordListClickListener, OnWordListClickListener menuClickListener) {
        this.menuClickListener = menuClickListener;
        this.wordListClickListener = wordListClickListener;
        this.wordLists = new ArrayList<>();
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
        holder.preview.setText("Placeholder!");

        holder.itemView.setOnClickListener(v -> wordListClickListener.onClick(wordList));
        holder.menuButton.setOnClickListener(v -> menuClickListener.onClick(wordList));
    }

    @Override
    public int getItemCount() {
        if (wordLists != null)
            return wordLists.size();
        else
            return 0;
    }

    public void setWordLists(List<WordList> wordLists){
        this.wordLists = wordLists;
    }
}
