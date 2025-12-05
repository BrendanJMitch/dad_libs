package com.brendan.dadlibs.wordbuilder;

import static com.brendan.dadlibs.wordbuilder.CharUtil.isConsonant;
import static com.brendan.dadlibs.wordbuilder.CharUtil.isVowel;
import static com.brendan.dadlibs.wordbuilder.CharUtil.matchCase;

import com.brendan.dadlibs.engine.Inflection;

public class AdjectiveBuilder implements WordBuilder{

    private final UnimorphDao dao;

    public AdjectiveBuilder(UnimorphDao dao){
        this.dao = dao;
    }
    @Override
    public String getInflectedForm(String adjectiveString, Inflection type) {
        if (adjectiveString.length() < 2)
            return "";
        if (type == Inflection.ABSOLUTE)
            return adjectiveString;
        String[] words = adjectiveString.split(" ");
        boolean isModified = false;
        for (int i = 0; i < words.length; i++){
            if (isAdjective(words[i])){
                words[i] = getSingleAdjectiveInflection(words[i], type);
                isModified = true;
            }
        }
        if (!isModified)
            words[0] = getSingleAdjectiveInflection(words[0], type);
        return String.join(" ", words);
    }

    private boolean isAdjective(String word){
        return dao.getInflectedForms(word, Inflection.ABSOLUTE.getUnimorphModifiers()).length > 0;
    }

    private String getSingleAdjectiveInflection(String adjective, Inflection type){
        if (adjective.length() < 2)
            return "";
        String[] forms = dao.getInflectedForms(adjective, type.getUnimorphModifiers());
        if (forms.length > 0)
            return matchCase(adjective, forms[0]);
        switch (type){
            case COMPARATIVE:
                return buildComparative(adjective);
            case SUPERLATIVE:
                return buildSuperlative(adjective);
            default:
                return adjective;
        }
    }

    private String buildComparative(String adjective){
        int len = adjective.length();
        if (adjective.endsWith("e"))
            return adjective + "r";
        if (isConsonant(adjective.charAt(len-1)) && isVowel(adjective.charAt(len-2)) && !adjective.endsWith("y"))
            return adjective + adjective.charAt(len-1) + "er";
        if (adjective.endsWith("y") && isConsonant(adjective.charAt(len-2)))
            return adjective.substring(0, len-1) + "ier";
        return adjective + "er";
    }

    private String buildSuperlative(String adjective){
        int len = adjective.length();
        if (adjective.endsWith("e"))
            return adjective + "st";
        if (isConsonant(adjective.charAt(len-1)) && isVowel(adjective.charAt(len-2)) && !adjective.endsWith("y"))
            return adjective + adjective.charAt(len-1) + "est";
        if (adjective.endsWith("y") && isConsonant(adjective.charAt(len-2)))
            return adjective.substring(0, len-1) + "iest";
        return adjective + "est";
    }
}
