package com.example.elaberinto;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class Ball implements Solid{
    private int _x, _y;
    private double _speedX, _speedY, _weight, _acX, _acY;
    public static final int RADIUS = 50;
    private static final double MAX_SPEED = 60.0f;

    public Ball(){
        _x = 0;
        _y = 0;
        _acX = 0;
        _acY = 0;
        _speedX = _speedY = 0.0f;
    }

    public Ball(int x, int y){
        _x = x;
        _y = y;
    }

    public boolean move(int x, int y){
        _x = x;
        _y = y;
        return true;
    }

    public void calcMovement(){ //velocity vector
        //first calc the new position
        _y = (int)(_y + Math.round(_speedY));
        _x = (int)(_x + Math.round(_speedX));
        _speedY += _acY; //accel
        _speedX += _acX;

        //speed capping
        if (_speedY > MAX_SPEED) //modular or component cap??
            _speedY = MAX_SPEED;

    }

    public void draw(Canvas canvas){
        Paint p = new Paint();
        p.setColor(Color.GRAY);
        canvas.drawCircle(_x, _y, RADIUS, p);
    }

    public void setSpeed(double sx, double sy){
        _speedX = sx;
        _speedY = sy;
    }

    @Override
    public void setAcceleration(double ax, double ay) {
        _acX = ax;
        _acY = ay;
    }

    @Override
    public double getXSpeed() {
        return _speedX;
    }

    @Override
    public double getYSpeed() {
        return _speedY;
    }

    public double getXAccel(){
        return _acX;
    }

    public double getYAccel(){
        return _acY;
    }

    public Point getPosition(){
        //base angle???
        return new Point(_x, _y + RADIUS);
    }

    public void onCollide(Solid s){/*does nothing*/}

}
