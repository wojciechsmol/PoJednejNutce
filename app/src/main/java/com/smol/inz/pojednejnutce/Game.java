package com.smol.inz.pojednejnutce;

import android.icu.util.TimeUnit;
import android.support.annotation.NonNull;
import android.util.Log;

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
import com.smol.inz.pojednejnutce.model.GuessedTimeUserPOJO;
import com.smol.inz.pojednejnutce.model.SongPOJO;
import com.smol.inz.pojednejnutce.utils.Category;
import com.smol.inz.pojednejnutce.utils.Level;
import com.smol.inz.pojednejnutce.view.PlayActivity;

import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Game {

    public static final int SINGLE_FULL_SCORE = 10;

    //Max score
    public static final int MAX_SCORE = 100;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    private Level mLevel;
    private Category mCategory;
    //Current Question Number
    private int mCurrentQuestionNumber;
    //Score
    private int mScore;

    private int mAvailableSongsCount;

    private int mGuessedSongsCurrentGameCount;

    private int guessedSongsCurrentLevelCount;
    private int mMaxQuestions;

    //List of songs guessed During the game
    private List<String> mSongsGuessedDuringTheGame;

    //List of songs to guess
    private List<SongPOJO> mSongsToGuess;
    // Random generator
    public static RandomDataGenerator generator;

    static {
        // Initializing RandomDataGenerator Object
        generator = new RandomDataGenerator();
    }

    public Game(Level mLevel, Category mCategory, List<SongPOJO> availableSongs, List<String> guessedSongs) {
        this.mLevel = mLevel;
        this.mCategory = mCategory;

        mAvailableSongsCount = availableSongs.size();
        guessedSongsCurrentLevelCount = guessedSongs.size();
        initialize();
        setSongsToGuess(availableSongs, guessedSongs);

    }

    private void setSongsToGuess(List<SongPOJO> availableSongs, List<String> guessedSongs) {

        mSongsToGuess = new ArrayList<>();

        int index = 0;
        while (mSongsToGuess.size() < mMaxQuestions && index < availableSongs.size()) {
            SongPOJO song = availableSongs.get(index);

            if (!guessedSongs.contains(song.getId()))
                mSongsToGuess.add(song);

            ++index;
        }

        Collections.shuffle(mSongsToGuess);
    }

    private void initialize() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mSongsGuessedDuringTheGame = new ArrayList<>();
        this.mCurrentQuestionNumber = 1;
        setMaxQuestions();
        this.mScore = 0;
        this.mGuessedSongsCurrentGameCount = 0;

    }


    private void setMaxQuestions() {
        int songsLeft = mAvailableSongsCount - guessedSongsCurrentLevelCount;
        mMaxQuestions = songsLeft < 10 ? songsLeft : 10;


    }

    public int getmMaxQuestions() {
        return mMaxQuestions;
    }

    public int getmCurrentQuestionNumber() {
        return mCurrentQuestionNumber;
    }

    public int getmScore() {
        return mScore;
    }

    public List<String> getmSongsGuessedDuringTheGame() {
        return mSongsGuessedDuringTheGame;
    }

    public SongPOJO getCurrentSong() {
        return mSongsToGuess.get(mCurrentQuestionNumber - 1);
    }


    // If the answer was correct
    public int correctAnswer(double timeGuessed, double songDuration) {

        //Calculate Score based on the time of the guess
        int score = (int) ((1 - (timeGuessed / songDuration)) * Game.SINGLE_FULL_SCORE);

        // increase Score  (include correction if somehow value is greater than 10 due to added reflex delay)
        mScore += score > 10 ? 10 : score;

        ++mGuessedSongsCurrentGameCount;
        mSongsGuessedDuringTheGame.add(getCurrentSong().getId());

        // IF this is not the end of the game increase question number
        if (!endOfAGame())
            // Go to next question
            ++mCurrentQuestionNumber;


        saveGuessedSongTime(timeGuessed);

        return score;
    }

    private void saveGuessedSongTime(final double timeGuessed) {

        //adding the delay margin for realness
        final int realGuessedTime = (int)(timeGuessed + PlayActivity.REFLEX_MARGIN);

        GuessedTimeUserPOJO guessedTimeUserPOJO = new GuessedTimeUserPOJO(mFirebaseAuth.getCurrentUser().getEmail(), realGuessedTime);

        mDatabaseReference.child("SongGuessedTime").child(getCurrentSong().getId()).child(mFirebaseAuth.getCurrentUser().getUid())
                .setValue(guessedTimeUserPOJO)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("ADDED GUESSED TIME: ", String.valueOf(realGuessedTime));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ADDING GUESSED TIME: ", "FAILED");
                    }
                });
    }

    // If the answer was incorrect
    public void wrongAnswer() {
        // IF this is not the end of the game increase question number
        if (!endOfAGame())
            // Go to next question
            ++mCurrentQuestionNumber;
    }

    public boolean endOfAGame() {
        return mCurrentQuestionNumber >= (mMaxQuestions);
    }

    public Level getmLevel() {
        return mLevel;
    }

    public Category getmCategory() {
        return mCategory;
    }

    public int getmGuessedSongsCurrentGameCount() {
        return mGuessedSongsCurrentGameCount;
    }

    //CLASS BUILDER
    //=====================================

    public static class Builder {
        private Level mLevel;
        private Category mCategory;
        private List<SongPOJO> mAvailableSongs;
        private List<String> mGuessedSongs;

        public Builder() {
        }

        public Builder setLevel(Level level) {
            this.mLevel = level;
            return this;
        }

        public Builder setCategory(Category category) {
            this.mCategory = category;
            return this;
        }

        public Builder setAvailableSongs(List<SongPOJO> availableSongs) {
            this.mAvailableSongs = availableSongs;
            return this;
        }

        public Builder setGuessedSongs(List<String> guessedSongs) {
            this.mGuessedSongs = guessedSongs;
            return this;
        }

        public Game build() {
            return new Game(mLevel, mCategory, mAvailableSongs, mGuessedSongs);
        }

    }
}
