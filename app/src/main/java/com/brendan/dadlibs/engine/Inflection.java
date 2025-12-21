package com.brendan.dadlibs.engine;

import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;

public enum Inflection {
    SINGULAR("singular", "Singular", "N;SG"),
    PLURAL("plural", "Plural", "N;PL"),
    POSSESSIVE("possessive", "Possessive", ""),
    PLURAL_POSSESSIVE("plural_possessive", "Plural Possessive", ""),

    PRESENT("present", "Present", "V;NFIN;IMP+SBJV"),
    THIRD_PERSON_PRESENT("third_person_present", "3rd-person Present", "V;PRS;3;SG"),
    SIMPLE_PAST("simple_past", "Simple Past", "V;PST"),
    PRESENT_PARTICIPLE("present_participle", "Present Participle", "V;V.PTCP;PRS"),
    PAST_PARTICIPLE("past_participle", "Past Participle", "V;V.PTCP;PST"),

    ABSOLUTE("absolute", "Absolute", "ADJ;"),
    COMPARATIVE("comparative", "Comparative", "ADJ;CMPR"),
    SUPERLATIVE("superlative", "Superlative", "ADJ;SPRL"),

    OTHER("other", "Other", "");

    private final String label;
    private final String unimorphModifiers;
    private final String displayName;
    private static final Map<String, Inflection> labelMap = new TreeMap<>();

    static {
        for (Inflection p : EnumSet.allOf(Inflection.class)){
            labelMap.put(p.label, p);
        }
    }

    public static Inflection getByLabel(String label){
        return labelMap.get(label);
    }
    Inflection(String label, String displayName, String unimorphModifiers){
        this.label = label;
        this.displayName = displayName;
        this.unimorphModifiers = unimorphModifiers;
    }

    public String getLabel() {
        return label;
    }

    public String getDisplayName(){
        return displayName;
    }
    public String getUnimorphModifiers() {
        return unimorphModifiers;
    }
}
