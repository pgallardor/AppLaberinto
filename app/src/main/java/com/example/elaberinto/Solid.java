package com.example.elaberinto;

import android.graphics.Point;

public interface Solid {
    void onCollide(Solid s);
    void onImpact(Solid s);
    void setSpeed(double sx, double sy); //this will be a 2D vector soon
    void calcMovement(); //calcs the next position of the solid
    Point getPosition(); //test this with a getShape
    double getXSpeed();
    double getYSpeed();
    void setAcceleration(double ax, double ay);
    double getXAccel();
    double getYAccel();
    boolean move(int x, int y);
    boolean isOnSurface(int circleX, int circleY);
}
