package com.example.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecentlyPlayedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecentlyPlayedAdapter recentlyPlayedAdapter;
    private List<Song> recentlyPlayedSongs;
    private MusicService musicService;

    private AppDatabase database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recently_played);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recentlyPlayedSongs = new ArrayList<>();
        recentlyPlayedAdapter = new RecentlyPlayedAdapter(recentlyPlayedSongs, this);
        recyclerView.setAdapter(recentlyPlayedAdapter);

        musicService = new MusicService();
        loadRecentlyPlayedSongs();
        ImageButton back = findViewById(R.id.recback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(RecentlyPlayedActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadRecentlyPlayedSongs() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "music-player-db").build();
            List<RecentlyPlayedEntity> entities = database.recentlyPlayedDao().getAllRecentlyPlayed();
            recentlyPlayedSongs.clear();
            for (RecentlyPlayedEntity entity : entities) {
                recentlyPlayedSongs.add(entity.toSong());
            }
            runOnUiThread(() -> recentlyPlayedAdapter.notifyDataSetChanged());
        });
    }
}
