package com.brendan.dadlibs.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.brendan.dadlibs.builtins.PreLoader;
import com.brendan.dadlibs.dao.InflectionDao;
import com.brendan.dadlibs.dao.SavedStoryDao;
import com.brendan.dadlibs.dao.TemplateDao;
import com.brendan.dadlibs.dao.WordDao;
import com.brendan.dadlibs.dao.WordListDao;
import com.brendan.dadlibs.entity.InflectedForm;
import com.brendan.dadlibs.entity.SavedStory;
import com.brendan.dadlibs.entity.Template;
import com.brendan.dadlibs.entity.Word;
import com.brendan.dadlibs.entity.WordList;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Word.class, WordList.class, SavedStory.class, Template.class, InflectedForm.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static final ExecutorService executor =
            Executors.newFixedThreadPool(4);
    public static final String DB_FILENAME = "app_database.db";

    public abstract WordDao wordDao();
    public abstract WordListDao wordListDao();
    public abstract TemplateDao templateDao();
    public abstract SavedStoryDao savedStoryDao();
    public abstract InflectionDao inflectionDao();
    private static volatile AppDatabase INSTANCE;


    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class, DB_FILENAME
                    ).addCallback(new Callback() {
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                super.onCreate(db);

                                executor.execute(() -> {
                                    List<Template> templates = PreLoader.loadTemplates(context);
                                    INSTANCE.templateDao().insert(templates);

                                    List<WordList> lists = PreLoader.loadWordLists(context);
                                    INSTANCE.wordListDao().insert(lists);

                                    List<Word> words = PreLoader.loadWords(context);
                                    INSTANCE.wordDao().insert(words);

                                    List<InflectedForm> inflectedForms = PreLoader.loadInflectedForms(context);
                                    INSTANCE.inflectionDao().insert(inflectedForms);

                                    context.getSharedPreferences("app", Context.MODE_PRIVATE)
                                            .edit()
                                            .putBoolean("initialized", true)
                                            .apply();
                                });
                            }
                        }).build();
                }
            }
        }
        return INSTANCE;
    }

    public int version(){
        return this.getOpenHelper().getReadableDatabase().getVersion();
    }
}
