package com.brendan.dadlibs.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.brendan.dadlibs.data.entity.Word;

import java.util.List;

@Dao
public interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Word word);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<Word> word);
    @Delete
    void delete(Word word);

    @Query("SELECT * FROM Word w WHERE w.wordListId = :wordListId")
    List<Word> getAllFromList(Long wordListId);
}
