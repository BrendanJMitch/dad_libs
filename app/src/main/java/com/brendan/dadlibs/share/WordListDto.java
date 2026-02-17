package com.brendan.dadlibs.share;

import java.util.List;

public class WordListDto {
    public String name;
    public String singularName;
    public String marker;
    public String partOfSpeech;
    public List<WordDto> words;

    public WordListDto() {}

    public WordListDto(String name, String singularName, String marker, String partOfSpeech, List<WordDto> words) {
        this.name = name;
        this.singularName = singularName;
        this.marker = marker;
        this.partOfSpeech = partOfSpeech;
        this.words = words;
    }
}
