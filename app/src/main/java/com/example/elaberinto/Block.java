package com.example.elaberinto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

public class Block implements Solid {
    private int _height, _width, _x, _y, __id;
    private double _angle; //we might consider this later
    public static int N = 0;

    public Block(int x, int y, int height, int width, double angle){
        _x = x;
        _y = y;
        _height = height;
        _width = width;
        _angle = angle;
        //level builder help
        __id = N++;
    }

    public void onCollide(Solid s){
        Point sp = s.getPosition();
        Double radAngle = Math.toRadians(_angle);
        Double vx = s.getXSpeed();
        Double vy = s.getYSpeed();
        //u = vector bloque
        //v = vector velocidad
        //cos(theta) = dot(u, v)/magnitud(u) * magnitud(v)

        Double ux = _width*Math.cos(radAngle),
               uy = _width*Math.sin(radAngle);
        Double theta = Math.acos((ux*vx + uy*vy)/(Math.sqrt(vx*vx + vy*vy)*_width + 1e-6));
        //idea, rotate the speed vector in 90 - _alpha degrees?
        //s.setSpeed(vx * Math.cos(90-theta), vy * Math.sin(90-theta));
        //s.setAcceleration(Math.signum(s.getXAccel()) * GameCanvas.GRAVITY * Math.cos(radAngle),
        //        Math.signum(s.getYAccel()) * GameCanvas.GRAVITY * Math.sin(radAngle));
    }

    public void onImpact(Solid s){
        Point sp = s.getPosition();
        Double vx = s.getXSpeed();
        Double vy = s.getYSpeed();
        //u = vector bloque
        //v = vector velocidad
        //cos(theta) = dot(u, v)/magnitud(u) * magnitud(v)

        Double ux = _width*Math.cos(Math.toRadians(_angle));
        Double uy = _width*Math.sin(Math.toRadians(_angle));
        Double theta = Math.acos((ux*vx + uy*vy)/(Math.sqrt(vx*vx + vy*vy)*_width + 1e-6));

        s.setSpeed(vx*Math.cos(theta), vy*Math.sin(theta));
        /*
        if (theta > Math.PI / 2) theta = theta - Math.PI / 2;
        if (Math.abs(_angle - 90.0f) < 1e-6){
            s.setSpeed(-vx*Math.cos(-theta), -vy*Math.sin(-theta));
        }
        else s.setSpeed(vx*Math.cos(-theta), vy*Math.sin(-theta));
        */
        //Point near = nearest(sp.x, sp.y);
        //s.move(near.x, near.y);
        //s.setAcceleration(0, 0);
    }
    public void resolveCollision(Solid s){
        Point sp = s.getPosition();
        Double vx = s.getXSpeed();
        Double vy = s.getYSpeed();
        //u = vector bloque
        //v = vector velocidad
        //cos(theta) = dot(u, v)/magnitud(u) * magnitud(v)

        Double ux = _width*Math.cos(Math.toRadians(_angle));
        Double uy = _width*Math.sin(Math.toRadians(_angle));
        Double theta = Math.acos((ux*vx + uy*vy)/(Math.sqrt(vx*vx + vy*vy)*_width + 1e-6));

        //s.setSpeed(vx*Math.cos(theta), vy*Math.sin(theta));
        //move solid to nearest point before collision.
        //or bounce back?


        if (theta > Math.PI / 2) theta = theta - Math.PI / 2;
        //damp speed
        float slowFactor = 0.9f;
        if (Math.abs(_angle - 90.0f) < 1e-6){
            s.setSpeed(-vx*Math.cos(-theta)*slowFactor, -vy*Math.sin(-theta)*slowFactor);
        }
        else s.setSpeed(vx*Math.cos(-theta)*slowFactor, vy*Math.sin(-theta*slowFactor));

        //cap speed and or acceleration
        //Point near = nearest(sp.x, sp.y);
        //s.move(near.x, near.y);
        s.setAcceleration(s.getXAccel()*slowFactor, s.getYAccel()*slowFactor);
    }
    public boolean isOnSurface(int circleX, int circleY){
        double radAngle = Math.toRadians(_angle);
        //rotate (circleX, circleY) before comparison
        //https://math.stackexchange.com/questions/1687901/how-to-rotate-a-line-segment-around-one-of-the-end-points
        double cosTheta = Math.cos(radAngle);
        double sinTheta = Math.sin(radAngle);
        double deltaX = _x - circleX;
        double deltaY = _y - circleY;
        double testX = _x - cosTheta*deltaX - sinTheta*deltaY;
        double testY = _y - sinTheta*deltaX + cosTheta*deltaY;
        circleX = (int) testX;
        circleY = (int) testY;
        //return (circleX >= _x && circleX <= _x + _width && circleY >= _y && circleY <= y + _height);
        return (circleX >= _x && circleX <= _x + _width && circleY >= _y && circleY <= _y + _height);
    }

    private Point nearest(int sx, int sy){
        double m = Math.tan(Math.toRadians(_angle)+1e-6);
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

    public void draw(Canvas canvas, Bitmap bmp) {
        Paint p = new Paint();
        p.setColor(Color.rgb(102, 51, 0));
        canvas.save();
        canvas.rotate((float)_angle, _x, _y);
        //canvas.drawRect(_x, _y, _x + _width, _y + _height, p);
        Bitmap bl = Bitmap.createScaledBitmap(bmp, _width, _height, false);
        canvas.drawBitmap(bl, _x, _y, p);
        p.setColor(Color.WHITE);
        p.setTextSize(24);
        canvas.drawText(String.valueOf(__id + 1), _x + _width / 2, _y + (_height + 6) /2, p);
        canvas.restore();
        /*
        double radAngle = Math.toRadians(_angle);
        float x1 = (float) (_x + (_width) * Math.cos(radAngle) - (_height) * Math.sin(radAngle));
        float y1 = (float) (_y + (_width) * Math.sin(radAngle) + (_height) * Math.cos(radAngle));

        canvas.drawCircle(_x, _y, 10, p);
        p.setColor(Color.RED);
        canvas.drawCircle(x1,  y1, 10, p);
        */
    }
}
