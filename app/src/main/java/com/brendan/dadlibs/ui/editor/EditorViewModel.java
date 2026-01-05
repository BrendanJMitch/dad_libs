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
import com.brendan.dadlibs.engine.PartOfSpeech;
import com.brendan.dadlibs.engine.Placeholder;
import com.brendan.dadlibs.engine.Replacement;
import com.brendan.dadlibs.entity.Template;
import com.brendan.dadlibs.entity.WordList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EditorViewModel extends AndroidViewModel {

    private final TemplateDao templateDao;
    private final WordListDao wordListDao;
    private final DadLibEngine engine;
    private boolean isInitialized = false;
    private List<Replacement> replacements;
    private Template template;
    private boolean hasUnsavedChanges = false;

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
            if (templateId >= 0){
                template = templateDao.getById(templateId);
            } else {
                template = new Template("", "");
            }
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

    public void loadWordLists(@NonNull Consumer<List<WordList>> callback) {
        AppDatabase.executor.execute(() -> {
            List<WordList> options = wordListDao.getAll();
            new Handler(Looper.getMainLooper()).post(() -> callback.accept(options));
        });
    }

    public String getTemplateText(){
        return template.text;
    }

    public void setTemplateText(String text) {
        if (!text.equals(template.text)) {
            template.text = text;
            hasUnsavedChanges = true;
        }
    }

    public void setTemplateName(String name) {
        if (!name.equals(template.name)) {
            template.name = name;
            hasUnsavedChanges = true;
        }
    }

    public boolean hasUnsavedChanges(){
        return hasUnsavedChanges;
    }

    public List<Replacement> getAllReplacements(String template){
        // TODO: Race condition if replacements isn't loaded
        return replacements;
    }

    public Placeholder getDefaultPlaceholder(WordList wordList){
        PartOfSpeech partOfSpeech = PartOfSpeech.getByLabel(wordList.partOfSpeech);
        int index = engine.getNextIndex(template, wordList);
        return new Placeholder(wordList, partOfSpeech.getBaseInflection(), index);
    }

    public String getUnderlyingString(Placeholder placeholder) {
        return engine.getMarker(placeholder);
    }

    public void saveTemplate(){
        hasUnsavedChanges = false;
        AppDatabase.executor.execute(() -> templateDao.insert(template));
    }
}
