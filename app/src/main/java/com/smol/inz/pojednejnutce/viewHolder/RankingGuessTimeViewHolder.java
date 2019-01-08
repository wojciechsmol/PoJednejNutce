package com.smol.inz.pojednejnutce.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.smol.inz.pojednejnutce.R;

import org.w3c.dom.Text;

public class RankingGuessTimeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mUserNameText;
    public TextView mGuessTimeText;

    public RankingGuessTimeViewHolder(View itemView) {
        super(itemView);
        mUserNameText = itemView.findViewById(R.id.user_name_text);
        mGuessTimeText = itemView.findViewById(R.id.guess_time_text);
    }

    @Override
    public void onClick(View view) {

        //TODO This IS NOT NECESSARY HERE!!

    }
}
