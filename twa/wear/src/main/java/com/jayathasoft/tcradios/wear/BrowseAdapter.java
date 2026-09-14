package com.jayathasoft.tcradios.wear;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

class BrowseAdapter extends RecyclerView.Adapter<BrowseAdapter.BrowseViewHolder> {
    interface OnItemClickListener {
        void onItemClick(MediaBrowserCompat.MediaItem item);
    }

    private final List<MediaBrowserCompat.MediaItem> items = new ArrayList<>();
    private final OnItemClickListener listener;
    private final boolean showChevrons;

    BrowseAdapter(OnItemClickListener listener, boolean showChevrons) {
        this.listener = listener;
        this.showChevrons = showChevrons;
    }

    void setItems(List<MediaBrowserCompat.MediaItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BrowseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_browse, parent, false);
        return new BrowseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrowseViewHolder holder, int position) {
        MediaBrowserCompat.MediaItem item = items.get(position);
        MediaDescriptionCompat description = item.getDescription();
        String title = description.getTitle() != null ? description.getTitle().toString() : "";
        String subtitle = description.getSubtitle() != null ? description.getSubtitle().toString() : "";

        holder.titleView.setText(title);
        if (subtitle.isEmpty()) {
            holder.subtitleView.setVisibility(View.GONE);
        } else {
            holder.subtitleView.setVisibility(View.VISIBLE);
            holder.subtitleView.setText(subtitle);
        }

        if (showChevrons) {
            FaIconHelper.setIcon(holder.chevronView, '\uf054', 11f);
            holder.chevronView.setVisibility(View.VISIBLE);
            holder.iconView.setImageResource(R.drawable.ic_folder);
            holder.iconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.iconView.setPadding(6, 6, 6, 6);
            CircleImageHelper.apply(holder.iconView);
        } else {
            holder.chevronView.setVisibility(View.GONE);
            holder.iconView.setPadding(2, 2, 2, 2);
            holder.iconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            CircleImageHelper.apply(holder.iconView);
            ArtworkLoader.load(holder.iconView, description.getIconUri(), R.drawable.ic_radio);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class BrowseViewHolder extends RecyclerView.ViewHolder {
        final ImageView iconView;
        final TextView titleView;
        final TextView subtitleView;
        final TextView chevronView;

        BrowseViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.item_icon);
            titleView = itemView.findViewById(R.id.item_title);
            subtitleView = itemView.findViewById(R.id.item_subtitle);
            chevronView = itemView.findViewById(R.id.item_chevron);
        }
    }
}
