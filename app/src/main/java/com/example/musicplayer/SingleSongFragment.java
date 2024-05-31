package com.example.musicplayer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleSongFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleSongFragment extends Fragment {


    private  ArrayList<Song> list ;

    // TODO: Rename and change types of parameters
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
        return view;
    }
}