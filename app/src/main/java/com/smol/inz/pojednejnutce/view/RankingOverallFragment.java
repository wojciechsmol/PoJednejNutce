package com.smol.inz.pojednejnutce.view;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.smol.inz.pojednejnutce.R;
import com.smol.inz.pojednejnutce.model.UserPOJO;
import com.smol.inz.pojednejnutce.viewHolder.RankingViewHolder;

public class RankingOverallFragment extends Fragment {

    private RecyclerView mRankingOverallList;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerOptions<UserPOJO> options;
    private FirebaseRecyclerAdapter<UserPOJO, RankingViewHolder> adapter;
    View myFragment;

    public static RankingOverallFragment newInstance() {
        RankingOverallFragment rankingOverallFragment = new RankingOverallFragment();
        return rankingOverallFragment;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_ranking_overall, container, false);
        initialize();
        return myFragment;
    }

    private void initialize() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mRankingOverallList = myFragment.findViewById(R.id.ranking_list);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mLayoutManager.setReverseLayout(true);
        mRankingOverallList.setHasFixedSize(true);
        mRankingOverallList.setLayoutManager(mLayoutManager);

        Query query = mDatabaseReference.child("Users").orderByChild("score");

        options = new FirebaseRecyclerOptions.Builder<UserPOJO>()
                .setIndexedQuery(query, mDatabaseReference.child("Users"), UserPOJO.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<UserPOJO, RankingViewHolder>(options) {
            @Override
            protected void onBindViewHolder(RankingViewHolder holder, int position, UserPOJO model) {

                holder.mUserNameText.setText(model.getName());
                holder.mUserScoreText.setText(String.valueOf(model.getScore()));

                if (model.getName().equals(mFirebaseAuth.getCurrentUser().getEmail())) {
                    Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);

                    holder.mUserNameText.setTypeface(boldTypeface);
                    holder.mUserScoreText.setTypeface(boldTypeface);
                }

            }

            @Override
            public RankingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_ranking_overall, parent, false);

                return new RankingViewHolder(view);
            }
        };

        adapter.notifyDataSetChanged();
        mRankingOverallList.setAdapter(adapter);


    }
}
