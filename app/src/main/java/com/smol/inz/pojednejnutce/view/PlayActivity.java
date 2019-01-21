package com.smol.inz.pojednejnutce.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.smol.inz.pojednejnutce.model.UserCategoryGussedSongsPOJO;
import com.smol.inz.pojednejnutce.model.UserPOJO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayActivity extends AppCompatActivity {

    // Toast duration in miliseconds
    private static final int TOAST_DURATION = 700;
    // Transition time in miliseconds
    private static final int TRANSITION_TIME = 1100;
    // Y axe offset for Toast
    private static final int GRAVITY_Y_AXE = 25;

    public static final int REFLEX_MARGIN = 700;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    //Game object
    public static Game game;
    // Answer button top-left
    private Button buttonAnswer0;
    // Answer button top-right
    private Button buttonAnswer1;
    // Answer button bottom-left
    private Button buttonAnswer2;
    // Answer button bottom-right
    private Button buttonAnswer3;
    // Progress Bar indicating song fragment time
    private SeekBar progressBar;
    // Audio file startTime
    private double startTime = 0;
    //Was button clicked
    private boolean wasButtonBlocked;
    // Handler for Runnable
    private final Handler myHandler = new Handler();

    private double finalTime;

    RelativeLayout activity_play;


    /**
     * Handles playback of the sound file
     */
    private MediaPlayer mMediaPlayer;
    /**
     * Handles audio focus when playing a sound file
     */
    private AudioManager mAudioManager;
    /**
     * Handles playback of the answer sound file (correct or wrong)
     */
    private MediaPlayer mAnswerMediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        // Initializing required elements
        initialization();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Clean up resources

        releaseMediaPlayer();
        //Start audio
        startAudio();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Pause the media player
        if (mMediaPlayer != null)
            mMediaPlayer.pause();

    }

    @Override
    public void onResume() {
        super.onResume();
        // Resume the media player
        if (mMediaPlayer != null)
            mMediaPlayer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Clean up resources
        releaseMediaPlayer();
    }

    // When BACK button is Pressed I invoke the alert
    @Override
    public void onBackPressed() {
        showEndOfGameAlert();
    }

    //Initializes required elements
    private void initialization() {



        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();

        buttonAnswer0 = (Button) findViewById(R.id.button_answer0);
        buttonAnswer0.setOnClickListener(buttonAnswerListener);
        buttonAnswer1 = (Button) findViewById(R.id.button_answer1);
        buttonAnswer1.setOnClickListener(buttonAnswerListener);
        buttonAnswer2 = (Button) findViewById(R.id.button_answer2);
        buttonAnswer2.setOnClickListener(buttonAnswerListener);
        buttonAnswer3 = (Button) findViewById(R.id.button_answer3);
        buttonAnswer3.setOnClickListener(buttonAnswerListener);
        wasButtonBlocked = false;

        activity_play = findViewById(R.id.activity_play);

        ImageButton exitIconButton = findViewById(R.id.exit_icon);
        // Setting onClickLister for the ImageButton
        exitIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If user clicks on exit icon then the alert is shown
                showEndOfGameAlert();
            }
        });
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setClickable(false);
        // Initializing mAudioManager
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // Setting Question text (with correct number)
        TextView questionText = (TextView) findViewById(R.id.question_text);
        questionText.setText(getString(R.string.question, (game.getmCurrentQuestionNumber()), game.getmMaxQuestions()));



        List<String> possibleAnswers = new ArrayList<>();
        possibleAnswers.add(game.getCurrentSong().getTitle());
        //possibleAnswers.add(game.getCurrentSong().getAnswer2());
        //possibleAnswers.add(game.getCurrentSong().getAnswer3());
        //possibleAnswers.add(game.getCurrentSong().getAnswer4());
        possibleAnswers.add("Hey Joe");
        possibleAnswers.add("Iris");
        possibleAnswers.add("Twist And Shout");

        Collections.shuffle(possibleAnswers);
        // set Answers Texts on the Buttons
        buttonAnswer0.setText(possibleAnswers.get(0));
        buttonAnswer1.setText(possibleAnswers.get(1));
        buttonAnswer2.setText(possibleAnswers.get(2));
        buttonAnswer3.setText(possibleAnswers.get(3));

    }


    /**
     * This listener gets triggered whenever the audio focus changes
     * (i.e., we gain or lose audio focus because of another app or device).
     */
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // The AUDIOFOCUS_LOSS_TRANSIENT case means that we've lost audio focus for a
                // short amount of time. The AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK case means that
                // our app is allowed to continue playing sound but at a lower volume. We'll treat
                // both cases the same way because our app is playing short sound files.

                // Pause playback and reset player to the start of the file. That way, we can
                // play the word from the beginning when we resume playback.
                mMediaPlayer.pause();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // The AUDIOFOCUS_GAIN case means we have regained focus and can resume playback.
                mMediaPlayer.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // The AUDIOFOCUS_LOSS case means we've lost audio focus and
                // Stop playback and clean up resources
                releaseMediaPlayer();
            }
        }
    };

    /**
     * This listener gets triggered when the {@link MediaPlayer} has completed
     * playing the audio file.
     */
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {

            //Once the audio file stops playing (ONLY AT THE END OF FILE)I clean up the resources
            releaseMediaPlayer();

            // only if this is the end of the file (none of the buttons was clicked)
            if (!wasButtonBlocked) {

               // Creating Media Player Object playing wrong answer sound and starting it
                mAnswerMediaPlayer = MediaPlayer.create(PlayActivity.this, R.raw.wrong_answer);
                mAnswerMediaPlayer.start();
                //Blocking the buttons
                blockButtons();
                // Showing a toast with info that time's up
                final Toast toast = Toast.makeText(PlayActivity.this, getString(R.string.time_up), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, GRAVITY_Y_AXE);
                toast.show();

                // Cancelling Toast so that it lasts time specified in TOAST_DURATION
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, TOAST_DURATION);

                // The transition goes after TRANSITION TIME:
                myHandler.postDelayed(new Runnable() {
                    public void run() {

                        // If this is the end of the game go to GameEndActivity
                        if (game.endOfAGame()) {
                            game.wrongAnswer();
                            updateDataAndStartGameEndActivity();
                        }  // Otherwise go to next question
                        else {
                            game.wrongAnswer();
                            startActivity(new Intent(PlayActivity.this, PlayActivity.class));
                        }
                    }
                }, TRANSITION_TIME);
            }
        }


    };

    // LISTENER FOR ALL FOUR BUTTONS
    private View.OnClickListener buttonAnswerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // Blocking the buttons
            blockButtons();

            //Uwzglednienie opoznienia na czas reakcji
            double timeGuessed = mMediaPlayer.getCurrentPosition() - REFLEX_MARGIN;


            // Stop playback and release resource
            releaseMediaPlayer();
            // Answer button initialization
            Button answerButton = (Button) v;
            // Text from the button selected
            String buttonText = answerButton.getText().toString();

            // IF THE ANSWER WAS CORRECT (buttonText is equal to the song title)
            if (buttonText.equals(game.getCurrentSong().getTitle())) {

                // Creating Media Player Object playing Correct answer sound and starting it
                // Creating Media Player Object playing Correct answer sound and starting it
                mAnswerMediaPlayer = MediaPlayer.create(PlayActivity.this, R.raw.correct_answer);
                mAnswerMediaPlayer.start();




                // Showing a toast with info that the answer was correct
                final Snackbar snackbar = Snackbar.make(activity_play, getString(R.string.correct_answer) + "   + " +game.correctAnswer(timeGuessed, finalTime) + " POINTS!", Snackbar.LENGTH_SHORT);
                snackbar.show();

//                // Cancelling Toast so that it lasts time specified in TOAST_DURATION
//                myHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        snackbar.();
//                    }
//                }, TOAST_DURATION);

                // The transition goes after TRANSITION TIME:
                myHandler.postDelayed(new Runnable() {
                                          public void run() {
                                              // If this is the end of the game go to GameEndActivity
                                              if (game.endOfAGame()) {
                                                  updateDataAndStartGameEndActivity();
                                              } // Otherwise go to next question
                                              else {
                                                  startActivity(new Intent(PlayActivity.this, PlayActivity.class));
                                              }

                                          }
                                      }

                        , TRANSITION_TIME);

            } // IF THE ANSWER WAS WRONG:
            else {
                // Creating Media Player Object playing wrong answer sound and starting it
                mAnswerMediaPlayer = MediaPlayer.create(PlayActivity.this, R.raw.wrong_answer);
                mAnswerMediaPlayer.start();
                // Showing a toast with info that the answer was wrong
                final Toast toast = Toast.makeText(PlayActivity.this, getString(R.string.wrong_answer), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, GRAVITY_Y_AXE);
                toast.show();

                // Cancelling Toast so that it lasts time specified in TOAST_DURATION
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, TOAST_DURATION);

                // The transition goes after TRANSITION TIME:
                myHandler.postDelayed(new Runnable() {
                    public void run() {
                        // If this is the end of the game go to GameEndActivity
                        if (game.endOfAGame()) {
                            game.wrongAnswer();
                            updateDataAndStartGameEndActivity();
                        } // Otherwise go to next question
                        else {
                            game.wrongAnswer();
                            startActivity(new Intent(PlayActivity.this, PlayActivity.class));
                        }
                    }
                }, TRANSITION_TIME);
            }
        }
    };

    // Starting main audio file (song audio file)
    private void startAudio() {

        // Request audio focus so in order to play the audio file.
        int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // We have audio focus now.

            try {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(game.getCurrentSong().getUrl());
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }catch (Exception e) {
                e.printStackTrace();
            }




            // setting startTime for the progressBar
            startTime = mMediaPlayer.getCurrentPosition();
            // setting finalTime for the progressBar
            finalTime = mMediaPlayer.getDuration();
            // Setup a listener on the media player, so that we can stop and release the
            // media player once the sound has finished playing.
            mMediaPlayer.setOnCompletionListener(mCompletionListener);

            // initializing progressBar
            progressBar.setProgress((int) startTime);
            progressBar.setMax((int) finalTime);
            // Starting progress Bar
            myHandler.postDelayed(UpdateSongTime, 100);
        }


    }

    // This process updates progressBar
    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            // Updating progressBar each second based on audio being played
            if (mMediaPlayer != null) {
                startTime = mMediaPlayer.getCurrentPosition();
                progressBar.setProgress((int) startTime);
                myHandler.postDelayed(this, 10);
            }
        }
    };

    // Blocking the buttons
    private void blockButtons() {
        buttonAnswer0.setClickable(false);
        buttonAnswer1.setClickable(false);
        buttonAnswer2.setClickable(false);
        buttonAnswer3.setClickable(false);
        wasButtonBlocked = true;
    }

    // stop Audio and release resources from mediaPlayers
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;

            // Regardless of whether or not we were granted audio focus, abandon it. This also
            // unregisters the AudioFocusChangeListener so we don't get anymore callbacks.
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }

        // If the media player is not null, then it may be currently playing a sound.
        if (mAnswerMediaPlayer != null) {

            mAnswerMediaPlayer.release();
            mAnswerMediaPlayer = null;
        }
    }

    // This alert ensures that the user really want to cancel the game
    private void showEndOfGameAlert() {
        // Initializing alertBuilder
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PlayActivity.this);
        alertBuilder.setMessage(getString(R.string.cancel_game));
        alertBuilder.setCancelable(true);

        // Setting positive button
        alertBuilder.setPositiveButton(
                getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Go to GameEndActivity
                        updateDataAndStartGameEndActivity();
                        dialog.cancel();
                    }
                });

        // Setting negative button
        alertBuilder.setNegativeButton(
                getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        //Finally creating alert using builder
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void updateDataAndStartGameEndActivity() {

        final TaskCompletionSource<Void> tcsUpdateUserScore = new TaskCompletionSource<>();
        final DatabaseReference userReference = mDatabaseReference.child("Users").child(mFirebaseAuth.getCurrentUser().getUid());

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserPOJO user = dataSnapshot.getValue(UserPOJO.class);
                        int score = user.getScore();
                        score += game.getmScore();
                        user.setScore(score);

                        switch (game.getmCategory()) {
                            case POP:
                                int scorePop = user.getScorePOP();
                                scorePop += game.getmScore();
                                user.setScorePOP(scorePop);
                                break;

                            case ROCK:
                                int scoreRock = user.getScoreROCK();
                                scoreRock += game.getmScore();
                                user.setScoreROCK(scoreRock);
                                break;
                        }

                        userReference.setValue(user);
                        tcsUpdateUserScore.setResult(null);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        tcsUpdateUserScore.setException(databaseError.toException());
                    }
                });

        //UPDATE Guessed Songs count
        //===========================================================

        final TaskCompletionSource<Void> tcsUpdateGuessedSongsCount = new TaskCompletionSource<>();
        final DatabaseReference guessedSongsReference = mDatabaseReference.
                child(game.getmCategory().toString().toLowerCase() + "UserGuessedSongsCount").
                child(mFirebaseAuth.getCurrentUser().getUid());

        guessedSongsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserCategoryGussedSongsPOJO userCategoryGussedSongsPOJO = dataSnapshot.getValue(UserCategoryGussedSongsPOJO.class);
                userCategoryGussedSongsPOJO.setGuessedOverall(userCategoryGussedSongsPOJO.getGuessedOverall() + game.getmGuessedSongsCurrentGameCount());

                switch (game.getmLevel()) {
                    case AMATEUR:
                        userCategoryGussedSongsPOJO.setGuessedAmateur(userCategoryGussedSongsPOJO.getGuessedAmateur() + game.getmGuessedSongsCurrentGameCount());
                        break;
                    case SHOWER_SINGER:
                        userCategoryGussedSongsPOJO.setGuessedShowerSinger(userCategoryGussedSongsPOJO.getGuessedShowerSinger() + game.getmGuessedSongsCurrentGameCount());
                        break;
                    case PROFESSIONAL:
                        userCategoryGussedSongsPOJO.setGuessedProfessional(userCategoryGussedSongsPOJO.getGuessedProfessional() + game.getmGuessedSongsCurrentGameCount());
                        break;
                }

                guessedSongsReference.setValue(userCategoryGussedSongsPOJO);
                tcsUpdateGuessedSongsCount.setResult(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tcsUpdateGuessedSongsCount.setException(databaseError.toException());
            }
        });

        //UPDATE GuessedSongs
        //===========================================================
        final DatabaseReference addGuessedSongsReference = mDatabaseReference.child("GuessedSongs").child(mFirebaseAuth.getCurrentUser().getUid())
                .child(game.getmCategory().toString()).child(game.getmLevel().toString());

        for (String songId: game.getmSongsGuessedDuringTheGame()) {
            addGuessedSongsReference.child(songId).setValue("true")
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("PlayActivity: ", "FAILED Updating guessed Songs");
                        }
                    });
        }

        //PERFORM TASKS AND GO TO GameEndActivity

        Task<Void> allTask;

        allTask = Tasks.whenAll(tcsUpdateUserScore.getTask(), tcsUpdateGuessedSongsCount.getTask());
        allTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
                startActivity(new Intent(PlayActivity.this, GameEndActivity.class));
            }
        });
        allTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("UpdateAndStartGameEnd: ", "FAILED");
            }
        });


    }


}
