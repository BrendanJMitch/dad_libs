package com.brendan.dadlibs.share;

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
}
