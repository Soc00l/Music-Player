package com.example.musicplayer;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Room;
import android.content.Context;

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

    public static AppDatabase getDatabase(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "music-player-db")
                .addMigrations(MIGRATION_1_2) // 添加迁移策略
                .build();
    }
}
