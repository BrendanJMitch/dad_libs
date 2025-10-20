package com.brendan.dadlibs.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.brendan.dadlibs.engine.Inflection;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

@Entity(indices = {@Index(value = "partOfSpeech")})
public class WordList {
    @PrimaryKey
    public Long id;
    @NonNull
    public String name;
    @NonNull
    public String marker;
    @NonNull
    public Boolean isBuiltin;
    public String partOfSpeech;

    public WordList(@NonNull Long id, @NonNull String name, @NonNull String marker,
                    @NonNull Boolean isBuiltin, String partOfSpeech){
        this.id = id;
        this.name = name;
        this.marker = marker;
        this.partOfSpeech = partOfSpeech;
        this.isBuiltin = isBuiltin;
    }

    @Ignore
    public WordList(@NonNull String name, @NonNull Boolean isBuiltin, String partOfSpeech){
        this.name = name;
        this.partOfSpeech = partOfSpeech;
        this.isBuiltin = isBuiltin;
        this.marker = generateMarker(name);
    }

    public String getMarkerString(int index, Inflection inflection){
        return String.format(Locale.US, "${%s %d %s}", marker, index, inflection.getLabel());
    }

    private static String generateMarker(String listName) {
        String formattedName = Normalizer.normalize(listName, Normalizer.Form.NFD)
                .strip()
                .toLowerCase()
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^\\p{L}\\p{N}\\p{IsWhite_Space}_]", "")
                .replaceAll("\\p{IsWhite_Space}+", "_");


        return String.format("%s_%s", formattedName, UUID.randomUUID());
    }
}
