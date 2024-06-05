package com.example.musicplayer;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.musicplayer.Dao.FolderSongDao;
import com.example.musicplayer.Dao.RecentlyPlayedDao;
import com.example.musicplayer.Dao.SongDao;
import com.example.musicplayer.Entity.FolderSong;
import com.example.musicplayer.Entity.RecentlyPlayedEntity;
import com.example.musicplayer.Entity.SongEntity;

@Database(entities = {SongEntity.class, RecentlyPlayedEntity.class, FolderSong.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SongDao songDao();
    public abstract RecentlyPlayedDao recentlyPlayedDao();
    public abstract FolderSongDao folderSongDao();
}
