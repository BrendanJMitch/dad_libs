package com.brendan.dadlibs.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.brendan.dadlibs.entity.SavedStory;

import java.util.List;

@Dao
public interface SavedStoryDao {
    @Insert
    void insert(SavedStory story);
    @Update
    void update(SavedStory story);
    @Delete
    void delete(SavedStory story);

    @Query("SELECT * FROM SavedStory")
    List<SavedStory> getAll();

    @Query("SELECT * FROM SavedStory s WHERE s.id = :id")
    SavedStory get(long id);
}
