package com.example.elaberinto;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class GameCanvas extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private MainThread _thread;
    private Ball _ball;
    private Block[] _block;
    private Rect _goal;
    private boolean _gameWon;
    private int[] _wasOnBlock;
    private GameInclination gameInclination;
    public static final int BLOCKS = 5, FRAME_CHECK = 40;
    public static final double GRAVITY = 2.0f;
    private float _lastCollisionX, _lastCollisionY;

    public GameCanvas(Context context) {
        super(context);
        getHolder().addCallback(this);
        this.setOnTouchListener(this);
        gameInclination = new GameInclination(context);
        _lastCollisionX = 0;
        _lastCollisionY = 0;
        _gameWon = false;
        _thread = new MainThread(getHolder(), this);
        _ball = new Ball();

        loadLevel("test");
        //_ball.setAcceleration(0.0f, GRAVITY);

        setFocusable(true);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    public void loadLevel(String filename) {
        if (filename.equals("test")) {
            _block = new Block[BLOCKS];
            _block[0] = new Block(400, 400, 20, 200, 0.0f);
            _block[1] = new Block(370, 600, 20, 200, 30.0f);
            _block[2] = new Block(400, 900, 20, 400, 330.0f);
            _block[3] = new Block(200, 1100, 20, 160, 80.0f);
            _block[4] = new Block(150, 1200, 20, 400, 0.0f);

            _goal = new Rect(200, 1100, 300, 1200);
            _wasOnBlock = new int[BLOCKS];
            for (int i = 0; i < BLOCKS; i++) {
                _wasOnBlock[i] = 0;
            }
            return;
        }
        //TODO: load file
        _block = new Block[5];
        _block[0] = new Block(50, 50, 20, 400, 0.0f);
        _block[1] = new Block(50, 50, 20, 250, 90.0f);
        _block[2] = new Block(50, 300, 20, 250, 0.0f);
        _block[3] = new Block(450, 50, 20, 250, 90.0f);
        _block[4] = new Block(350, 100, 20, 200, 0.0f);

        _goal = new Rect(200, 1100, 300, 1200);
        _wasOnBlock = new int[5];
        for (int i = 0; i < 5; i++) {
            _wasOnBlock[i] = 0;
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        _thread.setRunning(true);
        _thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                _thread.setRunning(false);
                _thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                ;
            }
            retry = false;
        }
    }

    public void calcPhysics() {
        boolean movementInterrupted = false, onFreeFall = true;
        // _ball.setAcceleration(_ball.getXAccel(), GRAVITY);
        _ball.calcMovement();

        Pair<Double, Double> rotAccel = gameInclination.getAcceleration();
        _ball.setAcceleration(GRAVITY * rotAccel.first, GRAVITY * rotAccel.second);
        Point sp = _ball.getPosition();
        //check for position on the NEXT frame
        Pair<Double, Double> ballSpeed = new Pair<>(_ball.getXSpeed(), _ball.getYSpeed());
        for (int i = 0; i < BLOCKS; i++) {
            if (_block[i].isOnSurface((int)(sp.x+ballSpeed.first), (int) (sp.y + ballSpeed.second))) {
                Log.d("COLLISION", "DETECTED SOMETHING");
                _lastCollisionX = sp.x;
                _lastCollisionY = sp.y;
                //response to collision
                _block[i].resolveCollision(_ball);
                //move to previous time
                //_ball.move((int)(sp.x-ballSpeed.first+_ball.getXAccel()), (int)(sp.y-ballSpeed.second+_ball.getYSpeed()));
            }
        }

        sp = _ball.getPosition();
        if (_goal.left <= sp.x && sp.x <= _goal.right && _goal.top <= sp.y && sp.y <= _goal.bottom) {
            _gameWon = true;
        }
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawRGB(255, 255, 255);
        Paint p = new Paint();
        p.setTextSize(40);
        canvas.drawText(String.format("vx: %.2f vy: %.2f", _ball.getXSpeed(), _ball.getYSpeed()), 100, 100, p);
        canvas.drawText(String.format("ax: %.2f ay: %.2f", _ball.getXAccel(), _ball.getYAccel()), 100, 150, p);
        Pair<Double, Double> rotAccel = gameInclination.getAcceleration();
        canvas.drawText(String.format("rot x: %.5f rot y: %.5f", rotAccel.first, rotAccel.second), 100, 250, p);

        p.setColor(Color.GREEN);
        canvas.drawRect(_goal, p);

        _ball.draw(canvas);
        for (int i = 0; i < BLOCKS; i++) {
            _block[i].draw(canvas);
        }
        if (_gameWon) {
            canvas.drawText("You won!", 100, 200, p);
        }
        p.setColor(Color.BLUE);
        canvas.drawCircle(_lastCollisionX, _lastCollisionY, 10, p);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        //System.out.println(view.getX() + " " + view.getY());
        //if (_gameWon) return false;
        int x = Math.round(event.getX()),
                y = Math.round(event.getY());

        System.out.println(x + ", " + y);
        _ball.setSpeed(0.0f, 0.0f);
        //_ball.setAcceleration(-0.8f, 0.0f);
        _ball.move(x, y);

        return true;
    }
}
