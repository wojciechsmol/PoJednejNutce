package com.smol.inz.pojednejnutce.view;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
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
import com.smol.inz.pojednejnutce.viewHolder.RankingOverallViewHolder;

public class RankingActivity extends AppCompatActivity {

    private RecyclerView mRankingOverallList;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerOptions<UserPOJO> options;
    private FirebaseRecyclerAdapter<UserPOJO, RankingOverallViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void initialize() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mRankingOverallList = findViewById(R.id.ranking_list);
        mLayoutManager = new LinearLayoutManager(this);

        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRankingOverallList.setHasFixedSize(true);
        mRankingOverallList.setLayoutManager(mLayoutManager);

        Query query = mDatabaseReference.child("Users").orderByChild("points");

        options = new FirebaseRecyclerOptions.Builder<UserPOJO>()
                .setIndexedQuery(query, mDatabaseReference.child("Users"), UserPOJO.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<UserPOJO, RankingOverallViewHolder>(options) {
            @Override
            protected void onBindViewHolder(RankingOverallViewHolder holder, int position, UserPOJO model) {

                holder.mUserNameText.setText(model.getName());
                holder.mUserScoreText.setText(String.valueOf(model.getPoints()));

                if (model.getName().equals(mFirebaseAuth.getCurrentUser().getEmail())) {
                    Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);

                    holder.mUserNameText.setTypeface(boldTypeface);
                    holder.mUserScoreText.setTypeface(boldTypeface);
                }

            }

            @Override
            public RankingOverallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_ranking_overall, parent, false);

                return new RankingOverallViewHolder(view);
            }
        };

        adapter.notifyDataSetChanged();
        mRankingOverallList.setAdapter(adapter);

    }
}
