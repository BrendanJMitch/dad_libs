package com.brendan.dadlibs.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.brendan.dadlibs.builtins.PreLoader;
import com.brendan.dadlibs.data.dao.InflectionDao;
import com.brendan.dadlibs.data.dao.SavedStoryDao;
import com.brendan.dadlibs.data.dao.TemplateDao;
import com.brendan.dadlibs.data.dao.WordDao;
import com.brendan.dadlibs.data.dao.WordListDao;
import com.brendan.dadlibs.data.entity.InflectedForm;
import com.brendan.dadlibs.data.entity.SavedStory;
import com.brendan.dadlibs.data.entity.Template;
import com.brendan.dadlibs.data.entity.Word;
import com.brendan.dadlibs.data.entity.WordList;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {Word.class, WordList.class, SavedStory.class, Template.class, InflectedForm.class},
        version = 2,
        exportSchema = true)
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

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `SavedStory_new` (" +
                            "`id` INTEGER PRIMARY KEY," +
                            "`name` TEXT NOT NULL," +
                            "`templateId` INTEGER," +  // ← now nullable
                            "`text` TEXT NOT NULL," +
                            "`timestamp` INTEGER NOT NULL," +
                            "`rating` REAL," +
                            "FOREIGN KEY(`templateId`) REFERENCES `Template`(`id`) ON DELETE CASCADE" +
                            ")");
            db.execSQL(
                    "INSERT INTO `SavedStory_new` (`id`, `name`, `templateId`, `text`, `timestamp`, `rating`) " +
                            "SELECT `id`, `name`, `templateId`, `text`, `timestamp`, `rating` FROM `SavedStory`");
            db.execSQL("DROP TABLE `SavedStory`");
            db.execSQL("ALTER TABLE `SavedStory_new` RENAME TO `SavedStory`");
            db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_SavedStory_templateId` " +
                            "ON `SavedStory` (`templateId`)");
        }
    };

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_FILENAME)
                            .addCallback(new Callback() {
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
                        }).addMigrations(MIGRATION_1_2).build();
                }
            }
        }
        return INSTANCE;
    }

    public int version(){
        return this.getOpenHelper().getReadableDatabase().getVersion();
    }
}
