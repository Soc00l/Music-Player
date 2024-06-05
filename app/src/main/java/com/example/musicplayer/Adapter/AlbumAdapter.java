package com.example.musicplayer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.DetailActivity;
import com.example.musicplayer.Entity.Song;
import com.example.musicplayer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private Map<String, List<Song>> albumMap;
    private Context context;
    private List<String> albumNames;
    public AlbumAdapter(Map<String, List<Song>> albumMap, Context context) {
        this.albumMap = albumMap;
        this.context = context;
        this.albumNames = new ArrayList<>(albumMap.keySet());
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        String albumName = albumNames.get(position);
        List<Song> songs = albumMap.get(albumName);
        holder.tvAlbumName.setText(albumName);
        holder.tvSongCount.setText(String.format("%d songs", songs.size()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("title", albumName);
                intent.putExtra("tab",2);
                intent.putParcelableArrayListExtra("songs", new ArrayList<>(songs));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumMap.size();
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {
        TextView tvAlbumName;
        TextView tvSongCount;
        public AlbumViewHolder(View itemView) {
            super(itemView);
            tvAlbumName = itemView.findViewById(R.id.tv_album_name);
            tvSongCount = itemView.findViewById(R.id.tv_song_count);
        }
    }
}
