package com.example.c17snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;

public interface Movable {
    // Declare constant variables (if needed)
   // Canvas canvas = null;
   // Paint paint = null;
    //DisplayMetrics displayMetrics = new DisplayMetrics();

    //int screenWidth = displayMetrics.widthPixels;


    // Declare abstract methods (methods without a body)
    void draw(Canvas canvas, Paint paint);
    void move(Context context, int screenWidth);

    // You can also declare default methods (methods with a default implementation)
    // e.g., default void defaultMethod() {
    //            // implementation
    //        }

    // And static methods
    // e.g., static void staticMethod() {
    //            // implementation
    //        }
}