package com.example.donttouchmytail.framework.impl;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class CompassHandler implements SensorEventListener {

    float _yaw;
    float _pitch;
    float _roll;

    /**
     * Sensor.TYPE_ORIENTATION used in the book is deprecated. Pulled an example from
     * http://www.codingforandroid.com/2011/01/using-orientation-sensors-simple.html instead.
     * 
     * @param context
     */
    public CompassHandler(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0 && sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).size() != 0) {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float[] gravity = null;
        float[] geoMagnetic = null;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // http://stackoverflow.com/a/13728251/617044
            gravity = event.values.clone();
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            // http://stackoverflow.com/a/13728251/617044
            geoMagnetic = event.values.clone();
        }
        if (gravity != null && geoMagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            if (SensorManager.getRotationMatrix(R, I, gravity, geoMagnetic)) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                _yaw = orientation[0];
                // Just for safe keeping ... to get a heading between -180 and 180 do the following: 
                // _yaw = (float) Math.toDegrees(orientation[0]);
                _pitch = orientation[1];
                _roll = orientation[2];
            }
        }
    }

    public float getYaw() {
        return _yaw;
    }

    public float getPitch() {
        return _pitch;
    }

    public float getRoll() {
        return _roll;
    }

}
