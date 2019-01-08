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
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.smol.inz.pojednejnutce.R;
import com.smol.inz.pojednejnutce.model.UserPOJO;
import com.smol.inz.pojednejnutce.viewHolder.RankingViewHolder;


public class RankingSpecificCategoryFragment extends Fragment {

    private RecyclerView mRankingCategoryList;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerOptions<UserPOJO> options;
    private FirebaseRecyclerAdapter<UserPOJO, RankingViewHolder> adapter;
    private View myFragment;
    private String mCategory;
    private TextView mCategoryTitle;

    public static RankingSpecificCategoryFragment newInstance() {
        RankingSpecificCategoryFragment rankingSpecificCategoryFragment = new RankingSpecificCategoryFragment();
        return rankingSpecificCategoryFragment;
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
        myFragment = inflater.inflate(R.layout.fragment_specific_category, container, false);
        initialize();
        return myFragment;
    }

    private void initialize() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mRankingCategoryList = myFragment.findViewById(R.id.ranking_list);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mCategoryTitle = myFragment.findViewById(R.id.category_title_text);


        mLayoutManager.setReverseLayout(true);
        mRankingCategoryList.setHasFixedSize(true);
        mRankingCategoryList.setLayoutManager(mLayoutManager);

        mCategory = getArguments().getString("Category");

        mCategoryTitle = myFragment.findViewById(R.id.category_title_text);
        mCategoryTitle.setText(mCategory);

        Query query = mDatabaseReference.child("Users").orderByChild("score" + mCategory);

        options = new FirebaseRecyclerOptions.Builder<UserPOJO>()
                .setIndexedQuery(query, mDatabaseReference.child("Users"), UserPOJO.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<UserPOJO, RankingViewHolder>(options) {
            @Override
            protected void onBindViewHolder(RankingViewHolder holder, int position, UserPOJO model) {

                holder.mUserNameText.setText(model.getName());
                switch (mCategory) {
                    case "POP":
                        holder.mUserScoreText.setText(String.valueOf(model.getScorePOP()));
                        break;
                    case "ROCK":
                        holder.mUserScoreText.setText(String.valueOf(model.getScoreROCK()));
                }

                if (model.getName().equals(mFirebaseAuth.getCurrentUser().getEmail())) {
                    Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);

                    holder.mUserNameText.setTypeface(boldTypeface);
                    holder.mUserScoreText.setTypeface(boldTypeface);
                }

                mRankingCategoryList.smoothScrollToPosition(adapter.getItemCount());

            }

            @Override
            public RankingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_ranking_overall, parent, false);

                return new RankingViewHolder(view);
            }
        };

        adapter.notifyDataSetChanged();
        mRankingCategoryList.setAdapter(adapter);

    }

}
