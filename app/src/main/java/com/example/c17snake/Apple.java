package com.example.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.util.Random;

class Apple extends GameObject implements Movable{

    // The location of the apple on the grid
    // Not in pixels
    private Point location = new Point();

    // The range of values we can choose from
    // to spawn an apple
    private Point mSpawnRange;
    private int mSize;

    // An image to represent the apple
    private Bitmap mBitmapApple;

    private Random random = new Random();

    /// Set up the apple in the constructor
    Apple(Context context, Point range, int size) {
        super(context, range, size);

        // Make a note of the passed in spawn range
        mSpawnRange = range;
        // Make a note of the size of an apple
        mSize = size;
        // Hide the apple off-screen until the game starts
        location.x = -10;

        // Load the image to the bitmap
        mBitmapApple = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);

        // Resize the bitmap
        mBitmapApple = Bitmap.createScaledBitmap(mBitmapApple, size, size, false);
    }

    // This is called every time an apple is eaten
    void spawn() {
        // Choose two random values and place the apple
        //CHANGED
        location.x = random.nextInt(mSpawnRange.x - 1) + 1;
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }

    // Let com.example.c17snake.SnakeGame know where the apple is
    // com.example.c17snake.SnakeGame can share this with the snake
    Point getLocation() {
        return location;
    }

    // Draw the apple

    //void draw(Canvas canvas, Paint paint) {
        //canvas.drawBitmap(mBitmapApple,
                //location.x * mSize, location.y * mSize, paint);

   // }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mBitmapApple,
        location.x * mSize, location.y * mSize, paint);


    }



    @Override
    public void move(Context context, int screenWidth) {
        //can be used for moving apples , new feature

        Log.d("screenWidth", "Value: " + Float.toString(screenWidth));
        Log.d("location", "Value: " + Float.toString(location.x * mSize));

        //System.out.println(screenWidth);

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (windowManager != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(metrics);

            // Move the apple horizontally
            location.x += 1; // You can adjust the movement speed as needed


            // Check if the apple has reached the right boundary
            if (location.x * mSize >= screenWidth) {
                // If so, reset its position to the left
                location.x = 0;
            }
        }

        // Handle the case where WindowManager is not available
        // Optionally, you might want to log an error or handle it in another way
    }

}
