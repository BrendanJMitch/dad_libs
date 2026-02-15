package com.brendan.dadlibs.share;

import com.brendan.dadlibs.entity.SavedStory;
import com.brendan.dadlibs.entity.Template;

public final class ShareMapping {

    private ShareMapping() {}

    public static TemplateDto getTemplateDto(Template template) {
        return new TemplateDto(
                template.name,
                template.text
        );
    }

    public static Template getTemplateEntity(TemplateDto dto) {
        return new Template(
                dto.name,
                dto.text
        );
    }

    public static SavedStoryDto getSavedStoryDto(SavedStory story) {
        return new SavedStoryDto(
                story.name,
                story.text,
                story.timestamp,
                story.rating
        );
    }

    public static SavedStory getSavedStoryEntity(SavedStoryDto dto){
        return new SavedStory(
                dto.name,
                null,
                dto.text,
                dto.timestamp,
                dto.rating
        );
    }
}
