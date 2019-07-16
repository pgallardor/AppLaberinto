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

        setContentView(R.layout.game_layout);
        game = new GameCanvas(this);
        //game.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        gameLayout = findViewById(R.id.game_layout);
        game.setGameListener(this);
        gameLayout.addView(game);
        //gameLayout.removeAllViews();
        /*
        game.setGameListener(new GameCanvas.GameListener() {
            @Override
            public void onGameWon() {
                //swap level?
            }
        });
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
        //gameLayout.removeAllViews();
        Log.d("GAME WON", "CALLBACK LEVEL COMPLETED");

        GameCanvas game = (GameCanvas) gameLayout.getChildAt(0);
        Log.d("GAME WON", "LAUNCHING ACTIVITY");
        //Intent i = new Intent(this, TestActivity.class);
        //startActivity(i);
        //stop thread? or something:c
        //game.setThreadRunning(false);
        //game.setVisibility(View.GONE);
/*
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SomeTitle").setMessage("SomeMessage");
        AlertDialog dialog =  builder.create();
        dialog.show();*/
        //game.setThreadRunning(true);
        game.loadLevel("level2");
        game.setWon(false);
        game.setVisibility(View.VISIBLE);

        //gameLayout.removeViewAt(0);
        //game =  new GameCanvas(this);
        //gameLayout.addView(game);

    }
}
