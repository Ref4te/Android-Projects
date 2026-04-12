package com.service.playservice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    private List<Track> tracks;
    private OnItemClickListener listener;
    private int currentTrackIndex = -1;
    private boolean isPlaying = false;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public TrackAdapter(List<Track> tracks, OnItemClickListener listener) {
        this.tracks = tracks;
        this.listener = listener;
    }

    public void setCurrentTrackIndex(int index, boolean playing) {
        this.currentTrackIndex = index;
        this.isPlaying = playing;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_item, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        Track track = tracks.get(position);
        holder.tvTitle.setText(track.getTitle());
        
        if (position == currentTrackIndex) {
            holder.ivIcon.setImageResource(isPlaying ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
            holder.tvTitle.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_blue_dark));
        } else {
            holder.ivIcon.setImageResource(android.R.drawable.ic_media_play);
            holder.tvTitle.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.black));
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    static class TrackViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView ivIcon;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_track_title);
            ivIcon = itemView.findViewById(R.id.iv_music_icon);
        }
    }
}