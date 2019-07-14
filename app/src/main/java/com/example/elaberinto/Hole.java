package com.example.elaberinto;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class Hole implements Solid {
    private int _x, _y, _radius;

    public Hole(int x, int y, int r){
        _x = x;
        _y = y;
        _radius = r;
    }

    @Override
    public void onCollide(Solid s) {

    }

    @Override
    public void onImpact(Solid s) {
        if (s instanceof Ball){
            Ball b = (Ball) s;
            b.setStatus(!Ball.ALIVE);
        }
    }

    @Override
    public boolean isOnSurface(int circleX, int circleY) {
        double x = Math.pow(circleX - _x, 2),
               y = Math.pow(circleY - _y, 2);

        return x + y <= _radius*_radius;
    }

    public void draw(Canvas canvas){
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        canvas.drawCircle(_x, _y, _radius + 7, p);
    }

    @Override
    public void setSpeed(double sx, double sy) {

    }

    @Override
    public void calcMovement() {

    }

    @Override
    public Point getPosition() {
        return null;
    }

    @Override
    public double getXSpeed() {
        return 0;
    }

    @Override
    public double getYSpeed() {
        return 0;
    }

    @Override
    public void setAcceleration(double ax, double ay) {

    }

    @Override
    public double getXAccel() {
        return 0;
    }

    @Override
    public double getYAccel() {
        return 0;
    }

    @Override
    public boolean move(int x, int y) {
        return false;
    }
}
