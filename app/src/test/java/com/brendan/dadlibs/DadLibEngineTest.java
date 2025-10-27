package com.brendan.dadlibs;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import com.brendan.dadlibs.engine.DadLibEngine;
import com.brendan.dadlibs.entity.SavedStory;
import com.brendan.dadlibs.entity.Template;
import com.brendan.dadlibs.entity.Word;
import com.brendan.dadlibs.entity.WordList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class DadLibEngineTest {

    @Rule
    public RepeatRule repeatRule = new RepeatRule();
    private DadLibEngine engine;
    private static final String NOUN_MARKER = "nouns", VERB_MARKER = "verbs", TITLE = "My Life";
    private static final String
            BASE = "base",
            PLURAL = "plural",
            PAST = "past";
    private static final Word
            NOUN_0 = new Word(0L, "computer", 0L),
            NOUN_1 = new Word(1L, "sponge", 0L),
            VERB_0 = new Word(2L, "sneeze", 0L);

    private static final String
            NOUN_0_BASE = NOUN_0.word,
            NOUN_0_PLURAL = "computers",
            NOUN_1_BASE = NOUN_1.word,
            NOUN_1_PLURAL = "sponges",
            VERB_0_BASE = VERB_0.word,
            VERB_0_PAST = "sneezed";
    private static final Map<String, String> INFLECTIONS = Map.of(
            NOUN_0.id + BASE, NOUN_0_BASE,
            NOUN_0.id + PLURAL, NOUN_0_PLURAL,
            NOUN_1.id + BASE, NOUN_1_BASE,
            NOUN_1.id + PLURAL, NOUN_1_PLURAL,
            VERB_0.id + BASE, VERB_0_BASE,
            VERB_0.id + PAST, VERB_0_PAST
    );


    @Before
    public void setUp() {
        engine = new DadLibEngine((word, type) -> INFLECTIONS.get(word.id + type));
    }

    @Test
    public void testCreateWithOneList() {
        String beginning = "I was walking down the street and a ";
        String end = " fell on my head.";

        engine.addWordList(
                new WordList(0L, "Nouns", NOUN_MARKER, false, ""),
                List.of(
                        NOUN_0
                ));

        Template template = new Template(
                TITLE,
                beginning + "${" + NOUN_MARKER + " 0 " + BASE + "}" + end
        );

        SavedStory story = engine.create(template);
        assertEquals(beginning + NOUN_0 + end, story.text);
    }

    @Test
    public void testCreateWithTwoLists() {
        String beginning = "I always ";
        String middle = " when I notice a ";
        String end = " in my house.";

        engine.addWordList(
                new WordList(0L, "Nouns", NOUN_MARKER, false, ""),
                List.of(
                        NOUN_1
                ));
        engine.addWordList(
                new WordList(0L, "Verbs", VERB_MARKER, false, ""),
                List.of(
                        VERB_0
                ));

        Template template = new Template(
                TITLE,
                beginning + "${" + VERB_MARKER + " 0 " + BASE + "}" + middle + "${" + NOUN_MARKER + " 0 " + BASE + "}" + end
        );

        SavedStory story = engine.create(template);
        assertEquals(beginning + VERB_0 + middle + NOUN_1 + end, story.text);
    }

    @Test
    @RepeatRule.Repeat(100)
    public void testCreateWithTwoWordsFromSameList() {
        String beginning = "I was surprised to find a ";
        String middle = " in my ";
        String end = " when I woke up this morning.";

        engine.addWordList(
                new WordList(0L, "Nouns", NOUN_MARKER, false, ""),
                List.of(
                        NOUN_0,
                        NOUN_1
                ));

        Template template = new Template(
                TITLE,
                beginning + "${" + NOUN_MARKER + " 0 " + BASE + "}" + middle + "${" + NOUN_MARKER + " 1 " + BASE + "}" + end
        );

        SavedStory story = engine.create(template);
        assertThat(story.text, anyOf(
                is(beginning + NOUN_0 + middle + NOUN_1 + end),
                is(beginning + NOUN_1 + middle + NOUN_0 + end)));
    }


    @Test
    public void testInflection() {
        String beginning = "My dog ";
        String middle = " because his ";
        String end = " were too small.";

        engine.addWordList(
                new WordList(0L, "Nouns", NOUN_MARKER, false, ""),
                List.of(
                        NOUN_1
                ));
        engine.addWordList(
                new WordList(0L, "Verbs", VERB_MARKER, false, ""),
                List.of(
                        VERB_0
                ));

        Template template = new Template(
                TITLE,
                beginning + "${" + VERB_MARKER + " 0 " + PAST + "}" + middle + "${" + NOUN_MARKER + " 0 " + PLURAL + "}" + end
        );

        SavedStory story = engine.create(template);
        assertEquals(beginning + INFLECTIONS.get(VERB_0.id + PAST) + middle + INFLECTIONS.get(NOUN_1.id + PLURAL) + end, story.text);
    }

    @Test
    @RepeatRule.Repeat(100)
    public void testCreateIndexConsistency() {
        String beginning = "John loves his ";
        String middle = ", but his ";
        String end = " does not love him.";

        engine.addWordList(
                new WordList(0L, "Nouns", NOUN_MARKER, false, ""),
                List.of(
                        NOUN_0,
                        NOUN_1
                ));

        Template template = new Template(
                TITLE,
                beginning + "${" + NOUN_MARKER + " 0 " + BASE + "}" + middle + "${" + NOUN_MARKER + " 0 " + BASE + "}" + end
        );

        SavedStory story = engine.create(template);
        assertThat(story.text, anyOf(
                is(beginning + NOUN_0 + middle + NOUN_0 + end),
                is(beginning + NOUN_1 + middle + NOUN_1 + end)));
    }

    @Test
    public void testCreateIsRandom() {
        int numWords = 5;
        int numIterations = 100;

        //We don't need inflection here, so replace the inflector with a trivial one
        engine = new DadLibEngine((word, inflectionType) -> word.word);

        List<Word> words = new ArrayList<>(numWords);
        LongStream.range(0, numWords).forEach(i -> words.add(
                new Word(i, UUID.randomUUID().toString(), 0L))); // UUIDs are just a convenient way to get as many unique strings as we need

        engine.addWordList(
                new WordList(0L, "Nouns", NOUN_MARKER, false, ""), words);

        Template template = new Template(
                TITLE,
                "${" + NOUN_MARKER + " 0 " + BASE + "}"
        );

        Set<String> stories = new TreeSet<>();
        IntStream.range(0, numIterations).forEach(i -> stories.add(engine.create(template).text));

        assertEquals("Expected all input words to be represented in the output", numWords, stories.size());
    }
}
