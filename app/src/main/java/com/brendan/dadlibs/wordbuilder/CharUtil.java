package com.brendan.dadlibs.wordbuilder;

import java.util.Set;

public class CharUtil {
    private static final Set<Character> VOWELS = Set.of(
            'a','e','i','o','u',
            'A','E','I','O','U'
    );

    private static final Set<Character> CONSONANTS = Set.of(
            'b','c','d','f','g','h','j','k','l','m','n','p','q','r','s','t','v','w','x','y','z',
            'B','C','D','F','G','H','J','K','L','M','N','P','Q','R','S','T','V','W','X','Y', 'Z'
    );

    public static boolean isVowel(char letter){
        return VOWELS.contains(letter);
    }

    public static boolean isConsonant(char letter){
        return CONSONANTS.contains(letter);
    }

    public static String matchCase(String original, String inflected){
        if (Character.isUpperCase(original.charAt(0)))
            return inflected.substring(0, 1).toUpperCase() + inflected.substring(1);
        else
            return inflected.substring(0, 1).toLowerCase() + inflected.substring(1);
    }
}
