package com.brendan.dadlibs;

import static org.junit.Assert.assertEquals;

import com.brendan.dadlibs.engine.Inflection;
import com.brendan.dadlibs.wordbuilder.NounBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import androidx.annotation.NonNull;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class NounBuilderTest {

    private final String input;
    private final Inflection type;
    private final String expected;

    public NounBuilderTest(String input, Inflection type, String expected) {
        this.input = input;
        this.type = type;
        this.expected = expected;
    }


    @Parameterized.Parameters(name = "{index}: getInflection({0}, {1}) = {2}")
    public static Iterable<Object[]> data() {

        return Arrays.asList(new Object[][]{
                {"cat", Inflection.SINGULAR, "cat"},
                {"cat", Inflection.PLURAL, "cats"},
                {"bus", Inflection.PLURAL, "buses"},
                {"fox", Inflection.PLURAL, "foxes"},
                {"blitz", Inflection.PLURAL, "blitzes"},
                {"church", Inflection.PLURAL, "churches"},
                {"bush", Inflection.PLURAL, "bushes"},
                {"baby", Inflection.PLURAL, "babies"},
                {"leaf", Inflection.PLURAL, "leaves"},
                {"little woman", Inflection.PLURAL, "little women"},
                {"leaf of gold", Inflection.PLURAL, "leaves of gold"},
                {"cat", Inflection.POSSESSIVE, "cat's"},
                {"woman", Inflection.PLURAL_POSSESSIVE, "women's"},
                {"baby", Inflection.PLURAL_POSSESSIVE, "babies'"},
                {"leaf of gold", Inflection.POSSESSIVE, "leaf of gold's"},
                {"leaf of gold", Inflection.PLURAL_POSSESSIVE, "leaves of gold's"},
                {"frenchman", Inflection.PLURAL, "frenchmen"},
        });
    }

    @NonNull
    private static MockUnimorphDao getMockUnimorphDao() {
        MockUnimorphDao dao = new MockUnimorphDao();
        dao.addForms("leaf", Inflection.PLURAL.getUnimorphModifiers(), "leaves");
        dao.addForms("woman", Inflection.PLURAL.getUnimorphModifiers(), "women");
        dao.addForms("Frenchman", Inflection.PLURAL.getUnimorphModifiers(), "Frenchmen");
        dao.addForms("leaf", Inflection.SINGULAR.getUnimorphModifiers(), "leaf");
        dao.addForms("woman", Inflection.SINGULAR.getUnimorphModifiers(), "woman");
        dao.addForms("Frenchman", Inflection.SINGULAR.getUnimorphModifiers(), "Frenchman");
        return dao;
    }

    @Test
    public void testGetInflection() {
        MockUnimorphDao dao = getMockUnimorphDao();
        NounBuilder builder = new NounBuilder(dao);

        String result = builder.getInflectedForm(input, type);
        assertEquals(expected, result);
    }
}
