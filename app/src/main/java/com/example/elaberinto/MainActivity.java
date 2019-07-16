package com.example.elaberinto;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements GameCanvas.GameListener {
    private FrameLayout gameLayout;
    private GameCanvas game;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        /*
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        //setContentView(R.layout.activity_main);
        game = new GameCanvas(this, "test");
        game.setGameListener(this);
        //setContentView(game);

        setContentView(R.layout.game_layout);
        //game.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        gameLayout = findViewById(R.id.game_layout);
        gameLayout.addView(game);
        //gameLayout.removeAllViews();
        /*
        */

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
        //game.loadLevel("level2");
        //game.setWon(false);

        //gameLayout.removeViewAt(0);
        //game =  new GameCanvas(this);
        //gameLayout.addView(game);

    }
}
