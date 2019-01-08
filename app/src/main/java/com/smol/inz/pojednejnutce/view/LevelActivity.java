package com.smol.inz.pojednejnutce.view;

import android.app.ProgressDialog;
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
import com.smol.inz.pojednejnutce.model.UserCategoryGussedSongsPOJO;
import com.smol.inz.pojednejnutce.model.UserPOJO;
import com.smol.inz.pojednejnutce.utils.Category;
import com.smol.inz.pojednejnutce.utils.Level;

import java.util.ArrayList;
import java.util.List;

public class LevelActivity extends AppCompatActivity {

    public static final String LEVEL = "LEVEL";

    public static final int POP_AVAILABLE_ALL = 28;
    public static final int POP_AMATEUR_AVAILABLE_SONGS = 12;
    public static final int POP_SHOWER_SINGER_AVAILABLE_SONGS = 12;
    public static final int POP_PROFESSIONAL_AVAILABLE_SONGS = 4;

    public static final int ROCK_AVAILABLE_ALL = 29;
    public static final int ROCK_AMATEUR_AVAILABLE_SONGS = 12;
    public static final int ROCK_SHOWER_SINGER_AVAILABLE_SONGS = 12;
    public static final int ROCK_PROFESSIONAL_AVAILABLE_SONGS = 5;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    private Button mAmateurButton;
    private Button mShowerSingerButton;
    private Button mProfessionalButton;
    private TextView mCategoryTitleText;
    private Category mCategory;
    private TextView mUserScore;
    private TextView mGuessedOverallText;
    private TextView mGuessedAmateurText;
    private TextView mGuessedShowerSingerText;
    private TextView mGuessedProfessionalText;
    private ImageView lockAmateur;
    private ImageView lockShowerSinger;
    private ImageView lockProfessional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        initialize();
    }


    private void initialize() {
        setCategory();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUserScore = findViewById(R.id.user_score);

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

        mProfessionalButton = findViewById(R.id.button_professional);
        mProfessionalButton.setEnabled(false);
        mProfessionalButton.setAlpha(.3f);
        mProfessionalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCategory(Level.PROFESSIONAL);
            }
        });

        mCategoryTitleText = findViewById(R.id.category_title_text);
        mCategoryTitleText.setText(getmCategory().toString());
        mGuessedOverallText = findViewById(R.id.guessed_overall_text);
        mGuessedAmateurText = findViewById(R.id.guessed_amateur_text);
        mGuessedShowerSingerText = findViewById(R.id.guessed_shower_singer_text);
        mGuessedProfessionalText = findViewById(R.id.guessed_professional_text);
        lockAmateur = findViewById(R.id.lock_image_amateur);
        lockShowerSinger = findViewById(R.id.lock_image_shower_singer);
        lockProfessional = findViewById(R.id.lock_image_professional);
        getCategoryUserGuessedSongsCount();
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


    private void setCategory() {
        Intent intent = getIntent();

        switch (intent.getStringExtra(CategoryActivity.CATEGORY)) {
            case "POP":
                setmCategory(Category.POP);
                break;
            case "ROCK":
                setmCategory(Category.ROCK);
                break;
        }
    }

    public Category getmCategory() {
        return mCategory;
    }

    public void setmCategory(Category mCategory) {
        this.mCategory = mCategory;
    }

    private void onClickCategory(final Level level) {

        Task<Void> allTask;

        final Task<List<SongPOJO>> getSongsTask = getSongsForCurrentCategoryAndLevelTask(mCategory, level);
        final Task<List<String>> getGuessedSongsTask = getGuessedSongsTask(getmCategory(), level);

        allTask = Tasks.whenAll(getSongsTask, getGuessedSongsTask);
        allTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                PlayActivity.game = new Game.Builder()
                        .setLevel(level)
                        .setCategory(getmCategory())
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


    private Task<List<SongPOJO>> getSongsForCurrentCategoryAndLevelTask(Category category, Level level) {

        final TaskCompletionSource<List<SongPOJO>> tcs = new TaskCompletionSource<>();

        mDatabaseReference.child("Songs").child(category.toString()).child(level.toString())
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


    private Task<List<String>> getGuessedSongsTask(Category category, Level level) {

        final TaskCompletionSource<List<String>> tcs = new TaskCompletionSource<>();
        mDatabaseReference.child("GuessedSongs").child(mFirebaseAuth.getCurrentUser().getUid())
                .child(category.toString()).child(level.toString())
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

    private void getCategoryUserGuessedSongsCount() {

        mDatabaseReference.child(mCategory.toString().toLowerCase() + "UserGuessedSongsCount").child(mFirebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserCategoryGussedSongsPOJO userCategoryGussedSongsPOJO = dataSnapshot.getValue(UserCategoryGussedSongsPOJO.class);
                        setButtonsVisibilityAndLocks(userCategoryGussedSongsPOJO);
                        setGuessedSongsCountTexts(userCategoryGussedSongsPOJO);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void setGuessedSongsCountTexts(UserCategoryGussedSongsPOJO userCategoryGussedSongsPOJO) {
        int categoryAvailableAll = 0;
        int categoryAmateurAvailable = 0;
        int categoryShowerSingerAvailable = 0;
        int categoryProfessionalAvailable = 0;

        switch (mCategory) {
            case POP:
                categoryAvailableAll = POP_AVAILABLE_ALL;
                categoryAmateurAvailable = POP_AMATEUR_AVAILABLE_SONGS;
                categoryShowerSingerAvailable = POP_SHOWER_SINGER_AVAILABLE_SONGS;
                categoryProfessionalAvailable = POP_AMATEUR_AVAILABLE_SONGS;
                break;

            case ROCK:
                categoryAvailableAll = ROCK_AVAILABLE_ALL;
                categoryAmateurAvailable = ROCK_AMATEUR_AVAILABLE_SONGS;
                categoryShowerSingerAvailable = ROCK_SHOWER_SINGER_AVAILABLE_SONGS;
                categoryProfessionalAvailable = ROCK_PROFESSIONAL_AVAILABLE_SONGS;
                break;

        }

        mGuessedOverallText.setText(String.valueOf(userCategoryGussedSongsPOJO.getGuessedOverall()) + "/" + categoryAvailableAll);
        mGuessedAmateurText.setText(String.valueOf(userCategoryGussedSongsPOJO.getGuessedAmateur()) + "/" + categoryAmateurAvailable);
        mGuessedShowerSingerText.setText(String.valueOf(userCategoryGussedSongsPOJO.getGuessedShowerSinger()) + "/" + categoryShowerSingerAvailable);
        mGuessedProfessionalText.setText(String.valueOf(userCategoryGussedSongsPOJO.getGuessedProfessional()) + "/" + categoryProfessionalAvailable);
    }

    private void setButtonsVisibilityAndLocks(UserCategoryGussedSongsPOJO userCategoryGussedSongsPOJO) {

        switch (mCategory) {

            case POP:
                if (userCategoryGussedSongsPOJO.getGuessedAmateur() < POP_AMATEUR_AVAILABLE_SONGS) {
                    mAmateurButton.setEnabled(true);
                    mAmateurButton.setAlpha(1);
                    lockAmateur.setImageResource(R.drawable.lock_open);
                }

                if (userCategoryGussedSongsPOJO.getGuessedAmateur() >= POP_AMATEUR_AVAILABLE_SONGS
                        && userCategoryGussedSongsPOJO.getGuessedShowerSinger() < POP_SHOWER_SINGER_AVAILABLE_SONGS) {
                    mShowerSingerButton.setEnabled(true);
                    mShowerSingerButton.setAlpha(1);
                    lockAmateur.setImageResource(R.drawable.lock_open);
                }

                if (userCategoryGussedSongsPOJO.getGuessedShowerSinger() >= POP_PROFESSIONAL_AVAILABLE_SONGS
                        && userCategoryGussedSongsPOJO.getGuessedProfessional() < POP_PROFESSIONAL_AVAILABLE_SONGS) {
                    mProfessionalButton.setEnabled(true);
                    mProfessionalButton.setAlpha(1);
                    lockProfessional.setImageResource(R.drawable.lock_open);
                }
                break;


            case ROCK:
                if (userCategoryGussedSongsPOJO.getGuessedAmateur() < ROCK_AMATEUR_AVAILABLE_SONGS) {
                    mAmateurButton.setEnabled(true);
                    mAmateurButton.setAlpha(1);
                    lockAmateur.setImageResource(R.drawable.lock_open);
                }

                if (userCategoryGussedSongsPOJO.getGuessedAmateur() >= ROCK_AMATEUR_AVAILABLE_SONGS
                        && userCategoryGussedSongsPOJO.getGuessedShowerSinger() < ROCK_SHOWER_SINGER_AVAILABLE_SONGS) {
                    mShowerSingerButton.setEnabled(true);
                    mShowerSingerButton.setAlpha(1);
                    lockAmateur.setImageResource(R.drawable.lock_open);
                }

                if (userCategoryGussedSongsPOJO.getGuessedShowerSinger() >= ROCK_PROFESSIONAL_AVAILABLE_SONGS
                        && userCategoryGussedSongsPOJO.getGuessedProfessional() < ROCK_PROFESSIONAL_AVAILABLE_SONGS) {
                    mProfessionalButton.setEnabled(true);
                    mProfessionalButton.setAlpha(1);
                    lockProfessional.setImageResource(R.drawable.lock_open);
                }
                break;

        }


    }


}
