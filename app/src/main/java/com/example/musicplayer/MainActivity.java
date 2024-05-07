package com.example.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity  {
    boolean IsPause=true;
    private Button  Star;
    private Button Local;
    private Button Recent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //收藏按钮
        Star = findViewById(R.id.star);
        Star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //最近按钮
        Recent = findViewById(R.id.recent);
        Recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //本地按钮
        Local = findViewById(R.id.local);
        Local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent();
               intent.setClass(MainActivity.this,LocalMusicActivity.class);
               startActivity(intent);
            }
        });
        ImageButton pause = findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(IsPause)
                {
                    pause.setImageResource(R.drawable.pause_solid);
                    IsPause =!IsPause;
                }
                else {
                    pause.setImageResource(R.drawable.play_solid);
                    IsPause =!IsPause;
                }
            }
        });

    }


}