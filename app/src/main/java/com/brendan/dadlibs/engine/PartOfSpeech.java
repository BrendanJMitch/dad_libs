package com.brendan.dadlibs.engine;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum PartOfSpeech {
    NOUN("Noun", "noun",
            Inflection.SINGULAR,
            List.of(
                Inflection.PLURAL,
                Inflection.POSSESSIVE,
                Inflection.PLURAL_POSSESSIVE
    )),
    NON_PLURAL_NOUN("Non-plural Noun", "non_plural_noun",
            Inflection.SINGULAR,
            List.of(
                Inflection.POSSESSIVE
    )),
    VERB("Verb", "verb",
            Inflection.PRESENT,
            List.of(
                Inflection.THIRD_PERSON_PRESENT,
                Inflection.PRESENT_PARTICIPLE,
                Inflection.SIMPLE_PAST,
                Inflection.PAST_PARTICIPLE
    )),
    ADJECTIVE("Adjective", "adjective",
            Inflection.ABSOLUTE,
            List.of(
                Inflection.COMPARATIVE,
                Inflection.SUPERLATIVE
    )),
    OTHER("Other", "other",
            Inflection.OTHER,
            new ArrayList<>());

    public final String displayName;
    public final String label;
    private final Inflection baseInflection;
    private final List<Inflection> inflections;
    private static final Map<String, PartOfSpeech> labelMap = new TreeMap<>();
    private static final Map<String, PartOfSpeech> displayNameMap = new TreeMap<>();

    static {
        for (PartOfSpeech p : EnumSet.allOf(PartOfSpeech.class)){
            labelMap.put(p.label, p);
            displayNameMap.put(p.displayName, p);
        }
    }

    PartOfSpeech(String displayName, String label, Inflection baseInflection, List<Inflection> inflections){
        this.baseInflection = baseInflection;
        this.inflections = inflections;
        this.label = label;
        this.displayName = displayName;
    }

    public static PartOfSpeech getByLabel(String label){
        return labelMap.get(label);
    }

    public static PartOfSpeech getByDisplayName(String displayName){
        return displayNameMap.get(displayName);
    }

    public List<Inflection> getInflections(){
        List<Inflection> tmp = new ArrayList<>(inflections.size() + 1);
        tmp.add(baseInflection);
        tmp.addAll(inflections);
        return Collections.unmodifiableList(tmp);
    }

    public Inflection getBaseInflection(){
        return baseInflection;
    }

    public List<Inflection> getNonBaseInflections(){
        return inflections;
    }

    @NonNull
    @Override
    public String toString(){
        return displayName;
    }
}
