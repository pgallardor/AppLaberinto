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
        while(_running){
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
        }
    }

    public void setRunning(boolean isRunning){
        _running = isRunning;
    }
}
