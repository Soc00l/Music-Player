package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SingerAdapter extends RecyclerView.Adapter<SingerAdapter.SingerViewHolder> {

    private Map<String, List<Song>> singerMap;
    private Context context;
    private List<String> singerNames;

    public SingerAdapter(Map<String, List<Song>> singerMap, Context context) {
        this.singerMap = singerMap;
        this.context = context;
        this.singerNames = new ArrayList<>(singerMap.keySet());
    }

    @NonNull
    @Override
    public SingerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_singer, parent, false);
        return new SingerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingerViewHolder holder, int position) {
        String singerName = singerNames.get(position);
        List<Song> songs = singerMap.get(singerName);
        holder.tvSingerName.setText(singerName);
        holder.tvSongCount.setText(String.format("%d songs", songs.size()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("title", singerName);
                intent.putParcelableArrayListExtra("songs", new ArrayList<>(songs));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return singerMap.size();
    }

    public static class SingerViewHolder extends RecyclerView.ViewHolder {
        TextView tvSingerName;
        TextView tvSongCount;

        public SingerViewHolder(View itemView) {
            super(itemView);
            tvSingerName = itemView.findViewById(R.id.tv_singer_name);
            tvSongCount = itemView.findViewById(R.id.tv_song_count);
        }
    }
}
