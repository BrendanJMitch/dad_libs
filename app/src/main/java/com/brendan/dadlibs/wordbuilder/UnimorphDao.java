package com.brendan.dadlibs.wordbuilder;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface UnimorphDao {
    @Query("SELECT inflectedform\n" +
            "FROM   unimorphentry e\n" +
            "WHERE  e.lemma = :lemma COLLATE nocase\n" +
            "       AND e.modifiers = :modifiers")
    String[] getInflectedForms(String lemma, String modifiers);
}
