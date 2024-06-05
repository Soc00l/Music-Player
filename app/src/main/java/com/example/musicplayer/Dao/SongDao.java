package com.example.musicplayer.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.musicplayer.Entity.SongEntity;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

@Dao
public interface SongDao {
    @Insert
     void insert(SongEntity song);

    @Query("SELECT * FROM songs")
    List<SongEntity> getAllSongs();

    @Query("DELETE FROM songs WHERE name = :name AND singer = :singer")
    void deleteByNameAndSinger(String name, String singer);


    @Query("SELECT * FROM songs WHERE id = :id")
    SongEntity getSongById(int id);

    @Query("SELECT COUNT(*) FROM songs WHERE name = :name AND singer = :singer")
    int countFavorite(String name, String singer);

    @Delete
    void delete(SongEntity song);

    default void insertAsync(SongEntity song) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> insert(song));
    }


    default void insertAsync(SongEntity song, Runnable callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            insert(song);
            callback.run();
        });
    }



    default void countFavoriteAsync(String name, String singer, Consumer<Integer> callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            int count = countFavorite(name, singer);
            callback.accept(count);
        });
    }
    default List<SongEntity> getAllSongsAsync() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        List<SongEntity> songs = null;
        try {
            songs = executor.submit(this::getAllSongs).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        executor.shutdown();
        return songs;
    }

    default int countFavoriteAsync(String name, String singer) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int count = 0;
        try {
            Future<Integer> future = executor.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return countFavorite(name, singer);
                }
            });
            count = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        executor.shutdown();
        return count;
    }

    default void deleteByNameAndSingerAsync(String name, String singer,Runnable callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            deleteByNameAndSinger(name, singer);
            callback.run();
        });
    }
}
