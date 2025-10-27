package com.brendan.dadlibs.engine;


import androidx.annotation.NonNull;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public enum PartOfSpeech {
    NOUN("Noun", "noun", List.of(
            Inflection.SINGULAR,
            Inflection.POSSESSIVE,
            Inflection.PLURAL,
            Inflection.PLURAL_POSSESSIVE
    )),
    NON_PLURAL_NOUN("Non-plural Noun", "non_plural_noun", List.of(
            Inflection.SINGULAR,
            Inflection.POSSESSIVE
    )),
    VERB("Verb", "verb", List.of(
            Inflection.PRESENT,
            Inflection.THIRD_PERSON_PRESENT,
            Inflection.PRESENT_PARTICIPLE,
            Inflection.SIMPLE_PAST,
            Inflection.PAST_PARTICIPLE
    )),
    ADJECTIVE("Adjective", "adjective", List.of(
            Inflection.ABSOLUTE,
            Inflection.COMPARATIVE,
            Inflection.SUPERLATIVE
    )),
    OTHER("Other", "other", List.of(
            Inflection.OTHER
    ));

    public final List<Inflection> inflections;
    public final String displayName;
    public final String label;
    private static final Map<String, PartOfSpeech> labelMap = new TreeMap<>();

    static {
        for (PartOfSpeech p : EnumSet.allOf(PartOfSpeech.class)){
            labelMap.put(p.label, p);
        }
    }

    PartOfSpeech(String displayName, String label, List<Inflection> inflections){
        this.inflections = inflections;
        this.label = label;
        this.displayName = displayName;
    }

    public static PartOfSpeech getByLabel(String label){
        return labelMap.get(label);
    }

    @NonNull
    @Override
    public String toString(){
        return displayName;
    }
}
