package com.example.elaberinto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity implements GameCanvas.GameListener {
    private FrameLayout gameLayout;
    private GameCanvas game;
    private int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        level = 0;
        setContentView(R.layout.game_layout);
        game = new GameCanvas(this, level);
        gameLayout = findViewById(R.id.game_layout);
        game.setGameListener(this);
        gameLayout.addView(game);


    }

    @Override
    public void onPause() {
        super.onPause();
        game.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        game.onResume();
    }

    @Override
    public void onGameWon() {
        //swap activity
        //launch intent to replace the level

        Log.d("GAME WON", "CALLBACK LEVEL COMPLETED");
        if ((level + 1) == 4) {
            Log.d("GAME WON", "LAUNCHING ACTIVITY");
            //use a transition for that?
            game.setRunning(false);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //game.setVisibility(View.GONE);
                    gameLayout.removeAllViews();
                }
            });
            Intent i = new Intent(this, EndGameActivity.class);
            startActivity(i);
            return;
        }
        game.loadLevel(++level);
        //display end game screen
        //maybe allow to go back to start screen on that Intent?

        game.setWon(false);
        game.setVisibility(View.VISIBLE);

    }
}
