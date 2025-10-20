package com.brendan.dadlibs.builtins;

import static java.util.Map.entry;

import com.brendan.dadlibs.entity.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class BuiltinWords {

    public static final List<Word> WORDS = new ArrayList<>();
    public static final Map<Long, List<Word>> WORDS_BY_LIST = new TreeMap<>();
    private static final Map<Long, List<String>> wordBank = Map.ofEntries(
            entry(BuiltinWordLists.INTRANSITIVE_VERBS.id, List.of(
                    "run",
                    "fart",
                    "sneeze",
                    "evaporate",
                    "choke",
                    "grow",
                    "meditate",
                    "exercise",
                    "speak",
                    "explode",
                    "germinate",
                    "snooze",
                    "laugh",
                    "exist",
                    "panic"
            )),
            entry(BuiltinWordLists.TRANSITIVE_VERBS.id, List.of(
                    "chew",
                    "dismantle",
                    "beautify",
                    "charm",
                    "enrage",
                    "tease",
                    "sniff",
                    "enchant",
                    "embrace",
                    "believe in",
                    "orbit",
                    "improve",
                    "export",
                    "purify",
                    "borrow",
                    "sit on"
            )),
            entry(BuiltinWordLists.NOUNS.id, List.of(
                    "cow",
                    "robot",
                    "cup",
                    "oat",
                    "black hole",
                    "spork",
                    "gavel",
                    "faucet",
                    "Rubik's cube",
                    "thermometer",
                    "mother",
                    "can of beans",
                    "window",
                    "steamroller",
                    "chinchilla",
                    "steak",
                    "cloud",
                    "barber",
                    "cherub",
                    "battery"
            )),
            entry(BuiltinWordLists.UNCOUNTABLE_NOUNS.id, List.of(
                    "water",
                    "air",
                    "information",
                    "lasagna",
                    "sand",
                    "Bromine",
                    "salt",
                    "cotton",
                    "anger",
                    "evidence"
            )),
            entry(BuiltinWordLists.ADJECTIVES.id, List.of(
                    "angry",
                    "unhinged",
                    "blue",
                    "narcissistic",
                    "irrational",
                    "slimy",
                    "tidy",
                    "beautiful",
                    "portable",
                    "weathered",
                    "ugly",
                    "intoxicating",
                    "hungry",
                    "plague-ridden",
                    "fleshy"
            )),
            entry(BuiltinWordLists.PEOPLE.id, List.of(
                    "Barack Obama",
                    "Zeus",
                    "Kim Jong Un",
                    "Johnny Appleseed",
                    "Michael Phelps",
                    "the Duke of Cheesebury",
                    "Tom Cruise",
                    "Robin Hood",
                    "Sauron",
                    "Batman",
                    "my cat",
                    "Augustus Caesar",
                    "Paul McCartney",
                    "the pope",
                    "Lucifer"
            )),
            entry(BuiltinWordLists.PLACES.id, List.of(
                    "New York",
                    "the bathroom",
                    "your house",
                    "Mullbery Street",
                    "Oz",
                    "the Mariana Trench",
                    "Jupiter",
                    "Wyoming",
                    "Tatooine",
                    "the wilderness"
            ))
    );

    static {
        int wordId = 0;
        for (Long listId : wordBank.keySet()){
            for (String wordString : Objects.requireNonNull(wordBank.get(listId))){
                Word word = new Word(wordId, wordString, listId);
                WORDS.add(word);
                WORDS_BY_LIST.computeIfAbsent(listId, k -> new ArrayList<>());
                WORDS_BY_LIST.get(listId).add(word);
                wordId++;
            }
        }
    }

}
