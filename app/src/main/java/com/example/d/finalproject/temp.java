/*package com.example.d.finalproject;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;
    private TextView textView;
    private TextView troughs;

    Float maxVisiblePoint = 100f;
    Float calibration = 0f;
    List<Float> values = new ArrayList<Float>();
    List<Long> times = new ArrayList<Long>();
    Integer num_values = 70;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (mSensorManager != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        // Initialise Text Properties
        textView = (TextView) findViewById(R.id.text);
        troughs = (TextView) findViewById(R.id.troughs);
        textView.setTextColor(Color.BLACK);

        // Chart Properties
        mChart = new LineChart(this);
        mChart = (LineChart) findViewById(R.id.chart);
        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setText("Light Sensor Plot");
        mChart.setTouchEnabled(false);
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(false);
        mChart.setGridBackgroundColor(Color.BLACK);
        mChart.setBackgroundColor(Color.rgb(229,239,249));
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);
        // Legent Formatting
        Legend legend = mChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(Color.BLACK);
        // X Axis Formatting
        XAxis xAxis = mChart.getXAxis();
        xAxis.setDrawLabels(false);
        xAxis.setDrawGridLines(true);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setEnabled(true);
        // Y Axis Formatting
        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setTextColor(Color.BLACK);
        yAxis.setDrawGridLines(false);
        yAxis.setAxisMaximum(1000f);
        yAxis.setAxisMinimum(0f);
        yAxis.setDrawGridLines(true);
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        // Line Formatting
        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);
        mChart.setData(data);
        startPlot();
    }

    private void startPlot() {
        if (thread != null) {
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try {
                        Thread.sleep(10);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(MainActivity.this);
        thread.interrupt();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            textView.setText("" + event.values[0]);
        }

        if(plotData){
            addEntry(event);
            plotData=false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, mSensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (thread != null){
            thread.interrupt();
        }
        mSensorManager.unregisterListener(this);
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Luminosity");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(2f);
        set.setColor(Color.BLUE);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        set.setDrawCircles(false);
        set.setDrawValues(false);
        return set;
    }

    private void addEntry(SensorEvent event) {
        LineData data = mChart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            int i;
            if (set == null){
                set = createSet();
                data.addDataSet(set);
            }

            else if (data.getEntryCount() == num_values ) {
                List<Float> sorted = new ArrayList<>(values);
                Collections.sort(sorted);
                Float sum = 0f;
                for (i = num_values/10; i < 9 *num_values/10; i++) {
                    sum += sorted.get(i);
                }
                calibration = sum / (4 * num_values /5);
                maxVisiblePoint = calibration* 1.5f;
            }
            values.add(event.values[0]);
            times.add(event.timestamp/1000000L);
            data.addEntry(new Entry(set.getEntryCount(), event.values[0] + 5), 0);
            data.notifyDataChanged();

            mChart.notifyDataSetChanged();
            mChart.moveViewToX(data.getEntryCount());
            YAxis leftAxis = mChart.getAxisLeft();
            leftAxis.setAxisMaximum(maxVisiblePoint);

            getCount(event);

        }

    }

    private void getCount(SensorEvent event) {
        long startTime = event.timestamp/1000000L - 5000L;
        int count = 0;
        float threshold = calibration*3/10;
        for (long time : times) {
            if (time >= startTime) {
                count++;
            }
        }
        mChart.setVisibleXRangeMaximum(count);
        int over = 1;
        int troughCount = 0;
        for (int i = values.size() - count; i < values.size() ; i++) {
            if (values.get(i) < threshold && over == 1) {
                troughCount++;
                over = 0;
            }
            if (values.get(i) > threshold && over == 0) {
                troughCount++;
                over = 1;
            }
        }
        troughs.setText("Trough Count: " + Integer.toString((troughCount + 1)/2));
    }





}*/