package com.brendan.dadlibs.ui.stories;

import android.annotation.SuppressLint;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.entity.SavedStory;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SavedStoryAdapter extends RecyclerView.Adapter<SavedStoryAdapter.SavedStoryViewHolder> {

    public static class SavedStoryViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView preview;
        TextView date;
        ImageView menuButton;

        public SavedStoryViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.story_title);
            date = itemView.findViewById(R.id.story_date);
            preview = itemView.findViewById(R.id.story_preview);
            menuButton = itemView.findViewById(R.id.story_menu);
            preview.post(() -> {
                LinearGradient shader = new LinearGradient(
                        0, 0, 0, preview.getHeight(),
                        new int[]{
                                0xFF000000,   // fully opaque at top
                                0x00000000    // fully transparent at bottom
                        },
                        null,
                        Shader.TileMode.CLAMP
                );
                preview.getPaint().setShader(shader);
                preview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            });
        }
    }

    public interface SavedStoryClickListener {
        void onCardClick(SavedStory savedStory);
        void onMenuClick(View v, SavedStory savedStory);
    }

    private List<SavedStory> savedStories;
    private final SavedStoryClickListener savedStoryClickListener;
    private final SavedStoriesViewModel viewModel;

    public SavedStoryAdapter(SavedStoriesViewModel viewModel, SavedStoryClickListener savedStoryClickListener) {
        this.viewModel = viewModel;
        this.savedStoryClickListener = savedStoryClickListener;
        this.savedStories = new ArrayList<>();
    }


    @NonNull
    @Override
    public SavedStoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_story_card, parent, false);
        return new SavedStoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedStoryViewHolder holder, int index) {
        SavedStory savedStory = savedStories.get(index);

        holder.title.setText(savedStory.name);
        holder.preview.setText(savedStory.text);
        java.util.Date date = new java.util.Date(savedStory.timestamp*1000);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.US);
        String dateString = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .format(formatter);
        holder.date.setText(dateString);

        holder.itemView.setOnClickListener(v -> savedStoryClickListener.onCardClick(savedStory));
        holder.menuButton.setOnClickListener(v -> savedStoryClickListener.onMenuClick(v, savedStory));
    }

    @Override
    public int getItemCount() {
        if (savedStories != null)
            return savedStories.size();
        else
            return 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSavedStories(List<SavedStory> savedStorys){
        this.savedStories = savedStorys;
        notifyDataSetChanged();
    }

    public void addSavedStorys(List<SavedStory> savedStorys){
        this.savedStories.addAll(savedStorys);
        notifyItemRangeInserted(this.savedStories.size() - savedStorys.size(), savedStorys.size());
    }

    public void removeSavedStory(SavedStory savedStory){
        for (int i = 0; i < savedStories.size(); i++) {
            if (Objects.equals(savedStories.get(i).id, savedStory.id)) {
                savedStories.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }
}
