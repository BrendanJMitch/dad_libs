package com.brendan.dadlibs.dao;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.brendan.dadlibs.entity.InflectedForm;
import com.brendan.dadlibs.entity.Word;

import java.util.List;

public class WordWithInflections {
    @Embedded
    public Word word;

    @Relation(
            parentColumn = "id",
            entityColumn = "wordId"
    )
    public List<InflectedForm> inflectedForms;
}