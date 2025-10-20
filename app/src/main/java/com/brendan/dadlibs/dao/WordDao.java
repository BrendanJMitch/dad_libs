package com.brendan.dadlibs.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.brendan.dadlibs.entity.Word;
import com.brendan.dadlibs.entity.WordList;

import java.util.List;

@Dao
public interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Word word);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Word> word);
    @Delete
    void delete(Word word);

    @Query("SELECT * FROM Word w WHERE w.wordListId = :wordListId")
    List<Word> getAllFromList(Long wordListId);
}
