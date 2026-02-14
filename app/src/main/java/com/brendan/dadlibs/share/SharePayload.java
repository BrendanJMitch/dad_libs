package com.brendan.dadlibs.share;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SharePayload {

    public final String format = "dadlibs-template-share";
    public final int version = 1;
    public List<TemplateDto> templates;
    public List<WordListDto> wordLists;
    public List<SavedStoryDto> savedStories;

    public SharePayload(List<TemplateDto> templates, List<WordListDto> wordLists, List<SavedStoryDto> savedStories) {
        this.templates = Objects.requireNonNullElseGet(templates, ArrayList::new);
        this.wordLists = Objects.requireNonNullElseGet(wordLists, ArrayList::new);
        this.savedStories = Objects.requireNonNullElseGet(savedStories, ArrayList::new);
    }
}
