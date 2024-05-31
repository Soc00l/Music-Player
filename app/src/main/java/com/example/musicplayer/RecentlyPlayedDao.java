package com.example.musicplayer;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Dao
public interface RecentlyPlayedDao {
    @Insert
    void insert(RecentlyPlayedEntity song);

    @Query("SELECT * FROM recently_played ORDER BY timestamp DESC")
    List<RecentlyPlayedEntity> getAllRecentlyPlayed();

    @Query("SELECT COUNT(*) FROM recently_played")
    int getCount();

    @Query("DELETE FROM recently_played WHERE id = (SELECT id FROM recently_played ORDER BY timestamp ASC LIMIT 1)")
    void deleteOldest();

    @Query("DELETE FROM recently_played WHERE name = :name AND singer = :singer")
    void deleteByNameAndSinger(String name, String singer);

    @Delete
    void delete(RecentlyPlayedEntity song);

    default void deleteByNameAndSingerAsync(String name, String singer,Runnable callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            deleteByNameAndSinger(name, singer);
            callback.run();
        });
    }

    @Query("SELECT * FROM recently_played ORDER BY timestamp DESC LIMIT 1 OFFSET 1")
    RecentlyPlayedEntity getPreviousSong();

    default void getPreviousSongAsync(Consumer<Song> callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            RecentlyPlayedEntity songEntity = getPreviousSong();
            if (songEntity != null) {
                callback.accept(songEntity.toSong());
            } else {
                callback.accept(null);
            }
        });
    }
}
