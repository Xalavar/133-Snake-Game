package com.example.c17snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

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
        Random random = new Random();
        location.x = random.nextInt(mSpawnRange.x) + 1;
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
    public void move() {
        //can be used for moving apples , new feature
    }
}