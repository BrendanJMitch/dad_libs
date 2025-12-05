package com.brendan.dadlibs.wordbuilder;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.brendan.dadlibs.db.AppDatabase;
import com.brendan.dadlibs.entity.UnimorphEntry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database( entities = {UnimorphEntry.class}, version = 1)
public abstract class UnimoprhDatabase extends RoomDatabase{

    public static final ExecutorService executor =
            Executors.newFixedThreadPool(4);
    public abstract UnimorphDao unimorphDao();

    private static volatile UnimoprhDatabase INSTANCE;

    public static UnimoprhDatabase getDatabase(Context context){
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, UnimoprhDatabase.class, "unimorph.db")
                            .createFromAsset("unimorph.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
