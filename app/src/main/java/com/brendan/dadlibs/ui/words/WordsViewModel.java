package com.brendan.dadlibs.ui.words;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;

import com.brendan.dadlibs.dao.InflectionDao;
import com.brendan.dadlibs.dao.WordDao;
import com.brendan.dadlibs.dao.WordListDao;
import com.brendan.dadlibs.db.AppDatabase;
import com.brendan.dadlibs.engine.Inflection;
import com.brendan.dadlibs.engine.PartOfSpeech;
import com.brendan.dadlibs.entity.Word;
import com.brendan.dadlibs.entity.WordList;
import com.brendan.dadlibs.wordbuilder.BuilderProvider;
import com.brendan.dadlibs.wordbuilder.UnimoprhDatabase;
import com.brendan.dadlibs.wordbuilder.WordBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class WordsViewModel extends AndroidViewModel {

    private final WordDao wordDao;
    private final InflectionDao inflectionDao;
    private final WordListDao wordListDao;
    private final BuilderProvider builderProvider;
    private WordList wordList;
    private PartOfSpeech partOfSpeech;
    private WordBuilder wordBuilder;

    public interface WordsLoadedCallback {
        void onLoaded(List<Word> words);
    }

    public interface InflectionsLoadedCallback {
        void onLoaded(Map<Inflection, String> inflections);
    }

    public WordsViewModel(Application application) {
        super(application);
        wordDao = AppDatabase.getDatabase(application).wordDao();
        inflectionDao = AppDatabase.getDatabase(application).inflectionDao();
        wordListDao = AppDatabase.getDatabase(application).wordListDao();
        builderProvider = new BuilderProvider(application);
    }

    public WordList getWordList(){
        return wordList;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public void loadWords(long wordListId, WordsLoadedCallback callback){
        AppDatabase.executor.execute(() -> {
            this.wordList = wordListDao.getById(wordListId);
            partOfSpeech = PartOfSpeech.getByLabel(wordList.partOfSpeech);
            List<Word> words = wordDao.getAllFromList(wordList.id);
            wordBuilder = builderProvider.getWordBuilder(
                    PartOfSpeech.getByLabel(wordList.partOfSpeech));
            new Handler(Looper.getMainLooper()).post(() -> callback.onLoaded(words));
        });
    }

    public void loadWordInflections(Word word, InflectionsLoadedCallback callback){
        Map<Inflection, String> wordInflections = new HashMap<>();
        AppDatabase.executor.execute(() -> {
            for (Inflection inflection : partOfSpeech.getInflections()) {
                if (word == null)
                    wordInflections.put(inflection, "");
                else
                    inflectionDao.getInflection(word.id, inflection.getLabel());
            }
            new Handler(Looper.getMainLooper()).post(() ->
                    callback.onLoaded(wordInflections));
        });
    }

    public void buildWordInflections(String word, InflectionsLoadedCallback callback){
        Map<Inflection, String> wordInflections = new HashMap<>();
        UnimoprhDatabase.executor.execute(() -> {
            for (Inflection inflection : partOfSpeech.getInflections()){
                wordInflections.put(inflection, wordBuilder.getInflectedForm(word, inflection));
            }
            new Handler(Looper.getMainLooper()).post(() ->
                    callback.onLoaded(wordInflections));
        });
    }
}