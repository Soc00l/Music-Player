package com.example.musicplayer;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter <RecyclerAdapter.RecyclerViewHolder>{
    private List<Song> songs;

    public RecyclerAdapter(List<Song> songs)
    {
        this.songs = songs;
    }
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
            holder.BindData(songs.get(position));
            holder.itemview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickedPosition = holder.getAdapterPosition();

                    if (clickedPosition != RecyclerView.NO_POSITION) {
                        // Obtain the Song object based on the clicked position
                        Song song = songs.get(clickedPosition);

                        // Start the PlayerActivity and pass the Song object
                        Intent intent = new Intent(holder.itemview.getContext(), PlayerActivity.class);
                        intent.putExtra("song", song);
                        intent.putExtra("class","LocalMusicActivity");
                        holder.itemview.getContext().startActivity(intent);
                    }
                }
            });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        View itemview;
        TextView SongName;
        TextView Singer;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemview = itemView;
            SongName = itemView.findViewById(R.id.name);
            Singer = itemView.findViewById(R.id.singer);
        }
        void BindData(Song song)
        {
            SongName.setText(song.getName());
            Singer.setText(song.getSinger());
        }
    }
}
