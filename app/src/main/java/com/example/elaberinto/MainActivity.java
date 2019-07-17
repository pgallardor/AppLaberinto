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

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //setContentView(R.layout.activity_main);
        level = 3;
        setContentView(R.layout.game_layout);
        game = new GameCanvas(this, level);
        gameLayout = findViewById(R.id.game_layout);
        game.setGameListener(this);
        gameLayout.addView(game);



    }
    @Override
    public void onPause(){
        super.onPause();
        game.onPause();
    }
    @Override
    public void onResume(){
        super.onResume();
        game.onResume();
    }
    @Override
    public void onGameWon() {
        //swap activity
        //launch intent to replace the level

        Log.d("GAME WON", "CALLBACK LEVEL COMPLETED");
        Log.d("GAME WON", "LAUNCHING ACTIVITY");
        if ((level+1) == 4){
            game.setRunning(false);
            //use a transition for that?
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    game.setVisibility(View.GONE);
                    gameLayout.removeAllViews();
                }
            });
            Intent i = new Intent(this, EndGameActivity.class);
            startActivity(i);
        }
        /*
        Intent i = new Intent(this, TestActivity.class);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                game.setWon(false);
                game.setRunning(false);
                gameLayout.removeAllViews();
            }
        })
        startActivity(i);
        */
        //stop thread? or something:c
        //game.setThreadRunning(false);
        //game.setVisibility(View.GONE);

        //game.setThreadRunning(true);
        game.loadLevel(++level);
        //display end game screen
        //maybe allow to go back to start screen on that Intent?

        game.setWon(false);
        game.setVisibility(View.VISIBLE);


    }
}
