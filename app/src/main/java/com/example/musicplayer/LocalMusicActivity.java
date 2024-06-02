package com.example.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;



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
        FragmentList.add(SingleSongFragment.newInstance(Songlist));
        FragmentList.add(SingerFragment.newInstance(Songlist));
        FragmentList.add(AlbumFragment.newInstance(Songlist));
        FragmentList.add(FolderFragment.newInstance("",""));
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
    }
}
