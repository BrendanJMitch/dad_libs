package com.brendan.dadlibs.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UnimorphEntry {
    @PrimaryKey
    public Long id;
    @NonNull
    public String lemma;
    @NonNull
    public String inflectedForm;
    @NonNull
    public String modifiers;

    public UnimorphEntry(@NonNull Long id, @NonNull String lemma, @NonNull String inflectedForm,
                         @NonNull String modifiers){
        this.id = id;
        this.lemma = lemma;
        this.inflectedForm = inflectedForm;
        this.modifiers = modifiers;
    }
}
