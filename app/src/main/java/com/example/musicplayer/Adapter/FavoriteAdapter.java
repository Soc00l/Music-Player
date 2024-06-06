package com.example.musicplayer.Adapter;
// FavoriteAdapter.java
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

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {
    private List<Song> favoriteSongs;
    private Context context;

    public FavoriteAdapter(List<Song> favoriteSongs, Context context) {
        this.favoriteSongs = favoriteSongs;
        this.context = context;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_song, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Song song = favoriteSongs.get(position);
        holder.songTitle.setText(song.getName());
        holder.songSinger.setText(song.getSinger());
        holder.favorite_number.setText(String.valueOf(position+1));
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("song", song);
            intent.putExtra("class","FavoriteActivity");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return favoriteSongs.size();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle;
        TextView songSinger;
        TextView favorite_number;
        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            favorite_number = itemView.findViewById(R.id.favorite_number);
            songTitle = itemView.findViewById(R.id.songTitle);
            songSinger = itemView.findViewById(R.id.songSinger);
        }
    }
}
