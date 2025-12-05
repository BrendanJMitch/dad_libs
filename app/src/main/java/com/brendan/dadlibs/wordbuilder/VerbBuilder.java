package com.brendan.dadlibs.wordbuilder;

import static com.brendan.dadlibs.wordbuilder.CharUtil.isConsonant;
import static com.brendan.dadlibs.wordbuilder.CharUtil.isVowel;
import static com.brendan.dadlibs.wordbuilder.CharUtil.matchCase;

import com.brendan.dadlibs.engine.Inflection;

public class VerbBuilder implements WordBuilder{

    private final UnimorphDao dao;

    public VerbBuilder(UnimorphDao dao){
        this.dao = dao;
    }

    @Override
    public String getInflectedForm(String verbString, Inflection type){
        if (verbString.length() < 2)
            return "";
        if (type == Inflection.PRESENT)
            return verbString;
        String[] words = verbString.split(" ");
        for (int i = 0; i < words.length; i++){
            if (isVerb(words[i])){
                words[i] = getSingleVerbInflection(words[i], type);
                return String.join(" ", words);
            }
        }
        words[0] = getSingleVerbInflection(words[0], type);
        return String.join(" ", words);
    }

    private boolean isVerb(String word){
        return dao.getInflectedForms(word, Inflection.PRESENT.getUnimorphModifiers()).length > 0;
    }

    private String getSingleVerbInflection(String verb, Inflection type){
        String[] forms = dao.getInflectedForms(verb, type.getUnimorphModifiers());
        if (verb.length() < 2)
            return "";
        if (forms.length > 0)
            return matchCase(verb, forms[0]);
        switch (type){
            case THIRD_PERSON_PRESENT:
                return buildThirdPersonPresent(verb);
            case SIMPLE_PAST:
            case PAST_PARTICIPLE:
                return buildSimplePast(verb);
            case PRESENT_PARTICIPLE:
                return buildPresentParticiple(verb);
            default:
                return verb;
        }
    }

    private String buildSimplePast(String verb){
        int len = verb.length();
        if (verb.endsWith("e"))
            return verb + "d";
        if (isConsonant(verb.charAt(len-1)) && isVowel(verb.charAt(len-2)) && !verb.endsWith("y"))
            return verb + verb.charAt(len-1) + "ed";
        if (verb.endsWith("y") && isConsonant(verb.charAt(len-2)))
            return verb.substring(0, len-1) + "ied";
        return verb + "ed";
    }

    private String buildThirdPersonPresent(String verb){
        int len = verb.length();
        if (verb.endsWith("y") && isConsonant(verb.charAt(len-2)))
            return verb.substring(0, len-1) + "ies";
        return verb + "s";
    }

    private String buildPresentParticiple(String verb){
        int len = verb.length();
        if (verb.endsWith("e"))
            return verb.substring(0, len-1) + "ing";
        if (isConsonant(verb.charAt(len-1)) && isVowel(verb.charAt(len-2)) && !verb.endsWith("y"))
            return verb + verb.charAt(len-1) + "ing";
        return verb + "ing";
    }
}
