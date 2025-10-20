package com.brendan.dadlibs;

import static org.junit.Assert.assertEquals;

import androidx.annotation.NonNull;

import com.brendan.dadlibs.engine.Inflection;
import com.brendan.dadlibs.wordbuilder.VerbBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class VerbBuilderTest {

    private final String input;
    private final Inflection type;
    private final String expected;

    public VerbBuilderTest(String input, Inflection type, String expected) {
        this.input = input;
        this.type = type;
        this.expected = expected;
    }


    @Parameterized.Parameters(name = "{index}: getInflection({0}, {1}) = {2}")
    public static Iterable<Object[]> data() {

        return Arrays.asList(new Object[][]{
                {"eat", Inflection.PRESENT, "eat"},
                {"eat", Inflection.SIMPLE_PAST, "ate"},
                {"eat", Inflection.THIRD_PERSON_PRESENT, "eats"},
                {"eat", Inflection.PAST_PARTICIPLE, "eaten"},
                {"eat", Inflection.PRESENT_PARTICIPLE, "eating"},
                {"Eat", Inflection.SIMPLE_PAST, "Ate"},
                {"knot", Inflection.SIMPLE_PAST, "knotted"},
                {"knot", Inflection.PRESENT_PARTICIPLE, "knotting"},
                {"knot", Inflection.PAST_PARTICIPLE, "knotted"},
                {"knot", Inflection.THIRD_PERSON_PRESENT, "knots"},
                {"tone", Inflection.SIMPLE_PAST, "toned"},
                {"tone", Inflection.PRESENT_PARTICIPLE, "toning"},
                {"tone", Inflection.PAST_PARTICIPLE, "toned"},
                {"jump", Inflection.PRESENT, "jump"},
                {"jump", Inflection.SIMPLE_PAST, "jumped"},
                {"jump", Inflection.PRESENT_PARTICIPLE, "jumping"},
                {"jump", Inflection.PAST_PARTICIPLE, "jumped"},
                {"jump", Inflection.THIRD_PERSON_PRESENT, "jumps"},
                {"bully", Inflection.THIRD_PERSON_PRESENT, "bullies"},
                {"bully", Inflection.SIMPLE_PAST, "bullied"},
                {"annoy", Inflection.THIRD_PERSON_PRESENT, "annoys"},
                {"annoy", Inflection.SIMPLE_PAST, "annoyed"},
        });
    }

    @NonNull
    private static MockUnimorphDao getMockUnimorphDao() {
        MockUnimorphDao dao = new MockUnimorphDao();
        dao.addForms("eat", Inflection.PRESENT.getUnimorphModifiers(), "eat");
        dao.addForms("eat", Inflection.SIMPLE_PAST.getUnimorphModifiers(), "ate");
        dao.addForms("eat", Inflection.THIRD_PERSON_PRESENT.getUnimorphModifiers(), "eats");
        dao.addForms("eat", Inflection.PAST_PARTICIPLE.getUnimorphModifiers(), "eaten");
        dao.addForms("eat", Inflection.PRESENT_PARTICIPLE.getUnimorphModifiers(), "eating");
        return dao;
    }

    @Test
    public void testGetInflection() {
        MockUnimorphDao dao = getMockUnimorphDao();
        VerbBuilder builder = new VerbBuilder(dao);

        String result = builder.getInflectedForm(input, type);
        assertEquals(expected, result);
    }
}
