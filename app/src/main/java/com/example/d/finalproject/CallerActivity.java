package com.example.d.finalproject;

import android.content.Context;
import android.content.Intent;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.ContentValues.TAG;

public class CallerActivity extends AppCompatActivity implements SensorEventListener {
    TextView callPhoneNo;
    TextView test;
    TextView timer;
    TextView currInput;
    Float maxVisiblePoint = 100f;
    Float calibration = 0f;
    Float threshold = 999999f;
    List<Float> values = new ArrayList<Float>();
    Integer num_values = 40;
    String number;
    TelephonyManager mTelephonyManager;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (mSensorManager != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        // Initialise Flags
        over = -1;
        number = "";
        currBinary = "";
        troughStart = -1f;
        troughEnd = -1f;
        lastTrough = -1f;

        callPhoneNo = (TextView) findViewById(R.id.phoneNo);
        test = (TextView) findViewById(R.id.test);

    }

    float troughStart;
    float troughEnd;
    float lastTrough;
    String currBinary;
    int over;
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            values.add(event.values[0]);
            if (values.size() == num_values) {
                List<Float> sorted = new ArrayList<>(values);
                Collections.sort(sorted);
                Float sum = 0f;
                for (int i = num_values/10; i < 9 *num_values/10; i++) {
                    sum += sorted.get(i);
                }
                calibration = sum / (4 * num_values /5);
                maxVisiblePoint = calibration* 1.5f;
                threshold = calibration / 2;
                over = 1;
            }
            if (threshold == 999999) {
                test.setText("Calibrating...");
            }
            else {
                test.setText("Current:         " + event.values[0]+ "\n" +
                             "Threshold:      " + threshold  + "\n" +
                             "Current input: " + currBinary);
            }

            if (event.values[0] < threshold && over == 1) {
                over = 0;
                troughStart = event.timestamp/1000000L;
            }
            if (event.values[0] > threshold && over == 0) {
                over = 1;
                troughEnd = event.timestamp/1000000L;
                lastTrough = event.timestamp/1000000L;
                if (troughStart != -1) {
                    // Concatenate "1"
                    if (troughEnd - troughStart < 1000L) {
                        currBinary += "1";
                    }
                    // Concatenate "0"
                    else if (troughEnd - troughStart > 1000L) {
                        currBinary += "0";
                    }
                }

            }
            /*
            if (event.values[0] < threshold && over == 0 && troughStart != -1) {
                timer.setText("");
                Float time = event.timestamp/1000000000L - troughStart/1000L;
                if (time < 1) {
                    currInput.setText("1");
                }
                else if (time < 3){
                    currInput.setText("0");
                }
            }
            else {
                currInput.setText("");
            }

            if (event.values[0] > threshold && over == 1 && troughEnd != -1) {
                Float time = 5 - (event.timestamp/1000000000L - troughStart/1000L);
                if (time == Math.ceil(time)) {
                    if (time >= 0) {
                        timer.setText("" + time);
                    } else {
                        timer.setText("");
                    }
                }
            }
            */
            if (event.timestamp/1000000L - troughStart > 3000L && over == 0 && number.length() > 0) {
                call();
            }

            // Concatenate Binary Number with next "1" or "0"
            if (currBinary.length() != 0 && event.timestamp/1000000 - lastTrough > 5000 && over == 1) {
                int nextDigit = Integer.parseInt(currBinary, 2);
                if (nextDigit == 15) {
                    if (number.length() > 0) {
                        number = number.substring(0, number.length() - 1);
                        callPhoneNo.setText(number);
                    }
                }
                else if (nextDigit == 31) {
                    number = "";
                    callPhoneNo.setText(number);
                }
                else if (nextDigit < 10){
                    number += nextDigit;
                    callPhoneNo.setText(number);
                }
                currBinary = "";
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    public void call() {
        // Use format with "tel:" and phone number to create phoneNumber.
        String phoneNumber = String.format("tel: %s", callPhoneNo.getText().toString());
        // Create the intent.
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        // Set the data for the intent as the phone number.
        dialIntent.setData(Uri.parse(phoneNumber));
        // If package resolves to an app, send intent.
        if (dialIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(dialIntent);
        } else {
            Log.e(TAG, "Can't resolve app for ACTION_DIAL Intent.");
        }
    }
}