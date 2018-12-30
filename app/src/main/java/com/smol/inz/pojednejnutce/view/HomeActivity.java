package com.smol.inz.pojednejnutce.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smol.inz.pojednejnutce.R;
import com.smol.inz.pojednejnutce.model.UserPOJO;


public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;
    private Button mPlayButton;
    private Button mLogoutButton;
    private Button mRankingButton;
    private TextView mUserPoints;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initialize();
    }

    private void initialize() {

        //TODO
        //TODO Mozna dac oczekiwanie az sie wszystko wczyta
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        setListenerUserPoints();

        mUserPoints = findViewById(R.id.user_points);
        mUserPoints.setVisibility(View.INVISIBLE);
        mLogoutButton = findViewById(R.id.button_logout);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth.signOut();
                finish();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });
        mPlayButton = findViewById(R.id.button_play);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(HomeActivity.this, GenresActivity.class));
            }
        });

        mRankingButton = findViewById(R.id.button_ranking);
        mRankingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(HomeActivity.this, RankingActivity.class));
            }
        });

    }

    private void setListenerUserPoints() {
        //set value event listener for points text view
        mDatabaseReference.child("Users").child(mFirebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserPOJO mUserInfo = dataSnapshot.getValue(UserPOJO.class);
                        mUserPoints.setText(String.valueOf(mUserInfo.getPoints()));
                        mUserPoints.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("HomeActivity: ", "POINTS UPDATE WENT WRONG!");
                    }
                });
    }


}
