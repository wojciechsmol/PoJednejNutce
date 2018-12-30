package com.smol.inz.pojednejnutce;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smol.inz.pojednejnutce.model.SongPOJO;
import com.smol.inz.pojednejnutce.utils.Genre;
import com.smol.inz.pojednejnutce.utils.Level;

import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Game {

    private static final int ONE_SCORE = 10;

    //Max score
    public static final int MAX_SCORE = 100;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    private Level mLevel;
    private Genre mGenre;
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

    public Game(Level mLevel, Genre mGenre, List<SongPOJO> availableSongs, List<String> guessedSongs) {
        this.mLevel = mLevel;
        this.mGenre = mGenre;

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

    public boolean userExistsExecuteFirebase() {

        Log.d("USER EXISTS: ", "USEREXISTS");
        final TaskCompletionSource<Boolean> tcs = new TaskCompletionSource<>();

        mDatabaseReference.child("Users").child(mFirebaseAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("dataSnapShot: ", dataSnapshot.toString());
                        tcs.setResult(dataSnapshot.exists());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("Exception: ", "Exception ");
                    }
                });

        Task<Boolean> t = tcs.getTask();

        Log.d("BEFORE AWAIT:", "BEFORE AWAIT");
        try {
            Tasks.await(t);
        } catch (ExecutionException | InterruptedException e) {
            t = Tasks.forException(e);
        }

        Log.d("AFTER AWAIT: ", "AFTER AWAIT");

        boolean result = false;

        if (t.isSuccessful()) {
            result = t.getResult();
        }
        return result;
    }


    // If the answer was correct
    public void correctAnswer() {
        // increase Score
        mScore += ONE_SCORE;
        ++mGuessedSongsCurrentGameCount;
        mSongsGuessedDuringTheGame.add(getCurrentSong().getId());

        // IF this is not the end of the game increase question number
        if (!endOfAGame())
            // Go to next question
            ++mCurrentQuestionNumber;
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

    public Genre getmGenre() {
        return mGenre;
    }

    public int getmGuessedSongsCurrentGameCount() {
        return mGuessedSongsCurrentGameCount;
    }

    //CLASS BUILDER
    //=====================================

    public static class Builder {
        private Level mLevel;
        private Genre mGenre;
        private List<SongPOJO> mAvailableSongs;
        private List<String> mGuessedSongs;

        public Builder() {
        }

        public Builder setLevel(Level level) {
            this.mLevel = level;
            return this;
        }

        public Builder setGenre(Genre genre) {
            this.mGenre = genre;
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
            return new Game(mLevel, mGenre, mAvailableSongs, mGuessedSongs);
        }

    }
}
