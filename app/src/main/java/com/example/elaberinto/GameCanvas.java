package com.example.elaberinto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.util.ArrayList;

public class GameCanvas extends SurfaceView implements Runnable, View.OnTouchListener {
    public static final double GRAVITY = 2.0f;
    public static int BLOCKS = 5, HOLES = 1;
    private Ball _ball;
    private ArrayList<Block> _block;
    private Rect _goal;
    private boolean _gameWon;
    private Point _startPoint;
    private ArrayList<Hole> _hole;
    private GameInclination gameInclination;
    private int screenWidth, screenHeight;
    private GameListener gameListener;
    private SurfaceHolder surfaceHolder;
    private Context mContext;
    private boolean mRunning;
    private Thread mGameThread;
    private Bitmap _ballBMP, _bgBMP, _blockBMP;

    public GameCanvas(Context context, int startLevel) {
        super(context);
        mContext = context;
        this.setOnTouchListener(this);
        this.setKeepScreenOn(true);
        surfaceHolder = getHolder();
        gameInclination = new GameInclination(context);
        _gameWon = false;
        _ball = new Ball();

        _bgBMP = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        _blockBMP = BitmapFactory.decodeResource(getResources(), R.drawable.darkwood);
        _ballBMP = BitmapFactory.decodeResource(getResources(), R.drawable.ball);

        _block = new ArrayList<>();
        _hole = new ArrayList<>();
        loadLevel(startLevel);


        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        setFocusable(true);
    }

    public void loadLevel(int lvlID) {
        _block.clear();
        _hole.clear();
        if (lvlID == 0) {
            BLOCKS = 5;

            Block b1 = new Block(400, 400, 20, 200, 0.0f);
            Block b2 = new Block(370, 600, 20, 200, 30.0f);
            Block b3 = new Block(400, 900, 20, 400, 330.0f);
            Block b4 = new Block(200, 1100, 20, 160, 80.0f);
            Block b5 = new Block(150, 1200, 20, 400, 0.0f);

            _block.add(b1);
            _block.add(b2);
            _block.add(b3);
            _block.add(b4);
            _block.add(b5);
            _goal = new Rect(200, 1100, 300, 1200);
            _startPoint = new Point(100, 100);

            HOLES = 0;
            return;
        }
        int res_lvlID = -1;
        if (lvlID == 1) res_lvlID = R.raw.level1;
        if (lvlID == 2) res_lvlID = R.raw.level2;
        if (lvlID == 3) res_lvlID = R.raw.level3;
        if (lvlID == 4) res_lvlID = R.raw.level4;

        InputStream is = getResources().openRawResource(res_lvlID);
        try {
            byte[] buffer = new byte[is.available()];
            String text = "";
            while (is.read(buffer) != -1) {
                text = new String(buffer);
            }
            String[] line = text.split("\n");
            int status = 0, //0 init, 1 goal, 2 Nblocks, 3blocks, 4 Nholes, 5 holes
                    lineCnt = 0;
            for (String l : line) {
                String[] args = l.split(" ");
                if (status == 0) {
                    int x = Integer.parseInt(args[0]), y = Integer.parseInt(args[1].substring(0, args[1].length() - 1));
                    _startPoint = new Point(x, y);
                    status++;
                } else if (status == 1 || status == 3 || status == 5) {
                    int nArgs = 4;
                    if (status == 3) nArgs = 5;
                    if (status == 5) nArgs = 3;

                    int[] pts = new int[nArgs];
                    for (int i = 0; i < nArgs; i++) {
                        int parsed = 0;
                        if (i == nArgs - 1)
                            parsed = Integer.parseInt(args[i].substring(0, args[i].length() - 1));
                        else parsed = Integer.parseInt(args[i]);
                        pts[i] = parsed;
                    }

                    if (status == 1) {
                        _goal = new Rect(pts[0], pts[1], pts[2], pts[3]);
                        status++;
                    }

                    if (status == 3) {
                        _block.add(new Block(pts[0], pts[1], pts[2], pts[3], pts[4]));

                        lineCnt++;
                        if (lineCnt == BLOCKS) {
                            lineCnt = 0;
                            status++;
                        }
                    }

                    if (status == 5) {
                        _hole.add(new Hole(pts[0], pts[1], pts[2]));
                        lineCnt++;
                        if (lineCnt == HOLES) {
                            lineCnt = 0;
                            status++;
                        }
                    }
                } else if (status == 2) {
                    BLOCKS = Integer.parseInt(args[0].substring(0, args[0].length() - 1));
                    _block.clear();
                    status++;
                } else if (status == 4) {
                    HOLES = Integer.parseInt(args[0].substring(0, args[0].length() - 1));
                    _hole.clear();
                    status++;
                }
            }
        } catch (IOException e) {
        }

        _ball.move(_startPoint.x, _startPoint.y);

    }

    public void calcPhysics() {
        _ball.calcMovement();

        Pair<Double, Double> rotAccel = gameInclination.getAcceleration();
        _ball.setAcceleration(GRAVITY * rotAccel.first, GRAVITY * rotAccel.second);
        Point sp = _ball.getPosition();
        //check for position on the NEXT frame
        Pair<Double, Double> ballSpeed = new Pair<>(_ball.getXSpeed(), _ball.getYSpeed());
        for (int i = 0; i < BLOCKS; i++) {
            if (_block.get(i).isOnSurface((int) (sp.x + ballSpeed.first), (int) (sp.y + ballSpeed.second))) {
                Log.d("COLLISION", "DETECTED SOMETHING");
                //response to collision
                _block.get(i).onImpact(_ball);
                //move to previous time
            }
        }

        sp = _ball.getPosition();
        if (_goal.left <= sp.x && sp.x <= _goal.right && _goal.top <= sp.y && sp.y <= _goal.bottom) {
            _gameWon = true;
        }
        for (Hole ihole : _hole) {
            if (ihole.isOnSurface(sp.x, sp.y)) {
                ihole.onImpact(_ball);
            }
        }
    }

    public void drawOnCanvas(Canvas canvas) {
        Paint p = new Paint();
        canvas.drawBitmap(_bgBMP, 0, 0, p);
        p.setTextSize(40);
        canvas.save();
        canvas.scale((float) (screenWidth / 1080.0), (float) (screenHeight / 1920.0));

        p.setColor(Color.GREEN);
        p.setAlpha(80);
        canvas.drawRect(_goal, p);
        p.setAlpha(100);

        for (Hole hole : _hole) {
            hole.draw(canvas);
        }

        _ball.draw(canvas, _ballBMP);
        for (Block block : _block) {
            block.draw(canvas, _blockBMP);
        }

        if (_gameWon) {
            canvas.drawText("You won!", 100, 200, p);
        }
        if (!_ball.isAlive()) {
            p.setColor(Color.RED);
            p.setTextSize(72);
            canvas.drawText("You fell! Tap to try again.", 150, 1800, p);
            _ball.move(_startPoint.x, _startPoint.y);
        }

        canvas.restore();

        if (hasWon()) {
            gameListener.onGameWon();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        _ball.setStatus(Ball.ALIVE);
        int x = Math.round(event.getX()),
                y = Math.round(event.getY());

        System.out.println(x + ", " + y);
        _ball.setSpeed(0.0f, 0.0f);
        _ball.move(_startPoint.x, _startPoint.y);

        return true;
    }

    @Override
    public void run() {
        Canvas canvas;
        while (mRunning) {
            if (surfaceHolder.getSurface().isValid()) {
                canvas = surfaceHolder.lockCanvas();
                //canvas save and restore is done in the draw
                calcPhysics(); //physics
                drawOnCanvas(canvas); //draw
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void onPause() {
        mRunning = false;
        try {
            mGameThread.join();
        } catch (InterruptedException e) {

        }
    }

    public void onResume() {
        mRunning = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    public boolean hasWon() {
        return _gameWon;
    }

    public void setWon(boolean state) {
        _gameWon = state;
    }

    public void setGameListener(GameListener gameListener) {
        this.gameListener = gameListener;
    }

    public void setRunning(boolean state) {
        mRunning = state;
    }

    public interface GameListener {
        void onGameWon();
    }

}
