package com.brendan.dadlibs.ui.editor;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.brendan.dadlibs.dao.TemplateDao;
import com.brendan.dadlibs.dao.WordListDao;
import com.brendan.dadlibs.db.AppDatabase;
import com.brendan.dadlibs.engine.DadLibEngine;
import com.brendan.dadlibs.engine.Replacement;
import com.brendan.dadlibs.entity.Template;
import com.brendan.dadlibs.entity.WordList;

import java.util.ArrayList;
import java.util.List;

public class EditorViewModel extends AndroidViewModel {

    private final TemplateDao templateDao;
    private final WordListDao wordListDao;
    private final DadLibEngine engine;
    private boolean isInitialized = false;
    private List<Replacement> replacements;
    private Template template;

    public interface TemplateLoadedCallback {
        void onLoaded(Template template);
    }

    public EditorViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        templateDao = db.templateDao();
        wordListDao = db.wordListDao();
        engine = new DadLibEngine((w, s) -> null);
    }

    public void loadTemplate(long templateId, TemplateLoadedCallback callback){
        AppDatabase.executor.execute(() -> {
            template = templateDao.getById(templateId);
            new Handler(Looper.getMainLooper()).post(() ->
                    callback.onLoaded(template));
            if (!isInitialized) {
                for (WordList list : wordListDao.getAll()){
                    engine.addWordList(list, new ArrayList<>());
                }
                isInitialized = true;
            }
            replacements = engine.getAllReplacements(template.text);
        });
    }

    public String getTemplateText(){
        return template.text;
    }

    public List<Replacement> getAllReplacements(String template){
        return replacements;
    }
}
