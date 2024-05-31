package com.example.musicplayer;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerActivity extends AppCompatActivity {
    private  String mode = "order";
    private static SeekBar seekBar;
    private static TextView progress ;
    private static TextView total;
    private  boolean play=true;

    private String last ;//上一个页面
    private  Song song;

    private MusicService musicService;
    private boolean isServiceBound = false;
    private  TextView name;

    private TextView singer;

    private ImageButton isLove;

    private BroadcastReceiver songChangedReceiver;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 获取到 MusicService 实例
            MusicService.MusicControl binder = (MusicService.MusicControl) service;
            musicService = binder.getService();
            updateFavoriteButtonState(isLove);
            isServiceBound = true;
            // 在这里可以调用 MusicService 中的方法，如 playMusic()
            if (song != null) {
                musicService.playMusic(song);
            }
            UpdateUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
            musicService = null;
        }
    };

    private void bindMusicService() {
        Intent service_intent = new Intent(this, MusicService.class);
        service_intent.putExtra("song", song); // 将 Song 对象传递给 MusicService
        bindService(service_intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.player);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("song")) {
            this.song = intent.getParcelableExtra("song");
            name = findViewById(R.id.textViewSongName);
            singer = findViewById(R.id.textViewSingerName);
        }
        this.last = intent.getStringExtra("class");
        if (!isServiceBound) {
            bindMusicService();
        }
        if (intent.hasExtra("name")) {
            TextView name = findViewById(R.id.textViewSongName);
            name.setText(intent.getStringExtra("name"));
            TextView singer = findViewById(R.id.textViewSingerName);
            singer.setText(intent.getStringExtra("singer"));
            boolean IsPlay = intent.getBooleanExtra("Isplay", true);
            this.play = IsPlay;
        }
        songChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MusicService.ACTION_SONG_CHANGED.equals(intent.getAction())) {
                    song=  (Song) intent.getParcelableExtra("song");
                    UpdateUI();
                }
            }
        };
        IntentFilter filter = new IntentFilter(MusicService.ACTION_SONG_CHANGED);
        registerReceiver(songChangedReceiver, filter);



    //收起按钮
        ImageButton down = findViewById(R.id.down);
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PlayerActivity.this,MainActivity.class);
                switch (last)
                {
                    case "MainActivity":
                        intent.setClass(PlayerActivity.this,MainActivity.class);
                        break;

                    case "LocalMusicActivity":
                        intent.setClass(PlayerActivity.this,LocalMusicActivity.class);
                        break;

                    case "FavoriteActivity":
                        intent.setClass(PlayerActivity.this,FavoriteActivity.class);
                        break;

                    case "RecentlyPlayedActivity":
                        intent.setClass(PlayerActivity.this,RecentlyPlayedActivity.class);
                        break;
                }
                startActivity(intent);
            }
        });
        //上一首按钮
        ImageButton previous = findViewById(R.id.btnPrevious);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.playPreviousSong();
            }
        });
        //播放按钮
        ImageButton pause = findViewById(R.id.btnPlayPause);
        if(!play)
        {
            pause.setImageResource(R.drawable.play_solid);
        }
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  if (play == true)//播放状态
                  {
                      musicService.pausePlay();
                      play = false;
                      pause.setImageResource(R.drawable.play_solid);
                  }
                  else//暂停状态
                  {
                      musicService.continuePlay();
                      play = true;
                      pause.setImageResource(R.drawable.pause_solid);
                  }
            }
        });
        //下一首按钮
        ImageButton next = findViewById(R.id.btnNext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    musicService.playNextSong();
                    UpdateUI();
            }

        });
        //切换播放模式按钮
        ImageButton playMode = findViewById(R.id.playMode);
        playMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.equals("order")) {
                    mode = "repeat";
                    ChangeMode("repeat");
                    playMode.setImageResource(R.drawable._4gl_repeatonce2);
                }
                else if (mode.equals("repeat")) {
                    mode = "random";
                    ChangeMode("random");
                    playMode.setImageResource(R.drawable._4gl_shuffle);
                }
                else if (mode.equals("random")) {
                    mode = "order";
                    ChangeMode("order");
                    playMode.setImageResource(R.drawable._4gl_repeat2);
                }
            }
        });
        //我喜欢按钮
        isLove = findViewById(R.id.isLove);

        isLove.setOnClickListener(v -> {
            if (musicService != null) {
                AtomicBoolean result = musicService.toggleFavoriteAsync(song,() -> runOnUiThread(()->updateFavoriteButtonState(isLove)));
                if(result.get())
                {
                    Toast.makeText(getApplicationContext(),"加入收藏成功",Toast.LENGTH_LONG);
                }
                else {
                    Toast.makeText(getApplicationContext(),"取消收藏成功",Toast.LENGTH_LONG);
                }
            }
        });

        //列表按钮
        ImageButton  list = findViewById(R.id.playList);
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerActivity.this, LocalMusicActivity.class);
                startActivity(intent);
            }
        });
        //进度条
         seekBar = findViewById(R.id.seekBar);
         progress = findViewById(R.id.progress);
         total = findViewById(R.id.total);
         seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
             @Override
             public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

             }

             @Override
             public void onStartTrackingTouch(SeekBar seekBar) {

             }

             @Override
             public void onStopTrackingTouch(SeekBar seekBar) {
                 if (isServiceBound && musicService != null) {
                     musicService.seekTo(seekBar.getProgress());
                 }
             }
         });

    }
    @SuppressLint("HandlerLeak")
    public static Handler handler=new Handler(){//创建消息处理器对象
        //在主线程中处理从子线程发送过来的消息
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg){
            Bundle bundle=msg.getData();//获取从子线程发送过来的音乐播放进度
            //获取当前进度currentPosition和总时长duration
            int duration=bundle.getInt("duration");
            int currentPosition=bundle.getInt("currentPosition");
            //对进度条进行设置
            seekBar.setMax(duration);
            seekBar.setProgress(currentPosition);
            //歌曲是多少分钟多少秒钟
            int minute=duration/1000/60;
            int second=duration/1000%60;
            String strMinute=null;
            String strSecond=null;
            if(minute<10){//如果歌曲的时间中的分钟小于10
                strMinute="0"+minute;//在分钟的前面加一个0
            }else{
                strMinute=minute+"";
            }
            if (second<10){//如果歌曲中的秒钟小于10
                strSecond="0"+second;//在秒钟前面加一个0
            }else{
                strSecond=second+"";
            }
            //这里就显示了歌曲总时长

            total.setText(strMinute+":"+strSecond);
            //歌曲当前播放时长
            minute=currentPosition/1000/60;
            second=currentPosition/1000%60;
            if(minute<10){//如果歌曲的时间中的分钟小于10
                strMinute="0"+minute;//在分钟的前面加一个0
            }else{
                strMinute=minute+" ";
            }
            if (second<10){//如果歌曲中的秒钟小于10
                strSecond="0"+second;//在秒钟前面加一个0
            }else{
                strSecond=second+" ";
            }
            //显示当前歌曲已经播放的时间
            progress.setText(strMinute+":"+strSecond);
        }
    };
    protected void onDestroy() {
        super.onDestroy();
        // 解绑 MusicService
        if (isServiceBound) {
            unbindService(serviceConnection);
            isServiceBound = false;
        }
    }
    private void updateFavoriteButtonState(ImageButton isLove) {
        if (musicService != null && song != null) {
            boolean isFavorite = musicService.checkIfFavoriteAsync(song);
            if (isFavorite) {
                isLove.setImageResource(R.drawable.loved);
            } else {
                isLove.setImageResource(R.drawable.love);
            }
        }
    }
    private void ChangeMode(String mode)
    {
        musicService.ChangeMode(mode);
    }
    private void UpdateUI()
    {
        name.setText(song.getName());
        singer.setText(song.getSinger());
        boolean isFavorite = musicService.checkIfFavoriteAsync(song);
        if(isFavorite)
        {
            isLove.setImageResource(R.drawable.loved);
        }
    }

}
