package com.example.musicplayer;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteAdapter favoriteAdapter;
    private List<Song> favoriteSongs;

    private AppDatabase database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        loadFavoriteSongs();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        favoriteSongs = new ArrayList<>();
        favoriteAdapter = new FavoriteAdapter(favoriteSongs, this);
        recyclerView.setAdapter(favoriteAdapter);
        ImageButton back = findViewById(R.id.Main);
        back.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(FavoriteActivity.this,MainActivity.class);
            startActivity(intent);
        });

    }


    @SuppressLint("StaticFieldLeak")
    private void loadFavoriteSongs() {
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "music-player-db").build();
        new AsyncTask<Void, Void, List<SongEntity>>() {
            @Override
            protected List<SongEntity> doInBackground(Void... voids) {
                return database.songDao().getAllSongsAsync();
            }

            @Override
            protected void onPostExecute(List<SongEntity> songEntities) {
                if (songEntities != null && !songEntities.isEmpty()) {
                    favoriteSongs.clear();
                    for (SongEntity entity : songEntities) {
                        Song song = new Song(entity.name, entity.singer, entity.path);
                        favoriteSongs.add(song);
                    }
                    favoriteAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(FavoriteActivity.this, "No favorite songs found.", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }



}
