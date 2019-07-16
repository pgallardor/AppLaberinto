package com.example.elaberinto;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import java.io.IOException;
import java.io.InputStream;

public class GameCanvas extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private MainThread _thread;
    private Ball _ball;
    private Block[] _block;
    private Rect _goal;
    private boolean _gameWon;
    private int[] _wasOnBlock;
    private Point _startPoint;
    private Hole[] _hole;
    private GameInclination gameInclination;
    public static int BLOCKS = 5, FRAME_CHECK = 40, HOLES = 1;
    public static final double GRAVITY = 2.0f;
    private float _lastCollisionX, _lastCollisionY;
    private int screenWidth, screenHeight;
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

        loadLevel("kek");
        //_ball.setAcceleration(0.0f, GRAVITY);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        setFocusable(true);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    public void loadLevel(String filename) {
        if (filename.equals("test")) {
            BLOCKS = 5;
            _block = new Block[BLOCKS];
            _block[0] = new Block(400, 400, 20, 200, 0.0f);
            _block[1] = new Block(370, 600, 20, 200, 30.0f);
            _block[2] = new Block(400, 900, 20, 400, 330.0f);
            _block[3] = new Block(200, 1100, 20, 160, 80.0f);
            _block[4] = new Block(150, 1200, 20, 400, 0.0f);

            _goal = new Rect(200, 1100, 300, 1200);
            _startPoint = new Point(100, 100);
            _wasOnBlock = new int[BLOCKS];
            for (int i = 0; i < BLOCKS; i++) {
                _wasOnBlock[i] = 0;
            }
            return;
        }

        InputStream is = getResources().openRawResource(R.raw.level2);
        try{
            byte[] buffer = new byte[is.available()];
            String text = "";
            while (is.read(buffer) != -1){
                text = new String(buffer);
            }
            String[] line = text.split("\n");
            int status = 0, //0 init, 1 goal, 2 Nblocks, 3blocks, 4 Nholes, 5 holes
                lineCnt = 0;
            for (String l : line){
                String[] args = l.split(" ");
                Log.i("LINE", args[0]);
                if (status == 0){
                    int x = Integer.parseInt(args[0]), y = Integer.parseInt(args[1].substring(0, args[1].length() - 1));
                    _startPoint = new Point(x, y);
                    status++;
                }

                else if (status == 1 || status == 3 || status == 5){
                    int nArgs = 4;
                    if (status == 3) nArgs = 5;
                    if (status == 5) nArgs = 3;

                    int[] pts = new int[nArgs];
                    for (int i = 0; i < nArgs; i++){
                        int parsed = 0;
                        if (i == nArgs - 1)
                            parsed = Integer.parseInt(args[i].substring(0, args[i].length() - 1));
                        else parsed = Integer.parseInt(args[i]);
                        pts[i] = parsed;
                    }

                    if (status == 1){
                        _goal = new Rect(pts[0], pts[1], pts[2], pts[3]);
                        status++;
                    }

                    if (status == 3){
                        _block[lineCnt] = new Block(pts[0], pts[1], pts[2], pts[3], pts[4]);
                        lineCnt++;
                        if (lineCnt == BLOCKS){
                            lineCnt = 0;
                            status++;
                        }
                    }

                    if (status == 5){
                        _hole[lineCnt] = new Hole(pts[0], pts[1], pts[2]);
                        lineCnt++;
                        if (lineCnt == HOLES){
                            lineCnt = 0;
                            status++;
                        }
                    }
                }

                else if (status == 2){
                    BLOCKS = Integer.parseInt(args[0].substring(0, args[0].length() - 1));
                    _block = new Block[BLOCKS];
                    status++;
                }

                else if (status == 4){
                    HOLES = Integer.parseInt(args[0].substring(0, args[0].length() - 1));
                    _hole = new Hole[HOLES];
                    status++;
                }
            }
        } catch(IOException e){ }

        _ball.move(_startPoint.x, _startPoint.y);

        _wasOnBlock = new int[BLOCKS];
        for (int i = 0; i < BLOCKS; i++) {
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
        for (int ihole = 0; ihole < HOLES; ihole++){
            if (_hole[ihole].isOnSurface(sp.x, sp.y)){
                _hole[ihole].onImpact(_ball);
            }
        }
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawRGB(255, 255, 255);
        Paint p = new Paint();
        p.setTextSize(40);
        canvas.save();
        canvas.scale((float)(screenWidth/1080.0), (float)(screenHeight/1920.0));
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
        for (int i = 0; i < HOLES; i++){
            _hole[i].draw(canvas);
        }

        if (_gameWon) {
            canvas.drawText("You won!", 100, 200, p);
        }
        if (!_ball.isAlive()){
            p.setColor(Color.RED);
            canvas.drawText("You fell!", 100, 200, p);
        }

        p.setColor(Color.BLUE);
        canvas.drawCircle(_lastCollisionX, _lastCollisionY, 10, p);
        canvas.restore();
        //assume we draw on a 10000x10000 canvas
        /*
        canvas.translate(screenWidth, screenHeight);
        canvas.restore();
        */
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        //System.out.println(view.getX() + " " + view.getY());
        //if (_gameWon) return false;
        _ball.setStatus(Ball.ALIVE);
        int x = Math.round(event.getX()),
                y = Math.round(event.getY());

        System.out.println(x + ", " + y);
        _ball.setSpeed(0.0f, 0.0f);
        //_ball.setAcceleration(-0.8f, 0.0f);
        _ball.move(_startPoint.x, _startPoint.y);

        return true;
    }
}
