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
        //int dist = Math.abs(sp.y - _y); //only works with horizontal lines
        if (this.isOnSurface(sp.x, sp.y)){
            //double sx = s.getXSpeed();
            if (Math.abs(_angle) < 1e-6){
                s.setSpeed(s.getXSpeed(), 0.0f);
            }
            else if (Math.abs(_angle - 180.f) < 1e-6){
                s.setSpeed(0.0f, s.getYSpeed());
            }
            else {
                s.setSpeed(0.0f, 0.0f);
                double accelX = s.getXAccel(), accelY = s.getYAccel();
                double radAngle = _angle * Math.PI / 180;
                accelX += GameCanvas.GRAVITY * Math.sin(radAngle) * Math.cos(radAngle);
                accelY += GameCanvas.GRAVITY * Math.sin(radAngle) * Math.sin(radAngle);
                s.setAcceleration(accelX, accelY);
            }
            Point near = this.nearest(sp.x, sp.y);
            s.move(near.x, near.y);
        }
    }

    private boolean isOnSurface(int circleX, int circleY){
        double radAngle = _angle * Math.PI / 180;
        //double dy = (_y + Math.sin(radAngle)*(_y + _width) - _y),
        //       dx = (_x + Math.cos(radAngle)*(_x + _width) - _x);
        //double m = dy / dx;
        double m = Math.tan(radAngle);

        double dist = Math.abs(circleY - m * circleX + _x * m - _y) / Math.sqrt(1 + m*m);
        if (circleX >= _x && circleX <= (_x + _width) * Math.cos(radAngle) && dist <= Ball.RADIUS)
            return true;
        //calc the rect equation
        return false;
    }

    private Point nearest(int sx, int sy){
        double m = Math.tan(Math.toRadians(_angle));
        double x = (m * (sy + m * _x - _y) + sx) / (m*m + 1);
        double y = m * x - (m * _x - _y);

        return new Point((int)Math.round(x), (int)Math.round(y));
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
        canvas.save();
        canvas.rotate((float)_angle, _x, _y);
        canvas.drawRect(_x, _y, _x + _width, _y + _height, p);
        canvas.restore();
    }
}
