package com.brendan.dadlibs.repository;

import androidx.room.Transaction;

import com.brendan.dadlibs.dao.InflectionDao;
import com.brendan.dadlibs.dao.WordDao;
import com.brendan.dadlibs.dao.WordListDao;
import com.brendan.dadlibs.dao.WordWithInflections;
import com.brendan.dadlibs.entity.InflectedForm;
import com.brendan.dadlibs.entity.Word;
import com.brendan.dadlibs.entity.WordList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        List<WordWithInflections> wordsWithInflections =
                wordListDao.getWordsWithInflections(original.id);
        List<Word> newWords = new ArrayList<>();
        for (WordWithInflections w : wordsWithInflections) {
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