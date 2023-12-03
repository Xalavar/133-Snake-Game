package com.example.c17snake;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

class SnakeGame extends SurfaceView implements Runnable{

    // Objects for the game loop/thread
    private Thread mThread = null;
    // Control pausing between updates
    private long mNextFrameTime;
    // Is the game currently playing and or paused?
    private GameState mGameState;

    private volatile boolean mNewLife = false;
    private GameInfo mGameInfo;

    // for playing sound effects
    private SoundPool mSP;
    private int mEat_ID = -1;
    private int mCrashID = -1;

    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40;
    private int mNumBlocksHigh;


    // Objects for drawing
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;

    // A snake ssss
    private Snake mSnake;
    // And an apple
    private Apple mApple;
    //private Paint mPausePlay;
    private PauseButton pauseButton;
    private Bitmap pauseBitmap;
    private Bitmap playBitmap;




    // This is the constructor method that gets called
    // from com.example.c17snake.SnakeActivity
    public SnakeGame(Context context, Point size) {
        super(context);

        // Initialize GameState
        mGameState = new GameState();
        mGameInfo = new GameInfo();

        // Add dimensions for pause button and load the image
        pauseButton = new PauseButton(2050, 50, 100, 100);
        pauseBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pause_button);
        playBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.play_button);

        // Work out how many pixels each block is
        int blockSize = size.x / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        mNumBlocksHigh = size.y / blockSize;

        // Initialize the SoundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mSP = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Prepare the sounds in memory
            descriptor = assetManager.openFd("get_apple.ogg");
            mEat_ID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("snake_death.ogg");
            mCrashID = mSP.load(descriptor, 0);

        } catch (IOException e) {
            // Error
        }

        // Initialize the drawing objects
        mSurfaceHolder = getHolder();
        mPaint = new Paint();

        // Call the constructors of our two game objects
        mApple = new Apple(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

        mSnake = new Snake(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

    }

    // This class holds useful information like the score and lives
    public class GameInfo {
        private int lives; // number of lives
        private int score; // number of points

        public GameInfo() {
            resetCounters();
        }
        
        public GameInfo(int lives, int score) {
            this.lives = lives;
            this.score = score;
        }

        // A method for resetting the lives and score to their default values
        // Usually when starting a new game
        public void resetCounters() {
            this.lives = 3;
            this.score = 0;
        }

        public int getLives() {
            return lives;
        }
        
        public void incLives() {
            lives++;
        }

        public void decLives() {
            lives--;
        }
        
        public int getScore() {
            return score;
        }
        
        public void incScore() {
            score++;
        }

        public void decScore() {
            score--;
        }

    }

    public class GameState {

        private volatile boolean playing;
        private volatile boolean paused;
        private volatile boolean userPaused;
        private volatile boolean gameOver;

        public GameState() {
            this.playing = false;
            this.paused = true;
            this.userPaused = false;
            this.gameOver = false;
        }

        public boolean isPlaying() {
            return playing;
        }

        public void setPlaying(boolean playing) {
            this.playing = playing;
        }

        public boolean isPaused() {
            return paused;
        }

        public void setPaused(boolean paused) {
            this.paused = paused;
        }

        public void togglePaused() {
            this.paused = !paused;
        }

        public boolean isUserPaused() {
            return userPaused;
        }

        public void setUserPaused(boolean userPaused) {
            this.userPaused = userPaused;
        }

        public void toggleUserPaused() {
            this.userPaused = !userPaused;
        }

        public boolean isGameOver() {
            return gameOver;
        }

        public void setGameOver(boolean gameOver) {
            this.gameOver = gameOver;
        }
    }


    public class PauseButton {

        private int x; // X-coordinate of pause button
        private int y; // Y-coordinate of pause button
        private int width;  // Width of pause button
        private int height; // Height of pause button
        //private boolean isPaused;

        public PauseButton(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public boolean isClicked(float touchX, float touchY) {
            return touchX >= x && touchX <= x + width && touchY >= y && touchY <= y + height;
        }
    }

    // Called to start a new game
    public void newGame() {

        // reset the snake
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);

        // Get the apple ready for dinner
        mApple.spawn();

        // Reset the Score
        //mScore = 0;

        // Setup mNextFrameTime so an update can triggered
        mNextFrameTime = System.currentTimeMillis();
    }
    public void newLife(){

        // reset the snake
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);

        // Get the apple ready for dinner
        mApple.spawn();

        // Setup mNextFrameTime so an update can triggered
        mNextFrameTime = System.currentTimeMillis();

        mGameState.setPaused(false);
    }


    // Handles the game loop
    @Override
    public void run() {
        while (mGameState.isPlaying()) {
            if(!mGameState.isPaused()) {
                // Update 10 times a second
                if (updateRequired()) {
                    update();
                }
            }

            draw();
        }
    }


    // Check to see if it is time for an update
    public boolean updateRequired() {

        // Run at 10 frames per second
        final long TARGET_FPS = 10;
        // There are 1000 milliseconds in a second
        final long MILLIS_PER_SECOND = 1000;

        // Are we due to update the frame
        if(mNextFrameTime <= System.currentTimeMillis()){
            // Tenth of a second has passed

            // Setup when the next update will be triggered
            mNextFrameTime =System.currentTimeMillis()
                    + MILLIS_PER_SECOND / TARGET_FPS;

            // Return true so that the update and draw
            // methods are executed
            return true;
        }

        return false;
    }


    // Update all the game objects
    public void update() {

        // Move the snake
        mSnake.move();

        mApple.move(getContext(),getWidth());



        // Did the head of the snake eat the apple?
        if(mSnake.checkDinner(mApple.getLocation())){
            // This reminds me of Edge of Tomorrow.
            // One day the apple will be ready!
            mApple.spawn();

            // Increase score by +1
            mGameInfo.incScore();

            // Play a sound
            mSP.play(mEat_ID, 1, 1, 0, 0, 1);
        }

        // Did the snake die?
        if (mSnake.detectCollision()) {
            mSP.play(mCrashID, 1, 1, 0, 0, 1);
            mNewLife = true;
            mGameInfo.decLives();
            //mLife = mLife - 1;
            //pause();

            if (mGameInfo.getLives()==0) {
                // Ran out of lives
                mSP.play(mCrashID, 1, 1, 0, 0, 1);
                mGameState.setPaused(true);

                // Toggle the Game Over screen
                mGameState.setGameOver(true);
            } else {
                // Need to move the snake away from the edge to avoid a collision loop
                newLife();
                mGameState.setPaused(true);
            }

           //mGameOver = true;

            // Possible idea for later: show score and highest score at the end
        }

    }


    // Do all the drawing
    public void draw() {
        // Get a lock on the mCanvas
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();

            // Fill the screen with a color
            mCanvas.drawColor(Color.argb(255, 26, 128, 182));

            // Set the size and color of the mPaint for the text
            mPaint.setColor(Color.argb(255, 255, 255, 255));
            mPaint.setTextSize(120);


            // Draw the score
            mCanvas.drawText("Score: " + mGameInfo.getScore(), 20, 120, mPaint);
            mCanvas.drawText("Lives: " + mGameInfo.getLives(), 620, 120, mPaint);

            // Draw the apple and the snake
            mApple.draw(mCanvas, mPaint);
            mSnake.draw(mCanvas, mPaint);

            if(mGameState.isGameOver()){
                // The Game Over screen

                // Fill the screen with a color
                mCanvas.drawColor(Color.argb(255, 0, 0, 0));

                // Set the size and color of the mPaint for the text
                mPaint.setColor(Color.argb(255, 255, 255, 255));
                mPaint.setTextSize(250);

                // "Game Over" text
                mCanvas.drawText("Game Over", 500, 500, mPaint);

                // Score
                mPaint.setTextSize(150);
                mCanvas.drawText("Score: "+mGameInfo.getScore(), 900, 700, mPaint);

                // How to start new game
                mPaint.setTextSize(50);
                mCanvas.drawText("Tap the screen to start a new game", 750, 950, mPaint);
            } else if (mGameState.isUserPaused()) {
                // For when the USER pauses the game

                // Set the size and color of the mPaint for the text
                mPaint.setColor(Color.argb(255, 255, 255, 255));
                mPaint.setTextSize(100);

                // Draw the message
                mCanvas.drawText("Paused",
                        1675, 150, mPaint);

                // Draw the pause button
                mCanvas.scale(0.5f, 0.5f);
                mCanvas.drawBitmap(playBitmap, 4100, 100, null);

            } else if (mGameState.isPaused() && !mNewLife){
                // For when the game is internally paused/frozen

                // Set the size and color of the mPaint for the text
                mPaint.setColor(Color.argb(255, 255, 255, 255));
                mPaint.setTextSize(250);

                mCanvas.drawText("Tap to start!", 450, 600, mPaint);
            } else if (mNewLife) {
                // Set the size and color of the mPaint for the text
                mPaint.setColor(Color.argb(255, 255, 255, 255));
                mPaint.setTextSize(100);

                String message = String.format("You have %d lives remaining.", mGameInfo.getLives());
                if (mGameInfo.getLives() == 1) {
                    // Uses singular "life" when the player only has 1 life
                    message = String.format("You have %d life remaining.", mGameInfo.getLives());
                }
                mPaint.setTextSize(120);
                mCanvas.drawText(message,375, 600, mPaint);

                //mCanvas.drawText("Tap again to use new life.", 500, 900, mPaint);
            } else {
                // The game is playing, so render the pause button
                mCanvas.scale(0.5f, 0.5f);
                mCanvas.drawBitmap(pauseBitmap, 4100, 100, null);
            }

            // Unlock the mCanvas and reveal the graphics for this frame
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (mNewLife) {
                    // When the player loses a life, this conditional activates
                    mNewLife = false;
                    newLife();

                    return true; // Don't change snake direction
                } else if (mGameState.isPaused() && !mGameState.isUserPaused() && !mGameState.isGameOver()) {
                    // The game is internally "paused" until the screen is tapped
                    // This is primarily used for freezing the game so the user can read the screen
                    // Triggers setup for starting a new game

                    mGameState.setPaused(false);
                    newGame();

                    return true; // Don't change snake direction
                }
                if (pauseButton.isClicked(motionEvent.getX(), motionEvent.getY()) && !mGameState.isGameOver()) {
                    // Only allow the user to pause the game when it's running
                    // If the game is paused, we won't allow user input anywhere except the pause/play button

                    mGameState.toggleUserPaused();
                    mGameState.togglePaused();

                    return true; // Don't change snake direction
                } else if (mGameState.isGameOver()) {
                    // Game over
                    // Show the Game Over screen and start new game upon tap
                    mGameState.setGameOver(false);
                    mGameState.setPaused(true);
                    mGameState.setUserPaused(false);

                    mGameInfo.resetCounters(); // reset the lives and score to the defaults

                    return true; // Don't change snake direction
                } else if (!mGameState.isPaused()) {
                    // Let the com.example.c17snake.Snake class handle the input
                    mSnake.switchHeading(motionEvent);
                }

                break;

            default:
                break;

        }
        return true;
    }



    // Stop the thread
    public void pause() {
        mGameState.setPlaying(false);
        try {
            mThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }


    // Start the thread
    public void resume() {
        mGameState.setPlaying(true);
        mThread = new Thread(this);
        mThread.start();
    }
}