package com.example.roomexp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity                                                       //修饰不可少
public class Word {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "english_word")
    private String word;
    @ColumnInfo(name = "chinese_meaning")
    private String ChineseMeaning;
    @ColumnInfo(name = "chinese_invisible")
    private boolean chineseInvisible;

    boolean isChineseInvisible() {
        return chineseInvisible;
    }

    void setChineseInvisible(boolean chineseInvisible) {
        this.chineseInvisible = chineseInvisible;
    }

    public Word() {
    }

    public Word(String word, String chineseMeaning) {
        this.word = word;
        ChineseMeaning = chineseMeaning;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getChineseMeaning() {
        return ChineseMeaning;
    }

    public void setChineseMeaning(String chineseMeaning) {
        ChineseMeaning = chineseMeaning;
    }
}
