package com.brendan.dadlibs;

import static org.junit.Assert.assertTrue;

import com.brendan.dadlibs.engine.Inflection;
import com.brendan.dadlibs.entity.WordList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(Parameterized.class)
public class MarkerGenTest {

    @Parameterized.Parameters(name = "{index}: input=\"{0}\" → expected prefix=\"{1}\"")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"Hello there, world!", "hello_there_world"},
                {"  Leading and trailing  ", "leading_and_trailing"},
                {"Multiple   spaces", "multiple_spaces"},
                {"Tabs\tand\nnewlines", "tabs_and_newlines"},
                {"Café au lait", "cafe_au_lait"},
                {"Crème brûlée", "creme_brulee"},
                {"coöperate", "cooperate"},
                {"naïve approach", "naive_approach"},
                {"François", "francois"},
                {"ñoño", "nono"},
                {"smiley 🙂 face", "smiley_face"},
                {"100% guaranteed!", "100_guaranteed"},
                {"C# and C++", "c_and_c"},
                {"e-mail@example.com", "emailexamplecom"},
                {"—Em dash—test—", "em_dashtest"},
                {"non-breaking\u00A0space", "nonbreaking_space"},
                {"thin space", "thin_space"},
                {"zero-width\u200Bspace", "zerowidthspace"},
                {"***", ""},
                {"   ", ""}
        });
    }

    private final String input;
    private final String expectedPrefix;

    public MarkerGenTest(String input, String expectedPrefix) {
        this.input = input;
        this.expectedPrefix = expectedPrefix;
    }

    @Test
    public void testNormalization() {
        String marker = new WordList(input, false, "")
                .getMarkerString(0, Inflection.SINGULAR);

        Pattern pattern = Pattern.compile("\\$\\{([A-Za-z0-9_\\-\\s]+)\\}");
        Matcher matcher = pattern.matcher(marker);

        assertTrue("Marker should match ${...} format for input: " + input, matcher.matches());

        String group = matcher.group(1);
        assertTrue(
                "Expected prefix: \"" + expectedPrefix + "\", but got: \"" + group + "\" (input was: \"" + input + "\")",
                group.startsWith(expectedPrefix)
        );
    }
}
