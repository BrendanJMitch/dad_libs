package com.brendan.dadlibs.ui.words;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.entity.Word;

import java.util.ArrayList;
import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    public static class WordViewHolder extends RecyclerView.ViewHolder {

        TextView word;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            word = itemView.findViewById(R.id.word_text);
        }
    }

    public interface OnWordClickListener {
        void onClick(Word word);
    }

    private List<Word> words;
    private final OnWordClickListener wordClickListener;

    public WordAdapter(OnWordClickListener wordClickListener) {
        this.wordClickListener = wordClickListener;
        this.words = new ArrayList<>();
    }


    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.word_card, parent, false);
        return new WordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int index) {
        Word word = words.get(index);

        holder.word.setText(word.word);

        TypedValue typedValue = new TypedValue();
        boolean found = holder.word.getContext().getTheme()
                .resolveAttribute(
                        com.google.android.material.R.attr.textAppearanceTitleMedium,
                        typedValue,
                        true);

        if (found) {
            int styleResId = typedValue.resourceId;
            holder.word.setTextAppearance(styleResId);
        }

        holder.itemView.setOnClickListener(v -> wordClickListener.onClick(word));
    }

    @Override
    public int getItemCount() {
        if (words != null)
            return words.size();
        else
            return 0;
    }

    public void setWords(List<Word> words){
        this.words = words;
    }
}
