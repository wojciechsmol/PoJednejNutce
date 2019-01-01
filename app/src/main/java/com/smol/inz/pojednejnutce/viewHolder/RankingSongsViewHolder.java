package com.smol.inz.pojednejnutce.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.smol.inz.pojednejnutce.R;
import com.smol.inz.pojednejnutce.utils.ItemClickListener;

public class RankingSongsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mSongTitle;

    private ItemClickListener itemClickListener;

    public RankingSongsViewHolder(View itemView) {
        super(itemView);
        mSongTitle = itemView.findViewById(R.id.song_title_text);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
