package com.brendan.dadlibs.builtins;

import com.brendan.dadlibs.engine.PartOfSpeech;
import com.brendan.dadlibs.entity.WordList;

import java.util.List;

public class BuiltinWordLists {

    public static final WordList INTRANSITIVE_VERBS = new WordList(
            0L,
            "Intransitive Verbs",
            "intransitive_verb",
            true,
            PartOfSpeech.VERB.label
    );

    public static final WordList TRANSITIVE_VERBS = new WordList(
            1L,
            "Transitive Verbs",
            "transitive_verb",
            true,
            PartOfSpeech.VERB.label
    );

    public static final WordList NOUNS = new WordList(
            2L,
            "Nouns",
            "noun",
            true,
            PartOfSpeech.NOUN.label
    );

    public static final WordList UNCOUNTABLE_NOUNS = new WordList(
            3L,
            "Uncountable Nouns",
            "uncountable_noun",
            true,
            PartOfSpeech.NON_PLURAL_NOUN.label
    );

    public static final WordList ADJECTIVES = new WordList(
            4L,
            "Adjectives",
            "adjective",
            true,
            PartOfSpeech.ADJECTIVE.label
    );

    public static final WordList PEOPLE = new WordList(
            5L,
            "People",
            "person",
            true,
            PartOfSpeech.NON_PLURAL_NOUN.label
    );

    public static final WordList PLACES = new WordList(
            6L,
            "Places",
            "place",
            true,
            PartOfSpeech.NON_PLURAL_NOUN.label
    );

    public static final List<WordList> WORD_LISTS = List.of(
            TRANSITIVE_VERBS,
            INTRANSITIVE_VERBS,
            NOUNS,
            UNCOUNTABLE_NOUNS,
            PLACES,
            PEOPLE,
            ADJECTIVES
    );
}
