package com.example.roomexp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao                                         //修饰不可少Database access object数据库进入
public interface WordDao {
    @Insert
    void insertWords(Word... words);

    @Update
    void updateWord(Word... words);

    @Delete
    void deleteWords(Word... words);

    @Query("DELETE FROM word")
    void deleteAllWords();

    @Query("SELECT *FROM word ORDER BY id DESC")
//    List<Word> getAllWords();
    LiveData<List<Word>> getAllWordsLive();

    //    后面可以跟很多@QUERY
    @Query("SELECT *FROM word WHERE english_word LIKE :patten ORDER BY id DESC")
    LiveData<List<Word>> findWordsWithPatten(String patten);

}
