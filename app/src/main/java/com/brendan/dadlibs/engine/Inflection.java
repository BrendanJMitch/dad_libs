package com.brendan.dadlibs.engine;

public enum Inflection {
    SINGULAR("singular", "N;SG"),
    PLURAL("plural", "N;PL"),
    POSSESSIVE("possessive", ""),
    PLURAL_POSSESSIVE("plural_possessive", ""),

    PRESENT("present", "V;NFIN;IMP+SBJV"),
    THIRD_PERSON_PRESENT("third_person_present", "V;PRS;3;SG"),
    SIMPLE_PAST("simple_past", "V;PST"),
    PRESENT_PARTICIPLE("present_participle", "V;V.PTCP;PRS"),
    PAST_PARTICIPLE("past_participle", "V;V.PTCP;PST"),

    ABSOLUTE("absolute", "ADJ;"),
    COMPARATIVE("comparative", "ADJ;CMPR"),
    SUPERLATIVE("superlative", "ADJ;SPRL"),

    OTHER("adverb", "");

    private final String label;
    private final String unimorphModifiers;

    Inflection(String label, String unimorphModifiers){
        this.label = label;
        this.unimorphModifiers = unimorphModifiers;
    }

    public String getLabel() {
        return label;
    }
    public String getUnimorphModifiers() {
        return unimorphModifiers;
    }
}
