package com.brendan.dadlibs.ui.stories;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.brendan.dadlibs.dao.SavedStoryDao;
import com.brendan.dadlibs.db.AppDatabase;
import com.brendan.dadlibs.entity.SavedStory;

import java.util.function.Consumer;

public class SavedReaderViewModel extends AndroidViewModel {

    private final SavedStoryDao savedStoryDao;
    public SavedReaderViewModel(@NonNull Application application) {
        super(application);
        this.savedStoryDao = AppDatabase.getDatabase(application).savedStoryDao();
    }

    public void updateSavedStory(Long savedStoryId, Consumer<SavedStory> callback) {
        AppDatabase.executor.execute(() -> {
            SavedStory story = savedStoryDao.get(savedStoryId);
            new Handler(Looper.getMainLooper()).post(() ->
                    callback.accept(story));
        });
    }
}
