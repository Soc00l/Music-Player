package com.example.musicplayer.Entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "foldersongs")
public class FolderSong implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String folderName;
    private String songName;
    private String artist;
    private String album;
    private String path;

    public FolderSong()
    {

    }
    protected FolderSong(Parcel in) {
        id = in.readInt();
        folderName = in.readString();
        songName = in.readString();
        artist = in.readString();
        album = in.readString();
        path = in.readString();
    }

    public static final Creator<FolderSong> CREATOR = new Creator<FolderSong>() {
        @Override
        public FolderSong createFromParcel(Parcel in) {
            return new FolderSong(in);
        }

        @Override
        public FolderSong[] newArray(int size) {
            return new FolderSong[size];
        }
    };

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFolderName() { return folderName; }
    public void setFolderName(String folderName) { this.folderName = folderName; }

    public String getSongName() { return songName; }
    public void setSongName(String songName) { this.songName = songName; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
              dest.writeInt(id);
              dest.writeString(folderName);
              dest.writeString(songName);
              dest.writeString(artist);
              dest.writeString(album);
              dest.writeString(path);
    }
}
