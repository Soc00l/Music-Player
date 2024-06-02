package com.example.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private List<Song> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        list = intent.getParcelableArrayListExtra("songs");
        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
        Fragment SongFragment = SingleSongFragment.newInstance((ArrayList<Song>) list);
        fragmentArrayList.add(SongFragment);
        //返回按钮
        ImageButton back = findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(DetailActivity.this,LocalMusicActivity.class);
                startActivity(intent);
            }
        });
        //显示专辑/歌手名
        TextView textView = findViewById(R.id.tv_album_name);
        textView.setText(intent.getStringExtra("title"));
        ViewPager viewPager = findViewById(R.id.detail_view_pager);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(),fragmentArrayList));
    }
}