package com.example.musicplayer;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
}
