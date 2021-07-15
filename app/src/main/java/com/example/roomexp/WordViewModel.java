package com.example.roomexp;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class WordViewModel extends AndroidViewModel {
    private WordRepository wordRepository;

    public WordViewModel(@NonNull Application application) {
        super(application);
        wordRepository = new WordRepository(application);
    }

    public LiveData<List<Word>> getAllWordsLive() {
        return wordRepository.getAllWordsLive();
    }

    //模糊查询
    LiveData<List<Word>> findWordsWithPatten(String patten) {
        return wordRepository.findWordsWithPatten(patten);
    }

    //  ******************************4个AsyncTask移到WordViewModel.class后写4个接口，供mainActivity使用
    void insertWords(Word... words) {
        wordRepository.insertWords(words);
    }

    void updatetWords(Word... words) {
        wordRepository.updatetWords(words);
    }

    void deleteAllWords() {
        wordRepository.deleteAllWords();
    }

    void deleteWords(Word... words) {
        wordRepository.deleteWords();
    }

}
