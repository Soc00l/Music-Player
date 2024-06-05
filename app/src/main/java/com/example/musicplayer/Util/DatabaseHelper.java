package com.example.musicplayer.Util;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Room;
import android.content.Context;

import com.example.musicplayer.AppDatabase;

public class DatabaseHelper {

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `recently_played` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`name` TEXT, " +
                    "`path` TEXT, " +
                    "`singer` TEXT, " +
                    "`album` TEXT, " +
                    "`timestamp` INTEGER NOT NULL)");
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `foldersongs` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`folderName` TEXT, " +
                    "`songName` TEXT, " +
                    "`artist` TEXT, " +
                    "`album` TEXT, " +
                    "`path` TEXT)");
        }
    };

    public static AppDatabase getDatabase(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "music-player-db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // 添加所有迁移策略
                .build();
    }
}
