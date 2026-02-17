package com.brendan.dadlibs.data.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.brendan.dadlibs.data.entity.InflectedForm;
import com.brendan.dadlibs.data.entity.Word;

import java.util.List;

public class WordWithInflectedForms {

    @Embedded
    public Word word;

    @Relation(
            parentColumn = "id",
            entityColumn = "wordId"
    )
    public List<InflectedForm> inflectedForms;
}