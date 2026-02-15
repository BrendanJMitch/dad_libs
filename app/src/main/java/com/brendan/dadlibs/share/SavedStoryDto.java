package com.brendan.dadlibs.share;

public class SavedStoryDto {

    public String name;
    public String text;
    public Long timestamp;
    public Float rating;

    public SavedStoryDto() {}

    public SavedStoryDto(String name, String text, Long timestamp, Float rating) {
        this.name = name;
        this.text = text;
        this.timestamp = timestamp;
        this.rating = rating;
    }
}
