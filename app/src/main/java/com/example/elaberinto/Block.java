package com.example.elaberinto;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class Block implements Solid {
    private int _height, _width, _x, _y;
    private double _angle; //we might consider this later

    public Block(int x, int y, int height, int width, double angle){
        _x = x;
        _y = y;
        _height = height;
        _width = width;
        _angle = angle;
    }

    public void onCollide(Solid s){
        Point sp = s.getPosition();
        //Some code to check if the surface line is intersecting the ball
        int dist = Math.abs(sp.y - _y); //only works with horizontal lines
        if (sp.x >= _x && sp.x <= _x + _width && dist <= Ball.RADIUS){
            double sx = s.getXSpeed();
            s.setSpeed(sx, 0.0f);
            s.move(sp.x, _y - Ball.RADIUS);
        }
    }

    private boolean isInCircle(){
        return true;
    }

    //useless methods
    public boolean move(int x, int y){
        return false;
    }
    public Point getPosition() {
        return new Point(_x, _y);
    }
    public double getXSpeed() { return 0.0f; }
    public double getYSpeed() { return 0.0f; }

    public void setSpeed(double sx, double sy) {
        //static object
    }

    @Override
    public void calcMovement() {
        //static object
    }

    public void setAcceleration(double ax, double ay){

    }

    @Override
    public double getXAccel() {
        return 0.0f;
    }

    @Override
    public double getYAccel() {
        return 0.0f;
    }

    public void draw(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        int pivotX = Math.round(_x + (float) _width / 2);
        int pivotY = Math.round(_y + (float) _height / 2);
        canvas.save();
        canvas.rotate((float)_angle, pivotX, pivotY);
        canvas.drawRect(_x, _y, _x + _width, _y + _height, p);
        canvas.restore();
    }
}
