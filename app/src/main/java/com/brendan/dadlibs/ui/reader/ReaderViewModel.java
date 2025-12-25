package com.brendan.dadlibs.ui.reader;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;

import com.brendan.dadlibs.dao.SavedStoryDao;
import com.brendan.dadlibs.dao.TemplateDao;
import com.brendan.dadlibs.dao.WordDao;
import com.brendan.dadlibs.dao.WordListDao;
import com.brendan.dadlibs.db.AppDatabase;
import com.brendan.dadlibs.engine.DadLibEngine;
import com.brendan.dadlibs.entity.SavedStory;
import com.brendan.dadlibs.entity.Template;
import com.brendan.dadlibs.entity.WordList;

public class ReaderViewModel extends AndroidViewModel {

    private final TemplateDao templateDao;
    private final WordListDao wordListDao;
    private final WordDao wordDao;
    private final SavedStoryDao savedStoryDao;
    private final DadLibEngine engine;
    private SavedStory story;
    private boolean isInitialized = false;

    public interface TemplateLoadedCallback {
        void onLoaded(SavedStory story);
    }

    public ReaderViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        templateDao = db.templateDao();
        wordListDao = db.wordListDao();
        wordDao = db.wordDao();
        savedStoryDao = db.savedStoryDao();
        this.engine = new DadLibEngine((word, inflectionType) ->
                db.inflectionDao().getInflection(word.id, inflectionType));
    }

    public void updateTemplate(long templateId, TemplateLoadedCallback callback){
        AppDatabase.executor.execute(() -> {
            if (!isInitialized) {
                for (WordList list : wordListDao.getAll()){
                    engine.addWordList(list, wordDao.getAllFromList(list.id));
                }
                isInitialized = true;
            }
            Template template = templateDao.getById(templateId);
            story = engine.create(template);
            new Handler(Looper.getMainLooper()).post(() ->
                    callback.onLoaded(story));
        });
    }

    public SavedStory getStory(){
        return story;
    }

    public void saveStory(SavedStory newStory){
        AppDatabase.executor.execute(() -> savedStoryDao.insert(newStory));
    }

}
