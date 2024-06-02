package com.example.musicplayer;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AlbumFragment extends Fragment {

    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;
    private Map<String, List<Song>> albumMap;
    private List<Song> songs;

    private static final String ARG_SONGLIST = "songlist";
    public AlbumFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            songs = getArguments().getParcelableArrayList(ARG_SONGLIST);
        }
    }

    public static AlbumFragment newInstance(ArrayList<Song> songList) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_SONGLIST, songList);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = view.findViewById(R.id.AlbumRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        albumMap = groupSongsByAlbum(songs);
        albumAdapter = new AlbumAdapter(albumMap, getContext());
        recyclerView.setAdapter(albumAdapter);

        return view;
    }

    private Map<String, List<Song>> groupSongsByAlbum(List<Song> songs) {
        Map<String, List<Song>> albumMap = new HashMap<>();
        for (Song song : songs) {
            if (!albumMap.containsKey(song.getAlbum())) {
                albumMap.put(song.getAlbum(), new ArrayList<>());
            }
            albumMap.get(song.getAlbum()).add(song);
        }
        return albumMap;
    }
}

