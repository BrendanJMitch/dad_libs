package com.brendan.dadlibs.repository;

import androidx.room.Transaction;

import com.brendan.dadlibs.data.dao.InflectionDao;
import com.brendan.dadlibs.data.dao.WordDao;
import com.brendan.dadlibs.data.dao.WordListDao;
import com.brendan.dadlibs.data.entity.InflectedForm;
import com.brendan.dadlibs.data.entity.Word;
import com.brendan.dadlibs.data.entity.WordList;
import com.brendan.dadlibs.data.relation.WordWithInflectedForms;

import java.util.ArrayList;
import java.util.List;

public class WordListRepository {

    private final WordListDao wordListDao;
    private final WordDao wordDao;
    private final InflectionDao inflectionDao;

    public WordListRepository(WordListDao dao, WordDao wordDao, InflectionDao inflectionDao) {
        this.wordListDao = dao;
        this.wordDao = wordDao;
        this.inflectionDao = inflectionDao;
    }

    @Transaction
    public long deepCopyWordList(WordList original, String name, String singularName) {
        WordList newList = new WordList(name, singularName, false, original.partOfSpeech);
        long newWordListId = wordListDao.insert(newList);

        List<WordWithInflectedForms> wordsWithInflections =
                wordListDao.getWordListWithWords(original.id).words;
        List<Word> newWords = new ArrayList<>();
        for (WordWithInflectedForms w : wordsWithInflections) {
            Word copy = w.word;
            copy.id = null;
            copy.wordListId = newWordListId;
            newWords.add(copy);
        }
        List<Long> newWordIds = wordDao.insert(newWords);

        List<InflectedForm> newInflections = new ArrayList<>();
        for (int i = 0; i < wordsWithInflections.size(); i++) {
            for (InflectedForm inflectedForm : wordsWithInflections.get(i).inflectedForms){
                inflectedForm.wordId = newWordIds.get(i);
            }
            newInflections.addAll(wordsWithInflections.get(i).inflectedForms);
        }

        inflectionDao.insert(newInflections);
        return newWordListId;
    }
}