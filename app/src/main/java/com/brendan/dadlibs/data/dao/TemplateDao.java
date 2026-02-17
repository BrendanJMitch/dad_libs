package com.brendan.dadlibs.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.brendan.dadlibs.data.entity.Template;

import java.util.List;

@Dao
public interface TemplateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Template template);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Template> template);
    @Delete
    void delete(Template template);

    @Query("SELECT * FROM Template")
    List<Template> getAll();

    @Query("SELECT * FROM Template t WHERE t.id = :id")
    Template getById(long id);
}
