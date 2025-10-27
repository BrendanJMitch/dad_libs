package com.brendan.dadlibs.wordbuilder;

import static com.brendan.dadlibs.wordbuilder.CharUtil.isConsonant;
import static com.brendan.dadlibs.wordbuilder.CharUtil.matchCase;

import com.brendan.dadlibs.engine.Inflection;

public class NounBuilder implements WordBuilder{

    private final UnimorphDao dao;

    public NounBuilder(UnimorphDao dao){
        this.dao = dao;
    }

    @Override
    public String getInflectedForm(String noun, Inflection type){
        switch (type) {
            case PLURAL:
                return getPlural(noun);
            case POSSESSIVE:
                return buildPossessive(noun);
            case PLURAL_POSSESSIVE:
                return buildPluralPossessive(noun);
            default:
                return noun;
        }
    }

    private String getPlural(String noun){
        String[] words = noun.split(" ");
        for (int i = 0; i < words.length; i++){
            if (isNoun(words[i])){
                words[i] = getSingleWordPlural(words[i]);
                return String.join(" ", words);
            }
        }
        words[0] = getSingleWordPlural(words[0]);
        return String.join(" ", words);
    }

    private boolean isNoun(String word){
        return dao.getInflectedForms(word, Inflection.SINGULAR.getUnimorphModifiers()).length > 0;
    }

    private String getSingleWordPlural(String noun){
        String[] forms = dao.getInflectedForms(noun, Inflection.PLURAL.getUnimorphModifiers());
        if (forms.length > 0)
            return matchCase(noun, forms[0]);
        else
            return buildPlural(noun);
    }

    private String buildPlural(String noun){
        int len = noun.length();
        if (noun.endsWith("s") || noun.endsWith("x") || noun.endsWith("z") ||
                noun.endsWith("ch") || noun.endsWith("sh"))
            return noun + "es";
        if (noun.endsWith("y") && isConsonant(noun.charAt(len - 2)))
            return noun.substring(0, len-1) + "ies";
        return noun + "s";
    }

    private String buildPossessive(String noun){
        return noun + "'s";
    }

    private String buildPluralPossessive(String noun){
        String plural = getPlural(noun);
        if (plural.endsWith("s"))
            return plural + "'";
        return plural + "'s";
    }
}
