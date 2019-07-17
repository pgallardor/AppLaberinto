package com.example.elaberinto;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class Ball implements Solid {
    public static final int RADIUS = 20;
    public static final boolean ALIVE = true;
    private static final double MAX_SPEED = 5.0f;
    private int _x, _y;
    private double _speedX, _speedY, _acX, _acY;
    private boolean _isAlive;

    public Ball() {
        _x = 0;
        _y = 0;
        _acX = 0;
        _acY = 0;
        _speedX = _speedY = 0.0f;
        _isAlive = true;
    }

    public Ball(int x, int y) {
        _x = x;
        _y = y;
    }

    public boolean move(int x, int y) {
        _x = x;
        _y = y;
        return true;
    }

    public void calcMovement() { //velocity vector
        //speed capping
        if (Math.abs(_speedY) > MAX_SPEED) //modular or component cap??
            _speedY = Math.signum(_speedY) * MAX_SPEED;

        if (Math.abs(_speedX) > MAX_SPEED)
            _speedX = Math.signum(_speedX) * MAX_SPEED;

        //first calc the new position
        _y = (int) (_y + Math.round(_speedY));
        _x = (int) (_x + Math.round(_speedX));

        _speedY += _acY; //accel
        _speedX += _acX;

    }

    public void setStatus(boolean alive) {
        _isAlive = alive;
    }

    public boolean isAlive() {
        return _isAlive;
    }

    public void draw(Canvas canvas, Bitmap bmp) {
        if (!_isAlive) return;

        Paint p = new Paint();
        p.setColor(Color.GRAY);
        Bitmap locale = Bitmap.createScaledBitmap(bmp, 2 * RADIUS, 2 * RADIUS, false);
        canvas.drawBitmap(locale, _x - RADIUS, _y - RADIUS, p);

    }

    public void setSpeed(double sx, double sy) {
        _speedX = sx;
        _speedY = sy;
    }

    @Override
    public void setAcceleration(double ax, double ay) {
        _acX = ax;
        _acY = ay;
    }

    public boolean isOnSurface(int circleX, int circleY) {
        return false;
    }

    @Override
    public double getXSpeed() {
        return _speedX;
    }

    @Override
    public double getYSpeed() {
        return _speedY;
    }

    public double getXAccel() {
        return _acX;
    }

    public double getYAccel() {
        return _acY;
    }

    public void onImpact(Solid s) {
    }

    public Point getPosition() {
        //base angle???
        return new Point(_x, _y);
    }

    public void onCollide(Solid s) {/*does nothing*/}

}
