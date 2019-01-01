package com.smol.inz.pojednejnutce.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.smol.inz.pojednejnutce.R;


public class RankingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mUserNameText;
    public TextView mUserScoreText;

    public RankingViewHolder(View itemView){
        super(itemView);
        mUserNameText = itemView.findViewById(R.id.user_name_text);
        mUserScoreText = itemView.findViewById(R.id.user_score_text);

    }



    @Override
    public void onClick(View view) {

    }
}
