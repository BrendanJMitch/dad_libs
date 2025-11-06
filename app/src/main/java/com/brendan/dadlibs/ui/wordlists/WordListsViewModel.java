package com.brendan.dadlibs.ui.wordlists;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;

import com.brendan.dadlibs.dao.WordDao;
import com.brendan.dadlibs.dao.WordListDao;
import com.brendan.dadlibs.db.AppDatabase;
import com.brendan.dadlibs.entity.Word;
import com.brendan.dadlibs.entity.WordList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WordListsViewModel extends AndroidViewModel {

    private final WordListDao wordListDao;
    private final WordDao wordDao;
    private final Map<Long, List<Word>> wordLists;

    public interface DataLoadedCallback {
        void onLoaded(List<WordList> wordLists);
    }

    public WordListsViewModel(Application application) {
        super(application);
        wordListDao = AppDatabase.getDatabase(application).wordListDao();
        wordDao = AppDatabase.getDatabase(application).wordDao();
        wordLists = new HashMap<>();
    }

    public void getAllWordLists(DataLoadedCallback callback){
        AppDatabase.executor.execute(() -> {
            List<WordList> wordLists = wordListDao.getAll();
            for (WordList wordList : wordLists){
                this.wordLists.put(wordList.id, wordDao.getAllFromList(wordList.id));
            }
            new Handler(Looper.getMainLooper()).post(() -> callback.onLoaded(wordLists));
        });
    }

    public void updateWordList(WordList wordList){
        AppDatabase.executor.execute(() -> wordListDao.update(wordList));
    }

    public void insertWordList(WordList wordList, DataLoadedCallback callback){;
        wordLists.clear();
        AppDatabase.executor.execute(() -> {
            wordListDao.insert(wordList);
            List<WordList> wordLists = wordListDao.getAll();
            for (WordList list : wordLists){
                this.wordLists.put(list.id, wordDao.getAllFromList(list.id));
            }
            new Handler(Looper.getMainLooper()).post(() -> callback.onLoaded(wordLists));
        });
    }

    public void deleteWordList(WordList wordList){
        AppDatabase.executor.execute(() -> wordListDao.delete(wordList));
        wordLists.remove(wordList.id);
    }

    public boolean isEmpty(WordList wordList){
        return Objects.requireNonNull(wordLists.get(wordList.id)).isEmpty();
    }

    public String getPreview(WordList wordList){
        StringBuilder preview = new StringBuilder();
        for (Word word : Objects.requireNonNull(wordLists.get(wordList.id))){
            preview.append(word.word).append(", ");
            if (preview.length() > 100)
                break;
        }
        return preview.toString();
    }

}