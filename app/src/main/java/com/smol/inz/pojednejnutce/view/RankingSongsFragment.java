package com.smol.inz.pojednejnutce.view;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.smol.inz.pojednejnutce.R;
import com.smol.inz.pojednejnutce.model.SongSimplePOJO;
import com.smol.inz.pojednejnutce.model.UserPOJO;
import com.smol.inz.pojednejnutce.utils.ItemClickListener;
import com.smol.inz.pojednejnutce.viewHolder.RankingSongsViewHolder;
import com.smol.inz.pojednejnutce.viewHolder.RankingViewHolder;

public class RankingSongsFragment extends Fragment {

    private RecyclerView mSongsList;
    private LinearLayoutManager mLayoutManager;
    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerOptions<SongSimplePOJO> options;
    private FirebaseRecyclerAdapter<SongSimplePOJO, RankingSongsViewHolder> adapter;
    View myFragment;

    public static RankingSongsFragment newInstance() {
        RankingSongsFragment rankingSongsFragment = new RankingSongsFragment();
        return rankingSongsFragment;
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
        myFragment = inflater.inflate(R.layout.fragment_ranking_songs, container, false);
        initialize();
        return myFragment;
    }

    private void initialize() {

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mSongsList = myFragment.findViewById(R.id.song_list);
        mLayoutManager = new LinearLayoutManager(getActivity());


        mSongsList.setHasFixedSize(true);
        mSongsList.setLayoutManager(mLayoutManager);

        Query query = mDatabaseReference.child("SongList").orderByChild("title");

        options = new FirebaseRecyclerOptions.Builder<SongSimplePOJO>()
                .setIndexedQuery(query, mDatabaseReference.child("SongList"), SongSimplePOJO.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<SongSimplePOJO, RankingSongsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(RankingSongsViewHolder holder, int position, final SongSimplePOJO model) {

                holder.mSongTitle.setText(model.getTitle());
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongCLick) {
                        Toast.makeText(getActivity(), "SONG: " + model.getTitle(), Toast.LENGTH_SHORT);
                    }
                });



            }

            @Override
            public RankingSongsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_ranking_song_list, parent, false);

                return new RankingSongsViewHolder(view);
            }
        };

        adapter.notifyDataSetChanged();
        mSongsList.setAdapter(adapter);


    }

}
