package com.brendan.dadlibs.wordbuilder;

import android.content.Context;

import com.brendan.dadlibs.engine.PartOfSpeech;

public class BuilderProvider {

    private final NounBuilder nounBuilder;
    private final VerbBuilder verbBuilder;
    private final AdjectiveBuilder adjectiveBuilder;
    private final AdverbBuilder adverbBuilder;

    public BuilderProvider(Context context){
        UnimorphDao unimorphDao = UnimoprhDatabase.getDatabase(context).unimorphDao();
        nounBuilder = new NounBuilder(unimorphDao);
        verbBuilder = new VerbBuilder(unimorphDao);
        adjectiveBuilder = new AdjectiveBuilder(unimorphDao);
        adverbBuilder = new AdverbBuilder();
    }

    public WordBuilder getWordBuilder(PartOfSpeech partOfSpeech){
        switch (partOfSpeech){
            case NOUN:
            case NON_PLURAL_NOUN:
                return nounBuilder;
            case VERB:
                return verbBuilder;
            case ADJECTIVE:
                return adjectiveBuilder;
            case ADVERB:
                return adverbBuilder;
        }
        return null; // All cases covered, this is never reached
    }
}
