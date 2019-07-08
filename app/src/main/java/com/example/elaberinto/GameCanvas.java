package com.example.elaberinto;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class GameCanvas extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private MainThread _thread;
    private Ball _ball;
    private Block[] _block;
    public static final int BLOCKS = 2;
    public static final double GRAVITY = 1.5f;

    public GameCanvas(Context context) {
        super(context);
        getHolder().addCallback(this);
        this.setOnTouchListener(this);
        _thread = new MainThread(getHolder(), this);
        _ball = new Ball();
        _block = new Block[BLOCKS];
        _block[0] = new Block(400, 400, 20, 200, 0.0f);
        _block[1] = new Block(200, 600, 20, 200, 0.0f);
        setFocusable(true);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

    public void surfaceCreated(SurfaceHolder holder){
        _thread.setRunning(true);
        _thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        while(retry){
            try {
                _thread.setRunning(false);
                _thread.join();
            } catch (InterruptedException e){
                e.printStackTrace();;
            }
            retry = false;
        }
    }

    public void calcPhysics(){
        _ball.setAcceleration(0.0f, GRAVITY);
        _ball.calcMovement(); //inertia and gravity

        _block[0].onCollide(_ball); //collisions
        _block[1].onCollide(_ball);
    }

    public void draw(Canvas canvas){
        super.draw(canvas);
        canvas.drawRGB(255,255,255);

        _ball.draw(canvas);
        _block[0].draw(canvas);
        _block[1].draw(canvas);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        //System.out.println(view.getX() + " " + view.getY());
        int x = Math.round(event.getX()),
            y = Math.round(event.getY());

        System.out.println(x + ", " + y);
        _ball.setSpeed(-1.5f, 0.0f);
        _ball.move(x, y);

        return true;
    }
}
