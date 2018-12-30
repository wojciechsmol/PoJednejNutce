package com.smol.inz.pojednejnutce.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.smol.inz.pojednejnutce.Game;
import com.smol.inz.pojednejnutce.R;

public class GameEndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        //Initializing newGameButton
        Button mainMenuButton = findViewById(R.id.button_main_menu);

        //Setting onClickListener for the newGameButton
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameEndActivity.this, HomeActivity.class));
            }
        });

        // Initializing exit button
        Button exitButton = findViewById(R.id.button_exit);
        //Setting onClickListener for the exitButton
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Before we quit the app changes activity to MainActivity so that this activity is
                // visible when user goes to the app again
                finish();
                startActivity(new Intent(GameEndActivity.this, HomeActivity.class));
                moveTaskToBack(true);

            }
        });

        // Initializing Textview for SCORE
        TextView scoreNumberTextView = findViewById(R.id.score_number);
        // Setting text for the scoreNumberTextView
        scoreNumberTextView.setText(PlayActivity.game.getmScore() + "/" + Game.MAX_SCORE);

    }

    @Override
    public void onBackPressed() {
        // When the back button is pressed the app goes to Main Menu
        startActivity(new Intent(GameEndActivity.this, HomeActivity.class));
    }
}
