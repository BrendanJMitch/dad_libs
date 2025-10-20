package com.brendan.dadlibs.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.brendan.dadlibs.entity.InflectedForm;

import java.util.List;

@Dao
public interface InflectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(InflectedForm word);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<InflectedForm> word);

    @Delete
    void delete(InflectedForm word);

    @Query("SELECT inflectedForm FROM InflectedForm i WHERE i.wordId = :wordId AND i.type = :type")
    String getInflection(Long wordId, String type);

}
