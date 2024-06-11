package com.example.musicplayer;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicplayer.Entity.Song;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {
    boolean IsPause=true;
    private Button  Star;
    private Button Local;
    private Button Recent;
    private  ImageButton pause;
    private  MusicService musicService;
    private boolean isServiceBound = false;
    private static final int REQUEST_CODE = 100;

    private BroadcastReceiver songChangedReceiver;

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
            boolean allGranted = true;
            boolean someDenied = false;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    someDenied = true;
                }
            }

            if (allGranted) {

            } else if (someDenied) {
                boolean shouldShowRationale = false;
                for (String permission : permissions) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        shouldShowRationale = true;
                        break;
                    }
                }
                if (shouldShowRationale) {
                    // Show a message to explain why the permissions are needed and prompt the user to grant them
                    showPermissionRationaleDialog();
                } else {
                    // Permissions were permanently denied, show a message and direct the user to settings
//                    showPermissionsDeniedMessage();
                }
            }
        }
    }
    private void showPermissionsDeniedMessage() {
        new AlertDialog.Builder(this)
                .setTitle("权限被拒绝")
                .setMessage("无法设置铃声，因为必要的权限被拒绝。请在设置中手动授予权限。")
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Permission request canceled
                        Toast.makeText(getApplicationContext(), "无法设置铃声，权限被拒绝", Toast.LENGTH_SHORT).show();
                    }
                })
                .create()
                .show();
    }
    private void showPermissionRationaleDialog() {
        new AlertDialog.Builder(this)
                .setTitle("权限请求")
                .setMessage("为了设置铃声，需要访问存储的权限。请允许这些权限。")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissionsIfNeeded();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Permission request canceled
                        Toast.makeText(getApplicationContext(), "无法设置铃声，权限被拒绝", Toast.LENGTH_SHORT).show();
                    }
                })
                .create()
                .show();
    }
    private void requestPermissionsIfNeeded() {
        List<String> permissionsNeeded = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_MEDIA_AUDIO);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.WRITE_SETTINGS);
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), REQUEST_CODE);
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
        //请求权限
        requestPermissionsIfNeeded();

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
        pause = findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(musicService==null||musicService.getCurrentSong()==null) {
                    Toast.makeText(MainActivity.this,"当前没有在播放的歌曲",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (IsPause) {
                        pause.setImageResource(R.drawable.pause_solid);
                        IsPause = !IsPause;
                        musicService.continuePlay();
                    } else {
                        pause.setImageResource(R.drawable.play_solid);
                        IsPause = !IsPause;
                        musicService.pausePlay();
                    }
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

        songChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MusicService.ACTION_SONG_CHANGED.equals(intent.getAction())) {
                    updateUI();
                }
                if (MusicService.ACTION_PLAY_CHANGED.equals(intent.getAction())) {
                    UpdatePlay(intent.getBooleanExtra("play",true));
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.ACTION_SONG_CHANGED);
        filter.addAction(MusicService.ACTION_PLAY_CHANGED);
        registerReceiver(songChangedReceiver, filter);
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
    public void UpdatePlay(boolean play)
    {
        if(play)
        {
               this.IsPause = false;
               pause.setImageResource(R.drawable.pause_solid);
        }
        else
        {
               this.IsPause = true;
               pause.setImageResource(R.drawable.play_solid);
        }
    }
}