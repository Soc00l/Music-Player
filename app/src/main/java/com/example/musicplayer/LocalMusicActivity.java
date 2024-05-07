package com.example.musicplayer;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LocalMusicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.localmusic);
        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.FragmentContainerView, SingleSongFragment.newInstance())
                    .commit();
        }
        Button song = findViewById(R.id.song);
        song.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                Toast.makeText(LocalMusicActivity.this,"歌曲",Toast.LENGTH_LONG).show();
            }
        });

        Button album = findViewById(R.id.album);
        album.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                Toast.makeText(LocalMusicActivity.this,"专辑",Toast.LENGTH_LONG).show();
            }
        });

        Button singer = findViewById(R.id.singer);
        singer.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                Toast.makeText(LocalMusicActivity.this,"歌手",Toast.LENGTH_LONG).show();
            }
        });

        Button folder = findViewById(R.id.folder);
        folder.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                Toast.makeText(LocalMusicActivity.this,"文件夹",Toast.LENGTH_LONG).show();
            }
        });
    }




}
