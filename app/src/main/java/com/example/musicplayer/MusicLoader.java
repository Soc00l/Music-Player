package com.example.musicplayer;

import static com.example.musicplayer.SongSorter.sortSongsByName;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class MusicLoader {
    private Context context;
    private static final File PATH = Environment.getExternalStorageDirectory();
    public MusicLoader(Context context)
    {
        this.context = context;
    }

    public  ArrayList<Song> getMusic(){
        ArrayList<Song> songs = new ArrayList<>();
        Cursor cur = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Audio.Media.TITLE,   // 音频文件标题
                        MediaStore.Audio.Media.ARTIST,  // 音频文件艺术家
                        MediaStore.Audio.Media.ALBUM,   // 音频文件专辑
                        MediaStore.Audio.Media.DATA     // 音频文件路径
                },
                null, null, null);

        if (cur == null) {
            System.out.println("————查询失败！————");
        } else if (!cur.moveToFirst()) {
            System.out.println("————音频不存在！————");
            cur.close();
        } else {
            do {
                Song song = new Song();
                song.setName(cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                String artist = cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                if (artist == null || artist.equals("<unknown>")) {
                    String title = song.getName();
                    int dashIndex = title.lastIndexOf('-');

                    if (dashIndex != -1 && dashIndex < title.length() - 1) {
                        artist = title.substring(dashIndex + 1).trim();
                        String newTitle = title.substring(0, dashIndex).trim();
                        song.setName(newTitle);
                    } else {
                        artist = "未知歌手";
                    }
                }
                song.setSinger(artist);
                song.setAlbum(cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
                song.setPath(cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));

                songs.add(song);
            } while (cur.moveToNext());

            cur.close();
        }
        sortSongsByName(songs);
        return songs;
    }

//    public void loadMusic() {
//
//        @SuppressLint("SdCardPath") File musicPath = new File("/sdcard/Music");
//
//        // 检查目录是否存在和可读
//        if (!musicPath.exists() || !musicPath.isDirectory()) {
//            System.out.println("目录不存在或不可读: " + musicPath.getAbsolutePath());
//            return;
//        }
//
//        // 文件过滤器，筛选MP3文件
//        FilenameFilter mp3Filter = new FilenameFilter() {
//            @Override
//            public boolean accept(File dir, String name) {
//                return name.toLowerCase().endsWith(".mp3");
//            }
//        };
//
//        // 使用过滤器获取所有MP3文件
//        String[] fs = musicPath.list(mp3Filter);
//
//        // 检查文件列表是否为空
//        if (fs == null) {
//            System.out.println("无法读取目录内容或没有匹配的文件: " + musicPath.getAbsolutePath());
//            return;
//        }
//
//        // 输出MP3文件列表
//        for (String file : fs) {
//            try {
//                // 打印文件名和路径
//                System.out.println(new String(file.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
//            } catch (Exception e) {
//                System.out.println("文件名处理错误: " + file);
//            }
//        }
//    }
}
