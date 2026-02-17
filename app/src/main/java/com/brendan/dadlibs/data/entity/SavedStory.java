package com.brendan.dadlibs.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

@Entity(foreignKeys = @ForeignKey(
        entity = Template.class,
        parentColumns = "id",
        childColumns = "templateId",
        onDelete = ForeignKey.CASCADE
), indices = {@Index("templateId")}
)
public class SavedStory {
    @PrimaryKey
    public Long id;
    @NonNull
    public String name;
    public Long templateId;
    @NonNull
    public String text;
    @NonNull
    public Long timestamp;
    public Float rating;

    public SavedStory(@NonNull String name, Long templateId, @NonNull String text, Long timestamp, Float rating){
        this.name = name;
        this.templateId = templateId;
        this.text = text;
        this.timestamp = Objects.requireNonNullElseGet(timestamp, System::currentTimeMillis);
        this.rating = rating;
    }

    public String getFormattedTimestamp(){
        Date date = new Date(timestamp);
        DateFormat formatter = DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM,
                DateFormat.SHORT,
                Locale.getDefault()
        );
        return formatter.format(date);
    }
}
