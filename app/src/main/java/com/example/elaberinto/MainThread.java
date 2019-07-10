package com.example.elaberinto;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread {
    private SurfaceHolder _surface;
    private GameCanvas _game;
    private boolean _running;
    public static Canvas canvas;

    public MainThread(SurfaceHolder surfaceHolder, GameCanvas gameCanvas){
        super();
        _surface = surfaceHolder;
        _game = gameCanvas;
    }

    public void run(){
        //FPS logic
        long startTime, timeMillis, waitTime, totalTime = 0, targetTime = 1000 / 60;
        int frameCount = 0;
        while(_running){
            startTime = System.nanoTime();
            canvas = null;

            try{
                canvas = _surface.lockCanvas();
                synchronized (_surface) {
                    _game.calcPhysics();
                    _game.draw(canvas);
                }
            } catch (Exception e) {} finally {
                if (canvas != null) {
                    try {
                        _surface.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMillis;

            try{
                sleep(waitTime);
            } catch (Exception e) {}

            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if (frameCount == 60){
                frameCount = 0;
                totalTime = 0;
            }
        }
    }

    public void setRunning(boolean isRunning){
        _running = isRunning;
    }
}
