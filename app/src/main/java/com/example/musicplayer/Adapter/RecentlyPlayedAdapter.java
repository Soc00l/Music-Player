package com.example.musicplayer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.Entity.Song;
import com.example.musicplayer.PlayerActivity;
import com.example.musicplayer.R;

import java.util.List;

public class RecentlyPlayedAdapter extends RecyclerView.Adapter<RecentlyPlayedAdapter.ViewHolder> {
    private List<Song> songs;
    private Context context;

    public RecentlyPlayedAdapter(List<Song> songs, Context context) {
        this.songs = songs;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.nameTextView.setText(song.getName());
        holder.singerTextView.setText(song.getSinger());
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("song", song);
            intent.putExtra("class","RecentlyPlayedActivity");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView singerTextView;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.song_name);
            singerTextView = itemView.findViewById(R.id.song_singer);
        }
    }
}
