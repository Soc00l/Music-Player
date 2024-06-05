package com.example.musicplayer.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "songs")
public class SongEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String path;
    public String singer;
    public String album;

    public SongEntity(String name, String path, String singer, String album) {
        this.name = name;
        this.path = path;
        this.singer = singer;
        this.album = album;
    }

    public static SongEntity fromSong(Song song) {
        return new SongEntity(song.getName(), song.getPath(), song.getSinger(), song.getAlbum());
    }

    public Song toSong() {
        return new Song(name, singer, path);
    }
}
