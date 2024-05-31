package com.example.musicplayer;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class SongSorter {

    public  static  void sortSongsByName(ArrayList<Song> songs) {
        Collator collator = Collator.getInstance(Locale.CHINA); // 可以根据需要选择语言环境

        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2) {
                return collator.compare(song1.getName(), song2.getName());
            }
        });
    }
}