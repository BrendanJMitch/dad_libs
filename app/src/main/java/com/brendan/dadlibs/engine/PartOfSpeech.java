package com.brendan.dadlibs.engine;


import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public enum PartOfSpeech {
    NOUN("noun", List.of(
            Inflection.SINGULAR,
            Inflection.POSSESSIVE,
            Inflection.PLURAL,
            Inflection.PLURAL_POSSESSIVE
    )),
    NON_PLURAL_NOUN("non_plural_noun", List.of(
            Inflection.SINGULAR,
            Inflection.POSSESSIVE
    )),
    VERB("verb", List.of(
            Inflection.PRESENT,
            Inflection.THIRD_PERSON_PRESENT,
            Inflection.PRESENT_PARTICIPLE,
            Inflection.SIMPLE_PAST,
            Inflection.PAST_PARTICIPLE
    )),
    ADJECTIVE("adjective", List.of(
            Inflection.ABSOLUTE,
            Inflection.COMPARATIVE,
            Inflection.SUPERLATIVE
    )),
    ADVERB("adverb", List.of(
            Inflection.ADVERB
    ));

    public final List<Inflection> inflections;
    public final String label;
    private static final Map<String, PartOfSpeech> labelMap = new TreeMap<>();

    static {
        for (PartOfSpeech p : EnumSet.allOf(PartOfSpeech.class)){
            labelMap.put(p.label, p);
        }
    }

    PartOfSpeech(String label, List<Inflection> inflections){
        this.inflections = inflections;
        this.label = label;
    }

    public static PartOfSpeech getByLabel(String label){
        return labelMap.get(label);
    }
}
