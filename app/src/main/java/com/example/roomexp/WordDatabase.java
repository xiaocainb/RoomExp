package com.example.roomexp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.jetbrains.annotations.NotNull;

//singleton单例
@Database(entities = {Word.class}, version = 2, exportSchema = false)
//修饰不可少 三参数 entities必须用集合
public abstract class WordDatabase extends RoomDatabase {
    private static WordDatabase INSTANCE;

    static WordDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), WordDatabase.class, "word_database")
//*****************************************************
//                .allowMainThreadQueries()
//                强制主线程查询      数据库操作不能在主线程，尽量在副线程
//*****************************************************
//                    .fallbackToDestructiveMigration()//******破坏式迁移数据库
                    .addMigrations(MIGRATION_1_2)//**********版本迁移
                    .build();
        }
        return INSTANCE;
    }

    public abstract WordDao getWordDao();

    //*****************************************版本迁移
//    static final Migration MIGRATION_2_3 = new Migration(2,3) {
//        @Override
//        public void migrate(@NonNull @NotNull SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE word ADD COLUMN foo_data INTEGER NOT NULL DEFAULT 1");
//        }
//    };
//    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(@NonNull @NotNull SupportSQLiteDatabase database) {
//            database.execSQL("CREATE TABLE word_temp (id INTEGER PRIMARY KEY NOT NULL,english_word TEXT," +
//                    "chinese_meaning TEXT)");
//            database.execSQL("INSERT INTO word_temp (id,english_word,chinese_meaning )" +
//                    "SELECT id,english_wrod,chinese_meaning FROM word");
//            database.execSQL("DROP TABLE word");
//            database.execSQL("ALTER TABLE word_temp RENAME TO word");
//        }
//    };
//    *********************************************************
    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull @NotNull SupportSQLiteDatabase database) {
//          数据库中boolean用INTEGER      缺省0是visible
            database.execSQL("ALTER TABLE word ADD COLUMN chinese_invisible INTEGER NOT NULL DEFAULT 0");
        }
    };
}
