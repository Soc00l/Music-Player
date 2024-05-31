package com.example.musicplayer;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {SongEntity.class, RecentlyPlayedEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SongDao songDao();
    public abstract RecentlyPlayedDao recentlyPlayedDao();
}
