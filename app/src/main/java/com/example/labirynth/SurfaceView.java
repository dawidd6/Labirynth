package com.example.labirynth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class SurfaceView extends View implements Runnable {
    private int fps;
    private int interval;
    private Handler handler;
    private Paint linePaint;
    private Paint ballPaint;
    private Paint holePaint;
    private float ballRadius;
    private float ballX;
    private float ballY;
    private float speedX;
    private float speedY;
    private float speed0;
    private float surfaceStopX;
    private float surfaceStartX;
    private float surfaceStartY;
    private float surfaceStopY;
    private float lineStartX;
    private float lineStartY;
    private float lineStopX;
    private float lineStopY;
    private float lineTopY;
    private float lineBottomY;
    private float lineLeftX;
    private float lineRightX;
    private float lineThickness;
    private float holeX;
    private float holeY;
    private float holeBallDistance;
    private float[] lines;
    private float[] holes;

    public SurfaceView(Context context)  {
        super(context);

        init();

        handler = new Handler();

        ballPaint = new Paint();
        ballPaint.setStyle(Paint.Style.FILL);
        ballPaint.setColor(Color.BLUE);

        holePaint = new Paint();
        holePaint.setStyle(Paint.Style.FILL);
        holePaint.setColor(Color.BLACK);

        linePaint = new Paint();
        linePaint.setStrokeWidth(lineThickness);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setColor(Color.BLACK);
    }

    private void init() {
        // Redraw
        fps = 120;
        interval = 1000 / fps;

        // Ball
        ballRadius = getWidth() / 20f;
        ballX = ballRadius; //getWidth() / 2;
        ballY = ballRadius; //getHeight() / 2;

        // Surface
        surfaceStopX = getWidth() - ballRadius;
        surfaceStartX = ballRadius;
        surfaceStopY = getHeight() - ballRadius;
        surfaceStartY = ballRadius;

        // Speed
        speedX = 0;
        speedY = 0;
        speed0 = 0.05f;

        // Lines
        lineThickness = 10;
        lines = new float[]{
                ballRadius*4, 0, ballRadius*4, getHeight() - ballRadius*4,
                ballRadius*4, getHeight() - ballRadius*4, getWidth() - ballRadius*4, getHeight() - ballRadius*4,
                getWidth() - ballRadius*4, ballRadius*4, getWidth() - ballRadius*4, getHeight() - ballRadius*4,
                ballRadius*8, ballRadius*4, getWidth() - ballRadius*4, ballRadius*4,
                ballRadius*8, ballRadius*4, ballRadius*8, getHeight() - ballRadius*8,
                ballRadius*8, getHeight() - ballRadius*8, ballRadius*12, getHeight() - ballRadius*8,
                ballRadius*12, ballRadius*8, ballRadius*12, getHeight() - ballRadius*8
        };

        // Holes
        holes = new float[]{
                ballRadius*3, ballRadius*12,
                ballRadius, ballRadius*24,
                ballRadius*3, getHeight() - ballRadius,
                getWidth() - ballRadius, getHeight() - ballRadius,
                ballRadius*12, getHeight() - ballRadius*3,
                getWidth() - ballRadius*3, getHeight() - ballRadius*12,
                getWidth() - ballRadius, getHeight() - ballRadius*20,
                getWidth() - ballRadius*3, getHeight() - ballRadius*26,
                getWidth() - ballRadius, ballRadius*2,
                getWidth() - ballRadius*8, ballRadius*3,
        };
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
    }

    @Override
    public void run() {
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Move ball
        ballX += speedX;
        ballY += speedY;

        // Check board X bounds
        if (ballX > surfaceStopX) {
            ballX = surfaceStopX;
            speedX = speed0;
        }
        else if (ballX < surfaceStartX) {
            ballX = surfaceStartX;
            speedX = speed0;
        }

        // Check board Y bounds
        if (ballY > surfaceStopY) {
            ballY = surfaceStopY;
            speedY = speed0;
        }
        else if (ballY < surfaceStartY) {
            ballY = surfaceStartY;
            speedY = speed0;
        }

        // Check lines bounds
        for(int i = 0; i < lines.length; i = i + 4) {
            lineStartX = lines[i];
            lineStartY = lines[i + 1];
            lineStopX = lines[i + 2];
            lineStopY = lines[i + 3];

            if (lineStartX == lineStopX) { // vertical
                // Line bounds
                lineLeftX = lineStartX - ballRadius - lineThickness / 2f;
                lineRightX = lineStartX + ballRadius + lineThickness / 2f;
                lineTopY = lineStartY - ballRadius + lineThickness;
                lineBottomY = lineStopY + ballRadius - lineThickness;

                if (ballY > lineTopY && ballY < lineBottomY) {
                    if (ballX > lineLeftX && ballX < lineStartX) {
                        ballX = lineLeftX;
                        speedX = speed0;
                    } else if (ballX < lineRightX && ballX > lineStartX) {
                        ballX = lineRightX;
                        speedX = speed0;
                    }
                }
            } else if (lineStartY == lineStopY) { // horizontal
                // Line bounds
                lineTopY = lineStartY - ballRadius - lineThickness / 2f;
                lineBottomY = lineStartY + ballRadius + lineThickness / 2f;
                lineLeftX = lineStartX - ballRadius + lineThickness;
                lineRightX = lineStopX + ballRadius - lineThickness;

                if (ballX > lineLeftX && ballX < lineRightX) {
                    if (ballY > lineTopY && ballY < lineStartY) {
                        ballY = lineTopY;
                        speedY = speed0;
                    } else if (ballY < lineBottomY && ballY > lineStartY) {
                        ballY = lineBottomY;
                        speedY = speed0;
                    }
                }
            }
        }

        // Check holes and draw them
        for(int i = 0; i < holes.length; i = i + 2) {
            holeX = holes[i];
            holeY = holes[i + 1];
            holeBallDistance = (float)Math.sqrt(Math.pow(ballX - holeX, 2) + Math.pow(ballY - holeY, 2));
            if (holeBallDistance < ballRadius) {
                init();
            }

            canvas.drawCircle(holeX, holeY, ballRadius, holePaint);
        }

        // Draw lines
        canvas.drawLines(lines, linePaint);

        // Draw ball
        canvas.drawCircle(ballX, ballY, ballRadius, ballPaint);

        // Redraw after interval
        handler.postDelayed(this, interval);
    }

    public void addSpeedX(float speedX) {
        this.speedX += -speedX; // inverted
    }

    public void addSpeedY(float speedY) {
        this.speedY += speedY;
    }
}

