package com.example.c17snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;

public interface Movable {
    // Declare constant variables (if needed)
    void draw(Canvas canvas, Paint paint);
    void move(Context context, int screenWidth);

}