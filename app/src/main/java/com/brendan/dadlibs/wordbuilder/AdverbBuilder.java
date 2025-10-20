package com.brendan.dadlibs.wordbuilder;

import com.brendan.dadlibs.engine.Inflection;

public class AdverbBuilder implements WordBuilder{

    public AdverbBuilder(){}
    @Override
    public String getInflectedForm(String adverbString, Inflection type) {
        return adverbString;
    }
}
