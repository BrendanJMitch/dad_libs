package com.brendan.dadlibs.share;

import com.brendan.dadlibs.data.entity.InflectedForm;
import com.brendan.dadlibs.data.entity.SavedStory;
import com.brendan.dadlibs.data.entity.Template;
import com.brendan.dadlibs.data.entity.Word;
import com.brendan.dadlibs.data.entity.WordList;
import com.brendan.dadlibs.data.relation.WordListWithWords;
import com.brendan.dadlibs.data.relation.WordWithInflectedForms;

import java.util.ArrayList;
import java.util.List;

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
                dto.rating);
    }

    public static WordList getWordListEntity(WordListDto dto){
        return new WordList(
                dto.name,
                dto.singularName,
                false,
                dto.marker,
                dto.partOfSpeech);
    }

    public static Word getWordEntity(WordDto dto){
        return new Word(dto.word, null);
    }

    public static InflectedForm getInflectedFormEntity(InflectedFormDto dto){
        return new InflectedForm(
                dto.type,
                dto.inflectedForm
        );
    }

    public static WordWithInflectedForms getWordWithInflectedForms(WordDto dto){
        WordWithInflectedForms word = new WordWithInflectedForms();
        word.word = getWordEntity(dto);
        word.inflectedForms = new ArrayList<>();
        for (InflectedFormDto inflectedForm : dto.inflectedForms){
            word.inflectedForms.add(getInflectedFormEntity(inflectedForm));
        }
        return word;
    }

    public static WordListWithWords getWordListWithWords(WordListDto dto){
        WordListWithWords list = new WordListWithWords();
        list.wordList = getWordListEntity(dto);
        list.words = new ArrayList<>();
        for (WordDto word : dto.words){
            list.words.add(getWordWithInflectedForms(word));
        }
        return list;
    }

    public static InflectedFormDto getInflectedFormDto(InflectedForm form) {
        return new InflectedFormDto(
                form.type,
                form.inflectedForm);
    }

    public static WordDto getWordDto(WordWithInflectedForms wordWithInflectedForms){
        List<InflectedFormDto> inflectedFormDtos = new ArrayList<>();
        for (InflectedForm form : wordWithInflectedForms.inflectedForms){
            inflectedFormDtos.add(getInflectedFormDto(form));
        }
        return new WordDto(
                wordWithInflectedForms.word.word,
                inflectedFormDtos);
    }

    public static WordListDto getWordListDto(WordListWithWords wordList) {
        List<WordDto> wordDtos = new ArrayList<>();
        for (WordWithInflectedForms word : wordList.words) {
            wordDtos.add(getWordDto(word));
        }
        return new WordListDto(
                wordList.wordList.name,
                wordList.wordList.singularName,
                wordList.wordList.marker,
                wordList.wordList.partOfSpeech,
                wordDtos);
    }
}
