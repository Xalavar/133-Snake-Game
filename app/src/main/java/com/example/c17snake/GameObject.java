package com.example.c17snake;

import android.content.Context;
import android.graphics.Point;

abstract class GameObject {

    public Context context;
    public Point range;
    public int size;

    public GameObject(Context context, Point range, int size){

        this.context = context;
        this.range = range;
        this.size = size;


    }

    public GameObject() {

    }
}

