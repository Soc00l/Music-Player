package com.example.musicplayer.Fragment;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.Adapter.FolderAdapter;
import com.example.musicplayer.Entity.FolderSong;
import com.example.musicplayer.Entity.Song;
import com.example.musicplayer.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FolderFragment extends Fragment {
    private RecyclerView recyclerView;
    private FolderAdapter folderAdapter;
    private Map<String, List<Song>> folderMap;
    private List<FolderSong> folderSongs;
    private static final String ARG_FOLDER = "foldermap";

    public FolderFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            folderSongs = getArguments().getParcelableArrayList(ARG_FOLDER);
        }



    }
    public static FolderFragment newInstance(List<FolderSong> folderSongs) {
        FolderFragment fragment = new FolderFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_FOLDER, new ArrayList<>(folderSongs));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);
        recyclerView = view.findViewById(R.id.FolderRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        folderMap = groupSongsByFolder(folderSongs);
        folderAdapter = new FolderAdapter(folderMap, getContext());
        recyclerView.setAdapter(folderAdapter);
        // 创建ItemTouchHelper并将其附加到RecyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }


            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                folderAdapter.removeItem(position);
            }


        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }
    private Map<String, List<Song>> groupSongsByFolder(List<FolderSong> folderSongs) {
        Map<String, List<Song>> folderMap = new HashMap<>();

        for (FolderSong folderSong : folderSongs) {
            Song song = new Song();
            song.setName(folderSong.getSongName());
            song.setSinger(folderSong.getArtist());
            song.setAlbum(folderSong.getAlbum());
            song.setPath(folderSong.getPath());

            if (!folderMap.containsKey(folderSong.getFolderName())) {
                folderMap.put(folderSong.getFolderName(), new ArrayList<>());
            }
            folderMap.get(folderSong.getFolderName()).add(song);
        }

        return folderMap;
    }
}