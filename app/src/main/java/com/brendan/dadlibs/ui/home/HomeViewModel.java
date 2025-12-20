package com.brendan.dadlibs.ui.home;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Pair;

import androidx.lifecycle.AndroidViewModel;

import com.brendan.dadlibs.dao.TemplateDao;
import com.brendan.dadlibs.db.AppDatabase;
import com.brendan.dadlibs.engine.DadLibEngine;
import com.brendan.dadlibs.engine.Replacement;
import com.brendan.dadlibs.entity.Template;
import com.brendan.dadlibs.ui.editor.PlaceholderSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class HomeViewModel extends AndroidViewModel {

    private final TemplateDao templateDao;

    public interface DataLoadedCallback {
        void onLoaded(List<Template> templates);
    }

    public HomeViewModel(Application application) {
        super(application);
        templateDao = AppDatabase.getDatabase(application).templateDao();
    }

    public void updateTemplates(DataLoadedCallback callback){
        AppDatabase.executor.execute(() -> {
            List<Template> templates = templateDao.getAll();
            new Handler(Looper.getMainLooper()).post(() -> callback.onLoaded(templates));
        });
    }

    public void copyTemplate(Template original, String newName, DataLoadedCallback callback){
        Template copy = new Template(newName, original.text);
        AppDatabase.executor.execute(() -> {
            copy.id = templateDao.insert(copy);
            new Handler(Looper.getMainLooper()).post(() -> callback.onLoaded(List.of(copy)));
        });
    }

    public void deleteTemplate(Template template){
        AppDatabase.executor.execute(() -> templateDao.delete(template));
    }

    public List<Pair<Integer, Integer>> getBlankIndices(String template){
        return DadLibEngine.getAllReplacementIndices(template);
    }
}