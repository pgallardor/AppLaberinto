package com.example.elaberinto;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class Block implements Solid {
    private int _height, _width, _x, _y;
    private double _angle; //we might consider this later

    public Block(int x, int y, int height, int width){
        _x = x;
        _y = y;
        _height = height;
        _width = width;
    }

    public void onCollide(Solid s){
        Point sp = s.getPosition();
        /* Check the lines below
        if (sp.x >= _x && sp.x <= _x + _width && (sp.y <= _y + _height && _y <= sp.y) ){
            s.setSpeed(0.0f);
        }
        */
        //Some code to check if the surface line is intersecting the ball
        int dist = Math.abs(sp.y - _y);
        if (sp.x >= _x && sp.x <= _x + _width && dist <= Ball.RADIUS){
            s.setSpeed(0.0f);
            s.move(sp.x, _y - Ball.RADIUS);
        }
    }

    public void setSpeed(double ns) {
        //static object
    }

    @Override
    public void calcMovement() {
        //static object
    }

    public boolean move(int x, int y){
        return false;
    }

    public Point getPosition() {
        return new Point(_x, _y);
    }

    public void draw(Canvas canvas){
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        canvas.drawRect(_x, _y, _x + _width, _y + _height, p);
    }
}
