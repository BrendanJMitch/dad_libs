package com.brendan.dadlibs.ui.words;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;

import com.brendan.dadlibs.dao.WordDao;
import com.brendan.dadlibs.dao.WordListDao;
import com.brendan.dadlibs.db.AppDatabase;
import com.brendan.dadlibs.entity.Word;
import com.brendan.dadlibs.entity.WordList;

import java.util.List;

public class WordsViewModel extends AndroidViewModel {

    private final WordDao wordDao;

    public interface DataLoadedCallback {
        void onLoaded(List<Word> words);
    }

    public WordsViewModel(Application application) {
        super(application);
        wordDao = AppDatabase.getDatabase(application).wordDao();
    }

    public void updateWords(long wordListId, DataLoadedCallback callback){
        AppDatabase.executor.execute(() -> {
            List<Word> words = wordDao.getAllFromList(wordListId);
            new Handler(Looper.getMainLooper()).post(() -> callback.onLoaded(words));
        });
    }
}