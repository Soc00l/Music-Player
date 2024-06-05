package com.example.musicplayer.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recently_played")
public class RecentlyPlayedEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String path;
    public String singer;
    public String album;
    public long timestamp; // 用于排序

    public RecentlyPlayedEntity(String name, String path, String singer, String album, long timestamp) {
        this.name = name;
        this.path = path;
        this.singer = singer;
        this.album = album;
        this.timestamp = timestamp;
    }

    public static RecentlyPlayedEntity fromSong(Song song, long timestamp) {
        return new RecentlyPlayedEntity(song.getName(), song.getPath(), song.getSinger(), song.getAlbum(), timestamp);
    }

    public Song toSong() {
        return new Song(name, singer, path);
    }
}
