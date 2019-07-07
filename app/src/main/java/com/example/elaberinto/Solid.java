package com.example.elaberinto;

import android.graphics.Point;

public interface Solid {
    void onCollide(Solid s);
    void setSpeed(double ns); //this will be a 2D vector soon
    void calcMovement(); //calcs the next position of the solid
    Point getPosition(); //test this with a getShape
    boolean move(int x, int y);
}
