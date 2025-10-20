package com.brendan.dadlibs.wordbuilder;

import com.brendan.dadlibs.engine.Inflection;

public interface WordBuilder {
    String getInflectedForm(String word, Inflection type);
}
