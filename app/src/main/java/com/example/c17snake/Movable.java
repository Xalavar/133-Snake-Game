package com.example.c17snake;

import android.graphics.Canvas;
import android.graphics.Paint;

public interface Movable {
    // Declare constant variables (if needed)
   // Canvas canvas = null;
   // Paint paint = null;


    // Declare abstract methods (methods without a body)
    void draw(Canvas canvas, Paint paint);
    void move();

    // You can also declare default methods (methods with a default implementation)
    // e.g., default void defaultMethod() {
    //            // implementation
    //        }

    // And static methods
    // e.g., static void staticMethod() {
    //            // implementation
    //        }
}