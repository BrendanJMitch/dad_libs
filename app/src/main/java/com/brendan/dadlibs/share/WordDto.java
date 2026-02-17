package com.brendan.dadlibs.share;

import java.util.List;

public class WordDto {

    public String word;
    public List<InflectedFormDto> inflectedForms;

    public WordDto() {}

    public WordDto(String word, List<InflectedFormDto> inflectedForms) {
        this.word = word;
        this.inflectedForms = inflectedForms;
    }
}
