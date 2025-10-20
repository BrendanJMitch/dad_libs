package com.brendan.dadlibs.ui.home;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;

import com.brendan.dadlibs.dao.TemplateDao;
import com.brendan.dadlibs.db.AppDatabase;
import com.brendan.dadlibs.entity.Template;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final TemplateDao templateDao;

    public interface DataLoadedCallback {
        public void onLoaded(List<Template> templates);
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
}