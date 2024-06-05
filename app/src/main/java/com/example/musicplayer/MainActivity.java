package com.example.musicplayer;
import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicplayer.Entity.Song;

public class MainActivity extends AppCompatActivity  {
    boolean IsPause=true;
    private Button  Star;
    private Button Local;
    private Button Recent;

    private  MusicService musicService;
    private boolean isServiceBound = false;

    private static final int REQUEST_CODE = 1;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicControl binder = (MusicService.MusicControl) service;
            musicService = binder.getService();
            isServiceBound = true;
            IsPause = !musicService.IsPlaying();
            updateUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
            musicService = null;
        }
    };
    private void bindMusicService() {
        Intent serviceIntent = new Intent(this, MusicService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    private void updateUI() {
        if (isServiceBound) {
            Song currentSong = musicService.getCurrentSong();
            boolean isPlaying = musicService.IsPlaying();
            if (currentSong != null) {
                TextView songNameTextView = findViewById(R.id.SongName);
                TextView artistTextView = findViewById(R.id.SingerName);
                songNameTextView.setText(currentSong.getName());
                artistTextView.setText(currentSong.getSinger());
            }
            ImageButton playPauseButton = findViewById(R.id.pause);
            if (isPlaying)
            {
                playPauseButton.setImageResource(R.drawable.pause_solid);
            }
            else {
                playPauseButton.setImageResource(R.drawable.play_solid);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，加载音乐

            } else {
                // 权限被拒绝，显示提示
                Toast.makeText(this, "权限被拒绝，无法读取音乐文件。", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindMusicService();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //触发歌曲扫描
        triggerMediaScan("/storage/emulated/0/Music");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, REQUEST_CODE);
            } else {
                // 权限已经被授予，加载音乐

            }
        } else {
            // 对于Android 13以下版本，使用旧的权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
            } else {
                // 权限已经被授予，加载音乐
            }
        }

        //收藏按钮
        Star = findViewById(R.id.star);
        Star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent();
               intent.setClass(MainActivity.this,FavoriteActivity.class);
               startActivity(intent);
            }
        });
        //最近按钮
        Recent = findViewById(R.id.recent);
        Recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,RecentlyPlayedActivity.class);
                startActivity(intent);
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
        //暂停按钮
        ImageButton pause = findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(IsPause)
                {
                    pause.setImageResource(R.drawable.pause_solid);
                    IsPause =!IsPause;
                    musicService.continuePlay();
                }
                else {
                    pause.setImageResource(R.drawable.play_solid);
                    IsPause =!IsPause;
                    musicService.pausePlay();
                }
            }
        });
        //
        TextView Songname = findViewById(R.id.SongName);
        Songname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Songname.getText().equals("未在播放")) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, PlayerActivity.class);
                    Song song =musicService.getCurrentSong();
                    intent.putExtra("name",song.getName());
                    intent.putExtra("singer",song.getSinger());
                    intent.putExtra("class","MainActivity");
                    intent.putExtra("position",musicService.getProgress());
                    intent.putExtra("Isplay",!IsPause);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this,"当前没有在播放的歌曲",Toast.LENGTH_SHORT).show();
                }
            }
        });
        ImageButton list = findViewById(R.id.list);
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,LocalMusicActivity.class);
                startActivity(intent);
            }
        });
    }
    public void triggerMediaScan(String filePath) {
        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{filePath}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        System.out.println("Scanned " + path + ":");
                        System.out.println("-> uri=" + uri);
                    }
                });
    }


}