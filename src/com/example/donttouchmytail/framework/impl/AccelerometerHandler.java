package com.example.donttouchmytail.framework.impl;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AccelerometerHandler implements SensorEventListener {

    float _accelX;
    float _accelY;
    float _accelZ;

    public AccelerometerHandler(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
            Sensor accelerometer = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        _accelX = event.values[0];
        _accelY = event.values[1];
        _accelZ = event.values[2];
    }

    public float getAccelX() {
        return _accelX;
    }

    public float getAccelY() {
        return _accelY;
    }

    public float getAccelZ() {
        return _accelZ;
    }

}
