package com.brendan.dadlibs.ui.editor;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.brendan.dadlibs.dao.TemplateDao;
import com.brendan.dadlibs.db.AppDatabase;
import com.brendan.dadlibs.entity.Template;

public class EditorViewModel extends AndroidViewModel {

    private final TemplateDao templateDao;

    public interface TemplateLoadedCallback {
        void onLoaded(Template template);
    }

    public EditorViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        templateDao = db.templateDao();
    }

    public void loadTemplate(long templateId, TemplateLoadedCallback callback){
        AppDatabase.executor.execute(() -> {
            Template template = templateDao.getById(templateId);
            new Handler(Looper.getMainLooper()).post(() ->
                    callback.onLoaded(template));
        });
    }
}
