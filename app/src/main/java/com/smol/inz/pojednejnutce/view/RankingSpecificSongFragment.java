package com.smol.inz.pojednejnutce.view;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.smol.inz.pojednejnutce.R;
import com.smol.inz.pojednejnutce.model.GuessedTimeUserPOJO;
import com.smol.inz.pojednejnutce.viewHolder.RankingGuessTimeViewHolder;
import com.smol.inz.pojednejnutce.viewHolder.RankingViewHolder;

import java.util.concurrent.TimeUnit;

public class RankingSpecificSongFragment extends Fragment {

    private RecyclerView mSongGuessTimeList;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerOptions<GuessedTimeUserPOJO> options;
    private FirebaseRecyclerAdapter<GuessedTimeUserPOJO, RankingGuessTimeViewHolder> adapter;
    private String mSongId;
    private TextView mSongTitleText;
    View myFragment;

    public static RankingSpecificSongFragment newInstance() {
        RankingSpecificSongFragment rankingSpecificSongFragment = new RankingSpecificSongFragment();
        return rankingSpecificSongFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_ranking_specific_song, container, false);
        initialize();
        return myFragment;
    }

    private void initialize() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mSongGuessTimeList = myFragment.findViewById(R.id.guess_time_list);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mSongGuessTimeList.setHasFixedSize(true);
        mSongGuessTimeList.setLayoutManager(mLayoutManager);

        String songTitle = getArguments().getString("SongTitle");

        mSongTitleText = myFragment.findViewById(R.id.song_title_text);





        mSongTitleText.setText(songTitle);

        setSongIdFromTitle(songTitle);

        DatabaseReference currentSongReference = mDatabaseReference.child("SongGuessedTime").child(mSongId);

        Query query = currentSongReference.orderByChild("guessedTime");

        options = new FirebaseRecyclerOptions.Builder<GuessedTimeUserPOJO>()
                .setIndexedQuery(query, currentSongReference, GuessedTimeUserPOJO.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<GuessedTimeUserPOJO, RankingGuessTimeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(RankingGuessTimeViewHolder holder, int position, GuessedTimeUserPOJO model) {

                holder.mUserNameText.setText(model.getName());

                holder.mGuessTimeText.setText(convertMilisecondsToReadableFormat(model.getGussedTime()));

                if (model.getName().equals(mFirebaseAuth.getCurrentUser().getEmail())) {
                    Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);

                    holder.mUserNameText.setTypeface(boldTypeface);
                    holder.mGuessTimeText.setTypeface(boldTypeface);
                }

                mSongGuessTimeList.smoothScrollToPosition(adapter.getItemCount());

            }

            @Override
            public RankingGuessTimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_ranking_guess_time, parent, false);

                return new RankingGuessTimeViewHolder(view);
            }
        };

        adapter.notifyDataSetChanged();
        mSongGuessTimeList.setAdapter(adapter);
    }

    private void setSongIdFromTitle(String songTitle) {
        songTitle = songTitle.replaceAll(" ", "_").toLowerCase();

        mSongId = StringUtils.stripAccents(songTitle);
    }

    private String convertMilisecondsToReadableFormat(long milliseconds) {

        long millis = milliseconds % 1000;
        long seconds = (milliseconds / 1000);
        String ssmmm = String.format("%02d:%02d", seconds, millis);

        return ssmmm;
    }
}
