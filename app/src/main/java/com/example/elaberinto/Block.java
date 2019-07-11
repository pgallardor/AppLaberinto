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
        Point sp = s.getPosition();
        Log.d("COLLISION", "ANGLE_" + _angle);
        //add friction
        double accelX = 0.0f, accelY = 0.0f;
        double radAngle = Math.toRadians(_angle);

        if (Math.abs(_angle) < 1e-6){
            s.setSpeed(s.getXSpeed() * 0.95, 0.0f);
           // s.setAcceleration(s.getXAccel() * 0.5f, 0.0f);
            return;
        }
        s.setSpeed(s.getXSpeed(), 0 );
        accelX += GameCanvas.GRAVITY * Math.sin(radAngle) * Math.cos(radAngle);
        accelY += GameCanvas.GRAVITY * Math.sin(radAngle) * Math.sin(radAngle);
        s.setAcceleration(accelX, accelY);

    }

    public void onImpact(Solid s){
        //stop the solid
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
        s.setSpeed(0.8*vx*Math.cos(-theta), 0.8*vy*Math.sin(-theta));
        s.setAcceleration(0, 0);
        /*
        if (Math.abs(_angle) < 1e-6){
            s.setSpeed(s.getXSpeed(), 0.0f);
        //    s.setAcceleration(s.getXAccel(), 0.0f);
        }
        else if (Math.abs(_angle - 90.0f) < 1e-6){
            s.setSpeed(0.0f, s.getYSpeed());
         //   s.setAcceleration(0.0f, s.getYAccel());
        }
        else {
            s.setSpeed(0.0f, 0.0f);
       //     s.setAcceleration(0.0f, 0.0f);
        }
        */

        Point near = this.nearest(sp.x, sp.y);
        //s.move(near.x, near.y);
    }

    public boolean isOnSurface(int circleX, int circleY){
        double radAngle = _angle * Math.PI / 180;
        double m = Math.tan(radAngle);

        double dist = Math.abs(circleY - m * circleX + _x * m - _y) / Math.sqrt(1 + m*m);
        if (circleX >= _x && circleX <= _x + (_width) * Math.cos(radAngle) && dist <= Ball.RADIUS)
            return true;
        //calc the rect equation
        return false;
    }

    private Point nearest(int sx, int sy){
        double m = Math.tan(Math.toRadians(_angle));
        double x = (m * (sy + m * _x - _y) + sx) / (m*m + 1);
        double y = m * x - (m * _x - _y);

        return new Point((int)(Math.round(x+Ball.RADIUS*Math.cos(Math.toRadians(_angle)))),
                (int)(Math.round(y) + Ball.RADIUS*Math.sin(Math.toRadians(_angle))));
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
