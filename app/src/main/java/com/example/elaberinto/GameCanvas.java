package com.example.elaberinto;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Pair;
import android.view.FrameMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class GameCanvas extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private MainThread _thread;
    private Ball _ball;
    private Block[] _block;
    private boolean[] _wasOnBlock;
    public static final int BLOCKS = 5, FRAME_CHECK = 50;
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
        _block[3] = new Block(200, 1100, 20, 160, 80.0f);
        _block[4] = new Block(150, 1200, 20, 400, 0.0f);

        _ball.setAcceleration(0.0f, GRAVITY);

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
        boolean movementInterrupted = false, onFreeFall = true;
        //_ball.setAcceleration(_ball.getXAccel(), GRAVITY);
        Point sp = _ball.getPosition();
        Pair<Double, Double> ballSpeed = new Pair<>(_ball.getXSpeed(), _ball.getYSpeed());
        for (int i = 0; i < BLOCKS; i++) {
            for (int n_frame = 0; n_frame <= FRAME_CHECK; n_frame++) {
                int deltaX = (int)Math.round(n_frame * ballSpeed.first / FRAME_CHECK);
                int deltaY = (int)Math.round(n_frame * ballSpeed.second / FRAME_CHECK);
                if (_block[i].isOnSurface(sp.x + deltaX, sp.y + deltaY)) {
                    _ball.move(sp.x + deltaX, sp.y + deltaY);
                    if (_wasOnBlock[i]) {
                        _block[i].onCollide(_ball);
                    } else {
                        _block[i].onImpact(_ball);
                        movementInterrupted = true;
                    }
                    _wasOnBlock[i] = true;
                    onFreeFall = false;
                    break;
                } else _wasOnBlock[i] = false;
            }
        }
        //inertia and gravity
        if (!movementInterrupted){
            if (onFreeFall) _ball.setAcceleration(_ball.getXAccel(), GRAVITY);
            _ball.calcMovement();
        }
    }

    public void draw(Canvas canvas){
        super.draw(canvas);
        canvas.drawRGB(255,255,255);
        Paint p = new Paint();
        p.setTextSize(40);
        canvas.drawText(String.format("vx: %.2f vy: %.2f", _ball.getXSpeed(), _ball.getYSpeed()), 100, 100, p);
        canvas.drawText(String.format("ax: %.2f ay: %.2f", _ball.getXAccel(), _ball.getYAccel()), 100, 150, p);

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
        _ball.setSpeed(-3.0f, 0.0f);
        _ball.move(x, y);

        return true;
    }
}
