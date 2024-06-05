package com.example.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.musicplayer.Adapter.MyFragmentPagerAdapter;
import com.example.musicplayer.Entity.FolderSong;
import com.example.musicplayer.Entity.Song;
import com.example.musicplayer.Fragment.AlbumFragment;
import com.example.musicplayer.Fragment.FolderFragment;
import com.example.musicplayer.Fragment.SingerFragment;
import com.example.musicplayer.Fragment.SingleSongFragment;
import com.example.musicplayer.Util.MusicLoader;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class LocalMusicActivity extends AppCompatActivity {

    private ArrayList<Fragment> FragmentList = new ArrayList<Fragment>() ;
    private MusicLoader musicLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.localmusic);
//        添加ViewPager内容
        ArrayList<Song> Songlist = new ArrayList<>();
        musicLoader = new MusicLoader(this.getApplicationContext());
        Songlist = musicLoader.getMusic();
        List<FolderSong> FolderSongs = musicLoader.getAllFoldersWithSongs();
        FragmentList.add(SingleSongFragment.newInstance(Songlist));
        FragmentList.add(SingerFragment.newInstance(Songlist));
        FragmentList.add(AlbumFragment.newInstance(Songlist));
        FragmentList.add(FolderFragment.newInstance(FolderSongs));
        //返回按钮
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LocalMusicActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(),FragmentList));
        tabLayout.setupWithViewPager(viewPager);
        Intent intent = getIntent();
        if(intent!=null)
        {
            if (intent.hasExtra("tab"))
            {
                tabLayout.getTabAt(intent.getIntExtra("tab",0)).select();
            }
        }

    }
}
