package com.brendan.dadlibs;

import static org.junit.Assert.assertEquals;

import androidx.annotation.NonNull;

import com.brendan.dadlibs.engine.Inflection;
import com.brendan.dadlibs.wordbuilder.AdjectiveBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class AdjectiveBuilderTest {

    private final String input;
    private final Inflection type;
    private final String expected;

    public AdjectiveBuilderTest(String input, Inflection type, String expected) {
        this.input = input;
        this.type = type;
        this.expected = expected;
    }

    @Parameterized.Parameters(name = "{index}: getInflection({0}, {1}) = {2}")
    public static Iterable<Object[]> data() {

        return Arrays.asList(new Object[][]{
                {"good", Inflection.ABSOLUTE, "good"},
                {"good", Inflection.COMPARATIVE, "better"},
                {"good", Inflection.SUPERLATIVE, "best"},
                {"bad", Inflection.ABSOLUTE, "bad"},
                {"bad", Inflection.COMPARATIVE, "worse"},
                {"bad", Inflection.SUPERLATIVE, "worst"},
                {"ugly", Inflection.ABSOLUTE, "ugly"},
                {"ugly", Inflection.COMPARATIVE, "uglier"},
                {"ugly", Inflection.SUPERLATIVE, "ugliest"},
                {"fun", Inflection.ABSOLUTE, "fun"},
                {"fun", Inflection.COMPARATIVE, "funner"},
                {"fun", Inflection.SUPERLATIVE, "funnest"},
                {"blue", Inflection.ABSOLUTE, "blue"},
                {"blue", Inflection.COMPARATIVE, "bluer"},
                {"blue", Inflection.SUPERLATIVE, "bluest"},
                {"small", Inflection.ABSOLUTE, "small"},
                {"small", Inflection.COMPARATIVE, "smaller"},
                {"small", Inflection.SUPERLATIVE, "smallest"},
                {"Good and Bad and Ugly", Inflection.COMPARATIVE, "Better and Worse and Uglier"},
        });
    }

    @NonNull
    private static MockUnimorphDao getMockUnimorphDao() {
        MockUnimorphDao dao = new MockUnimorphDao();
        dao.addForms("good", Inflection.ABSOLUTE.getUnimorphModifiers(), "good");
        dao.addForms("good", Inflection.COMPARATIVE.getUnimorphModifiers(), "better");
        dao.addForms("good", Inflection.SUPERLATIVE.getUnimorphModifiers(), "best");
        dao.addForms("bad", Inflection.ABSOLUTE.getUnimorphModifiers(), "bad");
        dao.addForms("bad", Inflection.COMPARATIVE.getUnimorphModifiers(), "worse");
        dao.addForms("bad", Inflection.SUPERLATIVE.getUnimorphModifiers(), "worst");
        dao.addForms("ugly", Inflection.ABSOLUTE.getUnimorphModifiers(), "ugly");
        return dao;
    }

    @Test
    public void testGetInflection() {
        MockUnimorphDao dao = getMockUnimorphDao();
        AdjectiveBuilder builder = new AdjectiveBuilder(dao);

        String result = builder.getInflectedForm(input, type);
        assertEquals(expected, result);
    }
}
