package com.brendan.dadlibs.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.brendan.dadlibs.data.entity.WordList;
import com.brendan.dadlibs.data.relation.WordListWithWords;

import java.util.List;

@Dao
public interface WordListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(WordList wordList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<WordList> wordLists);

    @Update
    void update(WordList wordList);

    @Delete
    void delete(WordList wordList);

    @Query("Select * FROM WordList")
    List<WordList> getAll();

    @Query("Select * FROM WordList w WHERE w.id = :id")
    WordList getById(long id);

    @Transaction
    @Query("SELECT * FROM WordList WHERE id = :id")
    WordListWithWords getWordListWithWords(long id);


}
