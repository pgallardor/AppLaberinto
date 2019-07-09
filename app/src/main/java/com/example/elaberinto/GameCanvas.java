package com.example.elaberinto;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class GameCanvas extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private MainThread _thread;
    private Ball _ball;
    private Block[] _block;
    private boolean[] _wasOnBlock;
    public static final int BLOCKS = 5;
    public static final double GRAVITY = 1.5f;

    public GameCanvas(Context context) {
        super(context);
        getHolder().addCallback(this);
        this.setOnTouchListener(this);
        _thread = new MainThread(getHolder(), this);
        _ball = new Ball();
        _block = new Block[BLOCKS];
        _block[0] = new Block(400, 400, 20, 200, 0.0f);
        _block[1] = new Block(370, 600, 20, 200, 30.0f);
        _block[2] = new Block(400, 900, 20, 400, 330.0f);
        _block[3] = new Block(300, 1200, 20, 400, 0.0f);
        _block[4] = new Block(340, 1100, 20, 160, 90.0f);

        _wasOnBlock = new boolean[BLOCKS];
        for (int i = 0; i < BLOCKS; i++){
            _wasOnBlock[i] = false;
        }

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
        Point sp = _ball.getPosition();
        for (int i = 0; i < BLOCKS; i++){
            if (_block[i].isOnSurface(sp.x, sp.y)){
                if (_wasOnBlock[i]){
                    _block[i].onCollide(_ball);
                }
                else{
                    _block[i].onImpact(_ball);
                }
                _wasOnBlock[i] = true;
            }
            else _wasOnBlock[i] = false;
        }

        _ball.calcMovement(); //inertia and gravity
    }

    public void draw(Canvas canvas){
        super.draw(canvas);
        canvas.drawRGB(255,255,255);

        _ball.draw(canvas);
        for (int i = 0; i < BLOCKS; i++){
            _block[i].draw(canvas);
        }
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
