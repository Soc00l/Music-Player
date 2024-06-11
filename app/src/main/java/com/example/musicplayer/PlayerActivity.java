package com.example.musicplayer;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.Entity.FolderSong;
import com.example.musicplayer.Entity.Song;
import com.example.musicplayer.Util.DatabaseHelper;
import com.example.musicplayer.Util.MusicLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class PlayerActivity extends AppCompatActivity {
    private  String mode = "order";
    private static SeekBar seekBar;
    private static TextView progress ;
    private static TextView total;
    private  boolean play=true;

    private String last ;//上一个页面
    private Song song;

    private MusicService musicService;
    private boolean isServiceBound = false;
    private  TextView name;
    private TextView singer;
    private ImageButton isLove;
    private ImageButton pause;
    private BroadcastReceiver songChangedReceiver;
    private int position=0;//保存播放进度
    private AppDatabase appDatabase;
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
                if(musicService.getCurrentSong()==null||!musicService.getCurrentSong().equals(song)) {
                    musicService.playMusic(song);
                }
            }
//            musicService.seekTo(position);
            song = musicService.getCurrentSong();
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

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.player);
        Intent intent = getIntent();
        appDatabase = DatabaseHelper.getDatabase(getApplicationContext());
        name = findViewById(R.id.textViewSongName);
        singer = findViewById(R.id.textViewSingerName);
        if (intent != null && intent.hasExtra("song")) {
            this.song = intent.getParcelableExtra("song");
        }
        position = intent.getIntExtra("position", 0);
        if(intent.hasExtra("class"))
        {
            this.last = intent.getStringExtra("class");
        }
        else {
            last = "none";
        }

        if (!isServiceBound) {
            bindMusicService();
        }
        if (intent.hasExtra("name")) {
            TextView name = findViewById(R.id.textViewSongName);
            name.setText(intent.getStringExtra("name"));
            TextView singer = findViewById(R.id.textViewSingerName);
            singer.setText(intent.getStringExtra("singer"));
            boolean IsPlay = intent.getBooleanExtra("Isplay", true);
            this.position = intent.getIntExtra("position",0);
            this.play = IsPlay;

        }
        songChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MusicService.ACTION_SONG_CHANGED.equals(intent.getAction())) {
                    song=  (Song) intent.getParcelableExtra("song");
                    UpdateUI();
                }
                if (MusicService.ACTION_PLAY_CHANGED.equals(intent.getAction())) {
                    UpdatePlay(intent.getBooleanExtra("play",true));
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.ACTION_SONG_CHANGED);
        filter.addAction(MusicService.ACTION_PLAY_CHANGED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(songChangedReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        }


        //收起按钮
        ImageButton down = findViewById(R.id.down);
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
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
                    default:
                        intent.setClass(PlayerActivity.this,MainActivity.class);
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
        pause = findViewById(R.id.btnPlayPause);
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
                showPopupMenu(v);
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

    private void  UpdatePlay(boolean play)
    {
        if(play)
        {
            this.play = true;
            pause.setImageResource(R.drawable.pause_solid);
        }
        else
        {
            this.play = false;
            pause.setImageResource(R.drawable.play_solid);
        }
    }
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_song_options, popupMenu.getMenu());
        // 动态设置菜单项的文本
        MenuItem viewAlbumItem = popupMenu.getMenu().findItem(R.id.action_view_album);
        viewAlbumItem.setTitle("查看专辑: " + song.getAlbum());

        MenuItem viewArtistItem = popupMenu.getMenu().findItem(R.id.action_view_artist);
        viewArtistItem.setTitle("查看歌手: " + song.getSinger());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_add_to_playlist) {
                    // 处理“添加到歌单”操作
                    addToPlaylist(song);
                    return true;
                } else if (id == R.id.action_view_album) {
                    // 处理“查看专辑”操作
                    viewAlbum(song.getAlbum());
                    return true;
                } else if (id == R.id.action_view_artist) {
                    // 处理“查看歌手”操作
                    viewArtist(song.getSinger());
                    return true;
                } else if (id == R.id.action_set_as_ringtone) {
                    // 处理“设为铃声”操作
                    setMyRingtone(song.getPath());
                    return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }
    private void addToPlaylist(Song song) {
        showAddToPlaylistDialog(song);
    }

    public void setMyRingtone(String path) {
        File sdfile = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, sdfile.getName());
        // 确定音频文件的 MIME 类型，例如 "audio/mpeg" 用于 MP3 文件
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdfile.getAbsolutePath());
        Uri newUri = this.getContentResolver().insert(uri, values);
        RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE, newUri);
        Toast.makeText(getApplicationContext(), "设置来电铃声成功！", Toast.LENGTH_SHORT).show();
        System.out.println("setMyRingtone()-----铃声");
    }

    private void showAddToPlaylistDialog(Song song) {
        MusicLoader musicLoader = new MusicLoader(this);
        List<String> folderNames = musicLoader.getAllFolderNames();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择歌单");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        arrayAdapter.addAll(folderNames);
        arrayAdapter.add("新建歌单");

        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selected = arrayAdapter.getItem(which);
                if (selected.equals("新建歌单")) {
                    showCreateNewPlaylistDialog(song);
                } else {
                    addSongToFolder(song, selected);
                }
            }
        });
        builder.show();
    }

    private void showCreateNewPlaylistDialog(Song song) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("新建歌单");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String folderName = input.getText().toString();
                if (!folderName.isEmpty()) {
                    MusicLoader musicLoader = new MusicLoader(getApplicationContext());
                    List<String> existingFolders = musicLoader.getAllFolderNames();
                    if (existingFolders.contains(folderName)) {
                        Toast.makeText(getApplicationContext(), "歌单已存在", Toast.LENGTH_SHORT).show();
                    } else {
                        addSongToFolder(song, folderName);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "歌单名不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addSongToFolder(Song song, String folderName) {
        FolderSong folderSong = new FolderSong();
        folderSong.setFolderName(folderName);
        folderSong.setSongName(song.getName());
        folderSong.setArtist(song.getSinger());
        folderSong.setAlbum(song.getAlbum());
        folderSong.setPath(song.getPath());

        // 检查歌曲是否已经在歌单中
        appDatabase.folderSongDao().isSongInFolderAsync(folderName, song.getName(), song.getSinger(), new Consumer<Boolean>() {
            @Override
            public void accept(Boolean isInFolder) {
                if (isInFolder) {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "歌曲已在歌单中", Toast.LENGTH_SHORT).show());
                } else {
                    appDatabase.folderSongDao().insertAsync(folderSong, new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "歌曲已添加到歌单 " + folderName, Toast.LENGTH_SHORT).show());
                        }
                    });
                }
            }
        });

    }
    private void viewAlbum(String albumName) {
        MusicLoader musicLoader = new MusicLoader(this);
        ArrayList<Song> albumSongs = musicLoader.getSongsByAlbum(albumName);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("title", albumName);
        intent.putParcelableArrayListExtra("songs", albumSongs);
        startActivity(intent);
    }

    private void viewArtist(String artistName) {
        MusicLoader musicLoader = new MusicLoader(this);
        ArrayList<Song> artistSongs = musicLoader.getSongsBySinger(artistName);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("title", artistName);
        intent.putParcelableArrayListExtra("songs", artistSongs);
        startActivity(intent);
    }

}
