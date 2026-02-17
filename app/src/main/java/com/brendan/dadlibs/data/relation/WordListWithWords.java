package com.brendan.dadlibs.data.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.brendan.dadlibs.data.entity.Word;
import com.brendan.dadlibs.data.entity.WordList;

import java.util.List;

public class WordListWithWords {

    @Embedded
    public WordList wordList;

    @Relation(
            entity = Word.class,
            parentColumn = "id",
            entityColumn = "wordListId"
    )
    public List<WordWithInflectedForms> words;
}
