package com.brendan.dadlibs.builtins;

import android.content.Context;

import com.brendan.dadlibs.data.entity.InflectedForm;
import com.brendan.dadlibs.data.entity.Template;
import com.brendan.dadlibs.data.entity.Word;
import com.brendan.dadlibs.data.entity.WordList;
import com.brendan.dadlibs.engine.Inflection;
import com.brendan.dadlibs.engine.PartOfSpeech;
import com.brendan.dadlibs.wordbuilder.BuilderProvider;
import com.brendan.dadlibs.wordbuilder.WordBuilder;

import java.util.ArrayList;
import java.util.List;


public class PreLoader {

    public static List<WordList> loadWordLists(Context context) {
        return BuiltinWordLists.WORD_LISTS;
    }

    public static List<Word> loadWords(Context context) {
        return BuiltinWords.WORDS;
    }

    public static List<Template> loadTemplates(Context context){
        return BuiltinTemplates.TEMPLATES;
    }

    public static List<InflectedForm> loadInflectedForms (Context context){

        List<InflectedForm> forms = new ArrayList<>();
        BuilderProvider builderProvider = new BuilderProvider(context);

        for (WordList list : BuiltinWordLists.WORD_LISTS){
            List<Word> words = BuiltinWords.WORDS_BY_LIST.get(list.id);
            if (words == null)
                continue;

            PartOfSpeech partOfSpeech = PartOfSpeech.getByLabel(list.partOfSpeech);
            WordBuilder builder = builderProvider.getWordBuilder(partOfSpeech);

            for (Word word : words){
                for (Inflection inflection : partOfSpeech.getInflections()) {
                    forms.add(new InflectedForm(
                            word.id,
                            inflection.getLabel(),
                            builder.getInflectedForm(word.word, inflection)
                    ));
                }
            }
        }
        return forms;
    }
}
