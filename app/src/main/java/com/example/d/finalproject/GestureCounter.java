package com.example.d.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

import org.w3c.dom.Text;

public class GestureCounter extends Activity{

    private GestureDetectorCompat gestureObject;
    private TextView title;
    private TextView count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture_counter);

        // Initialise Text Properties
        title = (TextView) findViewById(R.id.gesture_counter_title);
        title.setTextColor(Color.BLACK);
        count = (TextView) findViewById(R.id.gesture_count);
        count.setTextColor(Color.BLACK);

        gestureObject = new GestureDetectorCompat(this, new LearnGesture());

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class LearnGesture extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // left to right swipe
            if (e2.getX() > e1.getX()) {
                Intent intent = new Intent(
                        GestureCounter.this, MainActivity.class);

                // finish() stops "history" for MainActivity class
                finish();

                startActivity(intent);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
/*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class LearnGesture extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // right to left swipe
            if (e2.getX() < e1.getX()) {
                Intent intent = new Intent(
                        MainActivity.this, GestureCounter.class);

                // finish() stops "history" for MainActivity class
                finish();

                startActivity(intent);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
*/