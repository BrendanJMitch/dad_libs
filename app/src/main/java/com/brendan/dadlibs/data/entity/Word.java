package com.brendan.dadlibs.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = WordList.class,
        parentColumns = "id",
        childColumns = "wordListId",
        onDelete = ForeignKey.CASCADE
    ), indices = {
        @Index(value = "wordListId"),
        @Index(value = "word")}
)
public class Word {
    @PrimaryKey
    public Long id;
    @NonNull
    public String word;
    public Long wordListId;

    public Word(long id, @NonNull String word, Long wordListId){
        this.id = id;
        this.word = word;
        this.wordListId = wordListId;
    }

    @Ignore
    public Word(@NonNull String word, Long wordListId){
        this.word = word;
        this.wordListId = wordListId;
    }

    @NonNull
    @Override
    public String toString(){
        return word;
    }
}
