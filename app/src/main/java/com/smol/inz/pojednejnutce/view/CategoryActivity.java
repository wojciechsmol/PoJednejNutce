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

public class CategoryActivity extends AppCompatActivity {

    public static final String CATEGORY = "CATEGORY";
    public static final String POP = "POP";
    public static final String ROCK = "ROCK";

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;
    private Button mPopButton;
    private Button mRockButton;
    private TextView mUserScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        initialize();
    }

    private void initialize() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUserScore = findViewById(R.id.user_score);
        mPopButton = findViewById(R.id.button_pop_category);
        mPopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryActivity.this, LevelActivity.class);
                intent.putExtra(CATEGORY, POP);
                startActivity(intent);
            }
        });
        mRockButton = findViewById(R.id.button_rock_category);
        mRockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryActivity.this, LevelActivity.class);
                intent.putExtra(CATEGORY, ROCK);
                startActivity(intent);
            }
        });

        setListenerUserScore();
    }

    private void setListenerUserScore() {
        //set value event listener for score text view

        mDatabaseReference.child("Users").child(mFirebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        UserPOJO mUserInfo = dataSnapshot.getValue(UserPOJO.class);
                        mUserScore.setText(String.valueOf(mUserInfo.getScore()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("HomeActivity: ", "SCORE UPDATE WENT WRONG!");
                    }
                });
    }
}
