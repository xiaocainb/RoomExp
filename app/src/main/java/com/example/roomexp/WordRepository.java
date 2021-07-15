package com.example.roomexp;

import android.content.Context;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;

//**************************************************************************************************
//repository是进一步把WordViewModel中写出来
class WordRepository {
    private LiveData<List<Word>> allWordsLive;
    private WordDao wordDao;

    WordRepository(Context context) {
        WordDatabase wordDatabase = WordDatabase.getDatabase(context.getApplicationContext());
        wordDao = wordDatabase.getWordDao();
        allWordsLive = wordDatabase.getWordDao().getAllWordsLive();
    }

    LiveData<List<Word>> getAllWordsLive() {
        return allWordsLive;
    }
//模糊匹配前后加＂％＂
    LiveData<List<Word>> findWordsWithPatten(String patten) {
        return wordDao.findWordsWithPatten("%" + patten + "%");
    }

    //  ******************************4个AsyncTask移到WordViewModel.class后写4个接口，供MainActivity使用
    void insertWords(Word... words) {
        new InsertAsyncTask(wordDao).execute(words);
    }

    void updatetWords(Word... words) {
        new UpdateAsyncTask(wordDao).execute(words);
    }

    void deleteAllWords(Word... words) {
        new DeleteAllAsyncTask(wordDao).execute();
    }

    void deleteWords(Word... words) {
        new DeleteAsyncTask(wordDao).execute(words);
    }

//  ******************************4个子线程AsyncTask用来执行wordDao对数据库的操作
    static class InsertAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao wordDao;

        InsertAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        //      工作线程中后台做任务
        @Override
        protected Void doInBackground(Word... words) {
//          数据库dao的insert放在后台进行
            wordDao.insertWords(words);
//          56行 publishProgress();
            return null;
        }
//          *********************************************************
//          *任务完成后结果带给主线程                                   *
//          *  @Override                                            *
//          *  protected void onPostExecute(Void unused) {          *
//          *      super.onPostExecute(unused);                     *
//          *  }                                                    *
//          *进度更新时调用                                           *
//          *  @Override                                            *
//          *  protected void onProgressUpdate(Void... values) {    *
//          *      super.onProgressUpdate(values);                  *
//          *  }                                                    *
//          *进行30行  后台任务之前调用                                 *
//          *  @Override                                            *
//          *  protected void onPreExecute() {                      *
//          *      super.onPreExecute();                            *
//          *  }                                                    *
//          *********************************************************
    }

    static class UpdateAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao wordDao;

        UpdateAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.updateWord(words);
            return null;
        }
    }

    static class DeleteAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao wordDao;

        DeleteAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.deleteWords(words);
            return null;
        }
    }

    static class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private WordDao wordDao;

        DeleteAllAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            wordDao.deleteAllWords();
            return null;
        }
    }
}
