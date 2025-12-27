package com.brendan.dadlibs.engine;

import android.util.Pair;

import com.brendan.dadlibs.entity.SavedStory;
import com.brendan.dadlibs.entity.Template;
import com.brendan.dadlibs.entity.Word;
import com.brendan.dadlibs.entity.WordList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DadLibEngine {

    private final Map<String, List<Word>> randomWords;
    private final Map<String, WordList> wordLists;

    private static final Pattern markerPattern = Pattern.compile(
            "\\$\\{([A-Za-z0-9_\\-\\s]+)\\}"
    );  // Matches markers of the form ${word_list_identifier 0 inflection_type}
    private final Inflector inflector;

    public interface Inflector{
        String getInflection(Word word, String inflectionType);
    }

    public DadLibEngine(Inflector inflector){
        randomWords = new TreeMap<>();
        wordLists = new TreeMap<>();
        this.inflector = inflector;
    }

    public void addWordList(WordList list, List<Word> words){
        randomWords.put(list.marker, new ArrayList<>(words));
        wordLists.put(list.marker, list);
    }

    public SavedStory create(Template template){
        shuffleWords();
        String text = template.text;
        Replacement replacement;
        while (true){
            replacement = getNextReplacement(text);
            if (replacement == null)
                break;
            text = doReplacement(text, replacement);
        }
        return new SavedStory(template.name, template.id, text, null, null);
    }


    public Replacement getNextReplacement(String template){
        Matcher matcher = markerPattern.matcher(template);

        if (matcher.find()) {
            Placeholder placeholder = getPlaceholder(matcher.group(1));
            return new Replacement(matcher.start(), matcher.end() - matcher.start(), placeholder);
        } else {
            return null;
        }
    }

    public List<Replacement> getAllReplacements(String template) {
        List<Replacement> replacements = new ArrayList<>();
        Matcher matcher = markerPattern.matcher(template);

        while (matcher.find()) {
            Placeholder placeholder = getPlaceholder(matcher.group(1));
            replacements.add(new Replacement(
                            matcher.start(), matcher.end() - matcher.start(), placeholder));
        }
        return replacements;
    }

    public String doReplacement(String text, Replacement replacement){
        String replacementStr;
        try {
            Placeholder placeholder = Objects.requireNonNull(replacement.placeholder);
            Word word = randomWords.get(placeholder.wordList.marker)
                    .get(placeholder.index);
            replacementStr = Objects.requireNonNull(
                    inflector.getInflection(word, placeholder.inflection.getLabel()));
        } catch (NullPointerException e){
            replacementStr = "(unknown word)";
        }
        return text.substring(0, replacement.startPos) +
                replacementStr +
                text.substring(replacement.startPos + replacement.length);
    }

    public static List<Pair<Integer, Integer>> getAllReplacementIndices(String template){
        List<Pair<Integer, Integer>> replacements = new ArrayList<>();
        Matcher matcher = markerPattern.matcher(template);

        while (matcher.find()) {
            replacements.add(new Pair<>(
                    matcher.start(), matcher.end() - matcher.start()));
        }
        return replacements;
    }

    public int getNextIndex(Template template, WordList wordList) {
        int highestIndex = 0;
        for (Replacement replacement : getAllReplacements(template.text)){
            if (replacement.placeholder.wordList.id.equals(wordList.id)){
                if (replacement.placeholder.index > highestIndex){
                    highestIndex = replacement.placeholder.index;
                }
            }
        }
        return highestIndex + 1;
    }

    public String getMarker(Placeholder placeholder) {
        return String.format(Locale.US, "${%s %d %s}",
                placeholder.wordList.marker,
                placeholder.index,
                placeholder.inflection.getLabel());
    }

    private Placeholder getPlaceholder(String placeholderString){
        String[] tokens = placeholderString.split(" ");
        try {
            WordList list = wordLists.get(tokens[0]);
            Inflection inflection = Inflection.getByLabel(tokens[2]);
            int index = Integer.parseInt(tokens[1]);
            return new Placeholder(list, inflection, index);
        } catch (Exception e) {
            return null;
        }
    }

    private void shuffleWords(){
        for (List<Word> randomWordList : randomWords.values()){
            Collections.shuffle(randomWordList);
        }
    }
}
