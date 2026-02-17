package com.brendan.dadlibs.engine;

import com.brendan.dadlibs.data.entity.WordList;

public class Placeholder {
    public final WordList wordList;
    public final Inflection inflection;
    public final int index;

    public Placeholder(WordList wordList, Inflection inflection, int index){
        this.wordList = wordList;
        this.inflection = inflection;
        this.index = index;
    }

}
