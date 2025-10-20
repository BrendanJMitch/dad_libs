package com.brendan.dadlibs;

import com.brendan.dadlibs.wordbuilder.UnimorphDao;

import java.util.HashMap;
import java.util.Map;

public class MockUnimorphDao implements UnimorphDao {
    private final Map<String, String[]> forms = new HashMap<>();

    public void addForms(String word, String unimorphModifiers, String... inflected) {
        forms.put(word.toLowerCase() + "|" + unimorphModifiers, inflected);
    }

    @Override
    public String[] getInflectedForms(String word, String unimorphModifiers) {
        return forms.getOrDefault(word.toLowerCase() + "|" + unimorphModifiers, new String[0]);
    }
}
