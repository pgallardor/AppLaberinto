package com.example.elaberinto;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class Ball implements Solid{
    private int _x, _y;
    private double _speed, _weight;
    public static final int RADIUS = 50;
    private static final double GRAVITY = 1.5f,
                                MAX_SPEED = 60.0f;

    public Ball(){
        _x = 0;
        _y = 0;
        _speed = 0.0f;
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
        _y = (int)(_y + Math.round(_speed));
        _speed += GRAVITY; //accel

        //speed capping
        if (_speed > MAX_SPEED)
            _speed = MAX_SPEED;

    }

    public void draw(Canvas canvas){
        Paint p = new Paint();
        p.setColor(Color.GRAY);
        canvas.drawCircle(_x, _y, RADIUS, p);
    }

    public void onCollide(Solid s){
        //does nothing
    }

    public void setSpeed(double ns){
        _speed = ns;
    }

    public Point getPosition(){
        return new Point(_x, _y + RADIUS);
    }
}
