package com.brendan.dadlibs.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.brendan.dadlibs.entity.Template;

import java.util.List;

@Dao
public interface TemplateDao {
    @Insert
    void insert(Template template);
    @Insert
    void insert(List<Template> template);
    @Update
    void update(Template template);
    @Delete
    void delete(Template template);

    @Query("SELECT * FROM Template")
    List<Template> getAll();

    @Query("SELECT * FROM Template t WHERE t.id = :id")
    Template getById(long id);
}
