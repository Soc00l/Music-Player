package com.example.musicplayer.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.musicplayer.Entity.FolderSong;
import com.example.musicplayer.Entity.SongEntity;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Dao
public interface FolderSongDao {
    @Insert
    void insert(FolderSong folderSong);

    @Query("SELECT * FROM foldersongs")
    List<FolderSong> getAllFolderSongs();

    @Query("SELECT DISTINCT folderName FROM foldersongs")
    List<String> getAllFolderNames();

    @Query("SELECT * FROM foldersongs WHERE folderName = :folderName")
    List<FolderSong> getSongsByFolderName(String folderName);

    @Query("SELECT COUNT(*) FROM foldersongs WHERE folderName = :folderName AND songName = :songName AND artist = :artist")
    int isSongInFolder(String folderName, String songName, String artist);

    @Query("DELETE FROM foldersongs WHERE folderName = :folderName AND songName = :songName AND artist = :artist")
    void deleteByFolderNameAndSong(String folderName, String songName, String artist);

    @Query("SELECT COUNT(*) FROM foldersongs WHERE folderName = :folderName AND songName = :songName AND artist = :artist")
    int countSongInFolder(String folderName, String songName, String artist);

    @Query("DELETE FROM foldersongs WHERE folderName = :folderName")
    void deleteFolder(String folderName);

    default void deleteFolderAsync(String folderName) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            deleteFolder(folderName);
        });
    }
    default void insertAsync(FolderSong folderSong, Runnable callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            insert(folderSong);
            if (callback != null) {
                callback.run();
            }
        });
    }

    default List<FolderSong> getAllFolderSongsAsync() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        List<FolderSong> folderSongs = null;
        try {
            folderSongs = executor.submit(this::getAllFolderSongs).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        executor.shutdown();
        return folderSongs;
    }

    default List<String> getAllFolderNamesAsync() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        List<String> names = null;
        try {
            names = executor.submit(this::getAllFolderNames).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        executor.shutdown();
        return names;
    }

    default Future<List<FolderSong>> getSongsByFolderNameAsync(String folderName) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        return executor.submit(() -> getSongsByFolderName(folderName));
    }

    default void isSongInFolderAsync(String folderName, String songName, String artist, Consumer<Boolean> callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            int count = countSongInFolder(folderName, songName, artist);
            callback.accept(count > 0);
        });
    }

    default void deleteByFolderNameAndSongAsync(String folderName, String songName, String artist, Runnable callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            deleteByFolderNameAndSong(folderName, songName, artist);
            callback.run();
        });
    }
}
