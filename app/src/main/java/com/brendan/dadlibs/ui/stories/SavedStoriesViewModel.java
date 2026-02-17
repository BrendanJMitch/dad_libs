package com.brendan.dadlibs.ui.stories;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.brendan.dadlibs.data.dao.SavedStoryDao;
import com.brendan.dadlibs.data.db.AppDatabase;
import com.brendan.dadlibs.data.entity.SavedStory;

import java.util.List;
import java.util.function.Consumer;

public class SavedStoriesViewModel extends AndroidViewModel {

    private final SavedStoryDao savedStoryDao;

    public SavedStoriesViewModel(@NonNull Application application) {
        super(application);
        savedStoryDao = AppDatabase.getDatabase(application).savedStoryDao();
    }

    public void updateSavedStories(Consumer<List<SavedStory>> callback) {
        AppDatabase.executor.execute(() -> {
            List<SavedStory> stories = savedStoryDao.getAll();
            new Handler(Looper.getMainLooper()).post(() -> callback.accept(stories));
        });
    }

    public void deleteStory(SavedStory story) {
        AppDatabase.executor.execute(() -> savedStoryDao.delete(story));
    }
}
