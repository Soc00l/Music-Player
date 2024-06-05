package com.example.musicplayer.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.R;
import com.example.musicplayer.Adapter.RecyclerAdapter;
import com.example.musicplayer.Entity.Song;

import java.util.ArrayList;

public class SingleSongFragment extends Fragment {
    private  ArrayList<Song> list ;
    private RecyclerView recyclerView;
    public SingleSongFragment() {

    }
    public static SingleSongFragment newInstance(ArrayList<Song> list) {
        SingleSongFragment fragment = new SingleSongFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("Songlist",  list);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
         this.list = getArguments().getParcelableArrayList("Songlist");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_single_song, container, false);
        recyclerView = view.findViewById(R.id.songRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(new RecyclerAdapter(list));
        view.setOnApplyWindowInsetsListener((v, insets) -> {
            int bottomInset = insets.getSystemWindowInsetBottom();
            recyclerView.setPadding(0, 0, 0, bottomInset);
            return insets;
        });
        return view;
    }
}