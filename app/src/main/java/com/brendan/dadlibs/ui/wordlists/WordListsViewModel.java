package com.brendan.dadlibs.ui.wordlists;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;

import com.brendan.dadlibs.dao.WordListDao;
import com.brendan.dadlibs.db.AppDatabase;
import com.brendan.dadlibs.entity.WordList;

import java.util.List;

public class WordListsViewModel extends AndroidViewModel {

    private final WordListDao wordListDao;

    public interface DataLoadedCallback {
        public void onLoaded(List<WordList> wordLists);
    }

    public WordListsViewModel(Application application) {
        super(application);
        wordListDao = AppDatabase.getDatabase(application).wordListDao();
    }

    public void updateWordLists(DataLoadedCallback callback){
        AppDatabase.executor.execute(() -> {
            List<WordList> wordLists = wordListDao.getAll();
            new Handler(Looper.getMainLooper()).post(() -> callback.onLoaded(wordLists));
        });
    }
}