package com.example.musicplayer;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

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
    public Song()
    {

    }
    protected Song(Parcel in) {
        name = in.readString();
        path = in.readString();
        singer = in.readString();
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
    }
}
