package com.example.musicplayer;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingerFragment extends Fragment {

    private RecyclerView recyclerView;
    private SingerAdapter singerAdapter;
    private Map<String, List<Song>> singerMap;
    private List<Song> songs;

    private static final String ARG_SONGLIST = "songlist";

    public SingerFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            songs = getArguments().getParcelableArrayList(ARG_SONGLIST);
        }
    }

    public static SingerFragment newInstance(ArrayList<Song> songList) {
        SingerFragment fragment = new SingerFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_SONGLIST, songList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_singer, container, false);
        recyclerView = view.findViewById(R.id.SingerRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        singerMap = groupSongsBySinger(songs);
        singerAdapter = new SingerAdapter(singerMap, getContext());
        recyclerView.setAdapter(singerAdapter);

        return view;
    }

    private Map<String, List<Song>> groupSongsBySinger(List<Song> songs) {
        Map<String, List<Song>> singerMap = new HashMap<>();
        for (Song song : songs) {
            if (!singerMap.containsKey(song.getSinger())) {
                singerMap.put(song.getSinger(), new ArrayList<>());
            }
            singerMap.get(song.getSinger()).add(song);
        }
        return singerMap;
    }
}


