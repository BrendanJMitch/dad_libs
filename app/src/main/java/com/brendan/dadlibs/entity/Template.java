package com.brendan.dadlibs.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Template {
    @PrimaryKey
    public Long id;
    @NonNull
    public String name;
    @NonNull
    public String text;

    public Template(@NonNull String name, @NonNull String text){
        this.name = name;
        this.text = text;
    }
}
