package com.example.musicplayer;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

import androidx.core.app.NotificationCompat;

import com.example.musicplayer.Dao.RecentlyPlayedDao;
import com.example.musicplayer.Entity.RecentlyPlayedEntity;
import com.example.musicplayer.Entity.Song;
import com.example.musicplayer.Entity.SongEntity;
import com.example.musicplayer.Util.DatabaseHelper;
import com.example.musicplayer.Util.MusicLoader;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class MusicService extends Service {
    //声明一个MediaPlayer引用
    private MediaPlayer player;
    //声明一个计时器引用
    private Timer timer;

    private boolean IsPlaying=true;
    private Song song;
    private  String mode = "order";
    private List<Song> SongList;
    public AppDatabase database;

    public static final String ACTION_SONG_CHANGED = "com.example.musicplayer.ACTION_SONG_CHANGED";
    public static final String ACTION_PLAY_PAUSE = "com.example.musicplayer.ACTION_PLAY_PAUSE";
    public static final String ACTION_OPEN_PLAYER = "com.example.musicplayer.ACTION_OPEN_PLAYER";
    public static final String ACTION_PREVIOUS = "com.example.musicplayer.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.example.musicplayer.ACTION_NEXT";


    @Override
    public void onCreate(){
        super.onCreate();
        //创建音乐播放器对象
        player=new MediaPlayer();
        this.database = DatabaseHelper.getDatabase(getApplicationContext());
        MusicLoader musicLoader = new MusicLoader(getApplicationContext());
        //获取歌单
        this.SongList = musicLoader.getMusic();
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            // 检查是否有歌曲信息并播放
            if (intent.hasExtra("song")) {
                Song song = intent.getParcelableExtra("song");
                if (song != null) {
                    playMusic(song);
                }
            }

            // 处理其他动作
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                        case ACTION_PLAY_PAUSE:
                            if (IsPlaying) {
                                pausePlay();
                            } else {
                                continuePlay();
                            }
                            break;
                        case ACTION_PREVIOUS:
                            playPreviousSong();
                            break;
                        case ACTION_NEXT:
                            playNextSong();
                            break;
                        case ACTION_OPEN_PLAYER:
                            Intent playerIntent = new Intent(this, PlayerActivity.class);
                            playerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            playerIntent.putExtra("song", song);
                            playerIntent.putExtra("position", player.getCurrentPosition());
                            startActivity(playerIntent);
                            break;
                        default:
                            if (intent.hasExtra("song")) {
                                Song song = intent.getParcelableExtra("song");
                                playMusic(song);
                            }
                            break;
                }
            }
        }
        return START_NOT_STICKY;
    }

    @SuppressLint("ForegroundServiceType")
    private void showNotification(Song song) {
        Intent playPauseIntent = new Intent(this, MusicService.class);
        playPauseIntent.setAction(ACTION_PLAY_PAUSE);
        PendingIntent playPausePendingIntent = PendingIntent.getService(this, 0, playPauseIntent,  PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent previousIntent = new Intent(this, MusicService.class);
        previousIntent.setAction(ACTION_PREVIOUS);
        PendingIntent previousPendingIntent = PendingIntent.getService(this, 0, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent nextIntent = new Intent(this, MusicService.class);
        nextIntent.setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getService(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent openPlayerIntent = new Intent(this, MusicService.class);
        openPlayerIntent.setAction(ACTION_OPEN_PLAYER);
        openPlayerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent openPlayerPendingIntent = PendingIntent.getService(this, 0, openPlayerIntent,  PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

        @SuppressLint("NotificationTrampoline") Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle(song.getName())
                .setContentText(song.getSinger())
                .setSmallIcon(R.drawable.music)
                .addAction(R.drawable.backward_step_solid, "Previous", previousPendingIntent)
                .addAction(IsPlaying ? R.drawable.pause_solid : R.drawable.note_pause, "Play/Pause", playPausePendingIntent)
                .addAction(R.drawable.forward_step_solid, "Next", nextPendingIntent)
                .setContentIntent(openPlayerPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                .build();

        startForeground(123, notification);
    }

    public void playMusic(Song song) {
        try {
            // 从 Song 对象中获取音乐的路径
            this.song = song;
            addToRecentlyPlayed(song);
            String path = song.getPath();
            Uri uri = Uri.parse(path);
            // 重置音乐播放器
            if (player == null) {
                player = new MediaPlayer();
            } else {
                player.reset(); // 重置音乐播放器
            }
            // 加载多媒体文件
            player.setDataSource(getApplicationContext(), uri);
            player.prepare();
            player.start(); // 播放音乐
            addTimer(); // 添加计时器
            showNotification(song); // 显示通知
            player.setOnCompletionListener(mp -> playNextSong());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ChangeMode(String mode)
    {
        this.mode = mode;
    }
    public void playNextSong() {
        if (SongList == null || SongList.isEmpty()) {
            return;
        }
        switch (mode)
        {
            //顺序播放
            case "order":
            {
                int position = SongList.indexOf(song);
                if(position!=SongList.size()-1)
                {
                    playMusic(SongList.get(position+1));
                    this.song = SongList.get(position+1);
                }
                else
                {
                    this.song = SongList.get(0);
                    playMusic(SongList.get(0));

                }
                break;
            }
            //随机播放
            case "random":
            {
                Random random = new Random();
                int position = random.nextInt(SongList.size());
                playMusic(SongList.get(position));
                this.song = SongList.get(position);
                break;
            }
            //循环播放
            case "repeat":
            {
                playMusic(song);
            }
        }
        Intent intent = new Intent(ACTION_SONG_CHANGED);
        intent.putExtra("song", song);
        sendBroadcast(intent);
    }

    //收藏
    public AtomicBoolean toggleFavoriteAsync(Song song, Runnable callback) {
        AtomicBoolean result = new AtomicBoolean(false);
        database.songDao().countFavoriteAsync(song.getName(), song.getSinger(), count -> {
            if (count > 0) {
                database.songDao().deleteByNameAndSingerAsync(song.getName(),song.getSinger(), callback);
            } else {
                database.songDao().insertAsync(SongEntity.fromSong(song), callback);
                result.set(true);
            }
        });
              return  result;
    }



    // 异步检查是否收藏
    public boolean checkIfFavoriteAsync(Song song) {
        return database.songDao().countFavoriteAsync(song.getName(),song.getSinger())>0;
    }
    //添加歌曲到最近播放中
//    musicService.toggleFavoriteAsync(song,() -> runOnUiThread(()->updateFavoriteButtonState(isLove)));
    public void addToRecentlyPlayed(Song song) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            RecentlyPlayedDao recentlyPlayedDao = database.recentlyPlayedDao();
            recentlyPlayedDao.deleteByNameAndSingerAsync(song.getName(), song.getSinger(), () -> {
                // 在删除完成后执行剩下的步骤
                int count = recentlyPlayedDao.getCount();
                if (count >= 10) { // 假设我们只保存最近播放的10首歌
                    recentlyPlayedDao.deleteOldest();
                }
                recentlyPlayedDao.insert(RecentlyPlayedEntity.fromSong(song, System.currentTimeMillis()));
            });
        });
    }
    //获取上一首歌
    public void playPreviousSong() {
        RecentlyPlayedDao recentlyPlayedDao = database.recentlyPlayedDao();
        recentlyPlayedDao.getPreviousSongAsync(previousSong -> {
            if (previousSong != null) {
                playMusic(previousSong);
                Intent intent = new Intent(ACTION_SONG_CHANGED);
                intent.putExtra("song", previousSong);
                sendBroadcast(intent);
            }
        });
    }
    //添加计时器用于设置音乐播放器中的播放进度条
    public void addTimer(){
        //如果timer不存在，也就是没有引用实例
        if(timer==null){
            //创建计时器对象
            timer=new Timer();
            TimerTask task=new TimerTask() {
                @Override
                public void run() {
                    if (player==null) return;
                    int duration=player.getDuration();//获取歌曲总时长
                    int currentPosition=player.getCurrentPosition();//获取播放进度
                    Message msg= PlayerActivity.handler.obtainMessage();//创建消息对象
                    //将音乐的总时长和播放进度封装至bundle中
                    Bundle bundle=new Bundle();
                    bundle.putInt("duration",duration);
                    bundle.putInt("currentPosition",currentPosition);
                    //再将bundle封装到msg消息对象中
                    msg.setData(bundle);
                    //最后将消息发送到主线程的消息队列
                    PlayerActivity.handler.sendMessage(msg);
                }
            };
            //开始计时任务后的5毫秒，第一次执行task任务，以后每500毫秒（0.5s）执行一次
            timer.schedule(task,5,500);
        }
    }
    //Binder是一种跨进程的通信方式
     class MusicControl extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
        //下面的暂停继续和退出方法全部调用的是MediaPlayer自带的方法

        public void pausePlay() {
            player.pause();
            IsPlaying = false;
            showNotification(song);
        }

        public void continuePlay() {
            player.start();
            IsPlaying = true;
            showNotification(song);
        }

    }
    public void pausePlay() {
        player.pause();
        IsPlaying = false;//暂停播放音乐
        showNotification(song);
    }

    public void continuePlay() {
        player.start();
        IsPlaying = true;//继续播放音乐
        showNotification(song);
    }

    public void seekTo(int progress) {
        player.seekTo(progress);//设置音乐的播放位置
    }
    //销毁多媒体播放器
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(player==null) return;
        if(player.isPlaying()) player.stop();//停止播放音乐
        player.release();//释放占用的资源
        player=null;//将player置为空
    }
    public MusicService() {

    }
    public Song getCurrentSong()
    {
        return song;
    }
    public boolean IsPlaying()
    {
        return IsPlaying;
    }
    public  int getProgress()
    {
        return player.getCurrentPosition();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicControl();
    }
}