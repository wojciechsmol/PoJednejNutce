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

public class GenresActivity extends AppCompatActivity {

    public static final String GENRE = "GENRE";
    public static final String POP = "POP";

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;
    private Button mPopButton;
    private TextView mUserPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);
        initialize();
    }

    private void initialize() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUserPoints = findViewById(R.id.user_points);
        mPopButton = findViewById(R.id.button_pop_genre);
        mPopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GenresActivity.this, LevelActivity.class);
                intent.putExtra(GENRE, POP);
                startActivity(intent);
            }
        });

        setListenerUserPoints();
    }

    private void  setListenerUserPoints() {
        //set value event listener for points text view

        mDatabaseReference.child("Users").child(mFirebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        UserPOJO mUserInfo = dataSnapshot.getValue(UserPOJO.class);
                        mUserPoints.setText(String.valueOf(mUserInfo.getPoints()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("HomeActivity: ", "POINTS UPDATE WENT WRONG!");
                    }
                });
    }
}
