package com.example.elaberinto;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //setContentView(R.layout.activity_main);

        setContentView(R.layout.game_layout);
        game = new GameCanvas(this);
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
    public void onGameWon() {
        //swap activity
        //launch intent to replace the level
        //gameLayout.removeAllViews();
        Log.d("GAME WON", "CALLBACK LEVEL COMPLETED");
        gameLayout.removeViewAt(0);
        game =  new GameCanvas(this);
        gameLayout.addView(game);

    }
}
