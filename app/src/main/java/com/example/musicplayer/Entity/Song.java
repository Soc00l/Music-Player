package com.example.musicplayer.Entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Song implements Parcelable {
    private String name;
    private String path;
    private String singer;
    private String album;



    public Song(String name , String singer)
    {
        this.name = name;
        this.singer = singer;
    }
    public Song(String name,String singer,String path)
    {
        this.name = name;
        this.singer = singer;
        this.path = path;
    }

    public Song(String name, String path, String singer, String album) {
        this.name = name;
        this.path = path;
        this.singer = singer;
        this.album = album;
    }

    public Song()
    {

    }
    protected Song(Parcel in) {
        name = in.readString();
        path = in.readString();
        singer = in.readString();
        album = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
             dest.writeString(name);
             dest.writeString(path);
             dest.writeString(singer);
             dest.writeString(album);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(name, song.name) &&
                Objects.equals(path, song.path) &&
                Objects.equals(singer, song.singer) &&
                Objects.equals(album, song.album);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path, singer, album);
    }
}
