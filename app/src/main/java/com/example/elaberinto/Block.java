package com.example.elaberinto;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

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
    }

    public void onImpact(Solid s){
        Log.d("IMPACT", "ANGLE_" + _angle);
        Point sp = s.getPosition();
        Double vx = s.getXSpeed();
        Double vy = s.getYSpeed();
        //u = vector bloque
        //v = vector velocidad
        //cos(theta) = dot(u, v)/magnitud(u) * magnitud(v)

        Double ux = _width*Math.cos(Math.toRadians(_angle));
        Double uy = _width*Math.sin(Math.toRadians(_angle));
        Double theta = Math.acos((ux*vx + uy*vy)/(Math.sqrt(vx*vx + vy*vy)*_width));
        s.setSpeed(2*vx*Math.cos(-theta), 2*vy*Math.sin(-theta));
        //Point near = nearest(sp.x, sp.y);
        //s.move(near.x, near.y);
        s.setAcceleration(0, 0);
    }

    public boolean isOnSurface(int circleX, int circleY){
        double x = (double)_x, y = (double)_y, dx, dy;
        double radAngle = Math.toRadians(_angle);
        double drotx = (_width) * Math.cos(radAngle) - (_height) * Math.sin(radAngle);
        double droty = (_width) * Math.sin(radAngle) + (_height) * Math.cos(radAngle);

        dx = _x + drotx;
        dy = _y + droty;

        if (x > dx){
            double aux = x;
            x = dx;
            dx = aux;
        }

        if (y > dy){
            double aux = y;
            y = dy;
            dy = aux;
        }

        //return (circleX >= _x && circleX <= _x + _width && circleY >= _y && circleY <= y + _height);
        return (circleX >= x && circleX <= dx + _width && circleY >= y && circleY <= dy);
    }

    private Point nearest(int sx, int sy){
        double m = Math.tan(Math.toRadians(_angle));
        double x = (m * ((double)sy + m * _x - _y) + (double)sx) / (m*m + 1);
        double y = m * x - (m * _x - _y);

        return new Point((int)(Math.round(x+Ball.RADIUS*Math.cos(Math.toRadians(_angle)))),
                (int)(Math.round(y) + Ball.RADIUS*Math.sin(Math.toRadians(_angle))));
    }

    private double pointProduct(Point sp){
        return 0.0f;
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
