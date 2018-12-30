package com.smol.inz.pojednejnutce.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smol.inz.pojednejnutce.Game;
import com.smol.inz.pojednejnutce.R;
import com.smol.inz.pojednejnutce.model.SongPOJO;
import com.smol.inz.pojednejnutce.model.UserGenreGussedSongsPOJO;
import com.smol.inz.pojednejnutce.model.UserPOJO;
import com.smol.inz.pojednejnutce.utils.Genre;
import com.smol.inz.pojednejnutce.utils.Level;

import java.util.ArrayList;
import java.util.List;

public class LevelActivity extends AppCompatActivity {

    public static final String LEVEL = "LEVEL";

    public static final int POP_AVAILABLE_ALL = 45;
    public static final int POP_AMATEUR_AVAILABLE_SONGS = 22;
    public static final int POP_SHOWER_SINGER_AVAILABLE_SONGS = 23;


    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    private Button mAmateurButton;
    private Button mShowerSingerButton;
    private TextView mGenreTitleText;
    private Genre mGenre;
    private TextView mUserPoints;
    private TextView mGuessedOverallText;
    private TextView mGuessedAmateurText;
    private TextView mGuessedShowerSingerText;
    private ImageView lockAmateur;
    private ImageView lockShowerSinger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        initialize();
    }


    private void initialize() {
        setGenre();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUserPoints = findViewById(R.id.user_points);
        mAmateurButton = findViewById(R.id.button_amateur);
        mAmateurButton.setEnabled(false);
        mAmateurButton.setAlpha(.3f);;
        mAmateurButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCategory(Level.AMATEUR);
            }
        });

        mShowerSingerButton = findViewById(R.id.button_shower_singer);
        mShowerSingerButton.setEnabled(false);
        mShowerSingerButton.setAlpha(.3f);
        mShowerSingerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCategory(Level.SHOWER_SINGER);
            }
        });

        mGenreTitleText = findViewById(R.id.genre_title_text);
        mGenreTitleText.setText(getmGenre().toString());
        mGuessedOverallText = findViewById(R.id.guessed_overall_text);
        mGuessedAmateurText = findViewById(R.id.guessed_amateur_text);
        mGuessedShowerSingerText = findViewById(R.id.guessed_shower_singer_text);
        lockAmateur = findViewById(R.id.lock_image_amateur);
        lockShowerSinger = findViewById(R.id.lock_image_shower_singer);
        getGenreUserGuessedSongsCount();
        setListenerUserPoints();


    }


    private void setListenerUserPoints() {
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


    private void setGenre() {
        Intent intent = getIntent();

        switch (intent.getStringExtra(GenresActivity.GENRE)) {
            case "POP":
                setmGenre(Genre.POP);
                break;
        }
    }

    public Genre getmGenre() {
        return mGenre;
    }

    public void setmGenre(Genre mGenre) {
        this.mGenre = mGenre;
    }

    private void onClickCategory(final Level level) {

        Task<Void> allTask;

        final Task<List<SongPOJO>> getSongsTask = getSongsForCurrentGenreAndLevelTask(mGenre, level);
        final Task<List<String>> getGuessedSongsTask = getGuessedSongsTask(getmGenre(), level);

        allTask = Tasks.whenAll(getSongsTask, getGuessedSongsTask);
        allTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                PlayActivity.game = new Game.Builder()
                        .setLevel(level)
                        .setGenre(getmGenre())
                        .setAvailableSongs(getSongsTask.getResult())
                        .setGuessedSongs(getGuessedSongsTask.getResult())
                        .build();

                startActivity(new Intent(LevelActivity.this, PlayActivity.class));

            }
        });
        allTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                //TODO Jakis toast?
                Log.d("onClickCategory: ", "FAILED");
            }
        });

    }


    private Task<List<SongPOJO>> getSongsForCurrentGenreAndLevelTask(Genre genre, Level level) {

        final TaskCompletionSource<List<SongPOJO>> tcs = new TaskCompletionSource<>();

        mDatabaseReference.child("Songs").child(genre.toString()).child(level.toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        List<SongPOJO> songs = new ArrayList<>();

                        for (DataSnapshot child : children) {
                            SongPOJO songPOJO = child.getValue(SongPOJO.class);
                            songs.add(songPOJO);
                        }
                        tcs.setResult(songs);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        tcs.setException(databaseError.toException());
                    }
                });

        return tcs.getTask();
    }


    private Task<List<String>> getGuessedSongsTask(Genre genre, Level level) {

        final TaskCompletionSource<List<String>> tcs = new TaskCompletionSource<>();
        mDatabaseReference.child("GuessedSongs").child(mFirebaseAuth.getCurrentUser().getUid())
                .child(genre.toString()).child(level.toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<String> songsGuessed = new ArrayList<>();
                        Iterable<DataSnapshot> guessedSongs = dataSnapshot.getChildren();

                        for (DataSnapshot song : guessedSongs) {
                            songsGuessed.add(song.getKey());
                        }

                        tcs.setResult(songsGuessed);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        tcs.setException(databaseError.toException());
                    }
                });

        return tcs.getTask();

    }

    private void getGenreUserGuessedSongsCount () {

        mDatabaseReference.child(mGenre.toString().toLowerCase() + "UserGuessedSongsCount").child(mFirebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserGenreGussedSongsPOJO userGenreGussedSongsPOJO = dataSnapshot.getValue(UserGenreGussedSongsPOJO.class);
                        setButtonsVisibilityAndLocks(userGenreGussedSongsPOJO);
                        setGuessedSongsCountTexts(userGenreGussedSongsPOJO);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void setGuessedSongsCountTexts(UserGenreGussedSongsPOJO userGenreGussedSongsPOJO) {
        int genreAvailableAll = 0;
        int genreAmateurAvailable = 0;
        int genreShowerSingerAvailable = 0;

        switch (mGenre) {
            case POP:
                genreAvailableAll = POP_AVAILABLE_ALL;
                genreAmateurAvailable = POP_AMATEUR_AVAILABLE_SONGS;
                genreShowerSingerAvailable = POP_SHOWER_SINGER_AVAILABLE_SONGS;
            break;
        }

        mGuessedOverallText.setText(String.valueOf(userGenreGussedSongsPOJO.getGuessedOverall()) + "/" + genreAvailableAll);
        mGuessedAmateurText.setText(String.valueOf(userGenreGussedSongsPOJO.getGuessedAmateur()) + "/" + genreAmateurAvailable);
        mGuessedShowerSingerText.setText(String.valueOf(userGenreGussedSongsPOJO.getGuessedShowerSinger()) + "/" + genreShowerSingerAvailable);
    }

    private void setButtonsVisibilityAndLocks(UserGenreGussedSongsPOJO userGenreGussedSongsPOJO) {

        if (userGenreGussedSongsPOJO.getGuessedAmateur() < POP_AMATEUR_AVAILABLE_SONGS) {
            mAmateurButton.setEnabled(true);
            mAmateurButton.setAlpha(1);
            lockAmateur.setImageResource(R.drawable.lock_open);
        }

        if (userGenreGussedSongsPOJO.getGuessedAmateur() >= POP_AMATEUR_AVAILABLE_SONGS
                && userGenreGussedSongsPOJO.getGuessedShowerSinger() < POP_SHOWER_SINGER_AVAILABLE_SONGS) {
            mShowerSingerButton.setEnabled(true);
            mShowerSingerButton.setAlpha(1);
            lockAmateur.setImageResource(R.drawable.lock_open);
        }


    }


}
