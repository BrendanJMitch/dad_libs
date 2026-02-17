package com.brendan.dadlibs.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(primaryKeys = {"wordId", "type"},
        foreignKeys = @ForeignKey(
        entity = Word.class,
        parentColumns = "id",
        childColumns = "wordId",
        onDelete = ForeignKey.CASCADE
))
public class InflectedForm {
    @NonNull
    public Long wordId;
    @NonNull
    public String type;
    @NonNull
    public String inflectedForm;

    public InflectedForm(@NonNull Long wordId, @NonNull String type, @NonNull String inflectedForm){
        this.wordId = wordId;
        this.type = type;
        this.inflectedForm = inflectedForm;
    }

}
