package com.example.musicplayer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.musicplayer.AppDatabase;
import com.example.musicplayer.DetailActivity;
import com.example.musicplayer.Entity.FolderSong;
import com.example.musicplayer.Entity.Song;
import com.example.musicplayer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    public Map<String, List<Song>> folderMap;
    private Context context;
    public List<String> folderNames;

    private AppDatabase appDatabase;

    public FolderAdapter(Map<String, List<Song>> folderMap, Context context) {
        this.folderMap = folderMap;
        this.context = context;
        this.folderNames = new ArrayList<>(folderMap.keySet());
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_folder, parent, false);
        appDatabase =  Room.databaseBuilder(context, AppDatabase.class, "music-player-db").build();
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        String folderName = folderNames.get(position);
        List<Song> Songs = folderMap.get(folderName);
        holder.tvFolderName.setText(folderName);
        holder.tvSongCount.setText(String.format("%d songs", Songs.size()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("title", folderName);
                intent.putExtra("tab",3);
                intent.putParcelableArrayListExtra("songs", new ArrayList<>(Songs));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return folderMap.size();
    }

    public static class FolderViewHolder extends RecyclerView.ViewHolder {
        TextView tvFolderName;
        TextView tvSongCount;

        public FolderViewHolder(View itemView) {
            super(itemView);
            tvFolderName = itemView.findViewById(R.id.tv_folder_name);
            tvSongCount = itemView.findViewById(R.id.tv_folder_count);
        }
    }
    public void removeItem(int position) {
        String folderName = folderNames.get(position);
        appDatabase.folderSongDao().deleteFolderAsync(folderName);
        // 在这里执行删除操作和通知适配器更新
        folderMap.remove(folderName);
        folderNames.remove(position);
        notifyItemRemoved(position);
    }

}