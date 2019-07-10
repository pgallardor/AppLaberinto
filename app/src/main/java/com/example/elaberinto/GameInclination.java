package com.example.elaberinto;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.util.Pair;

public class GameInclination implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private float[] rotationValues = new float[4];
    private double maximumRotation;
    private double minimumRotation;


    public GameInclination(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        rotationValues[0] = 0;
        rotationValues[1] = 0;
        rotationValues[2] = 0;
        rotationValues[3] = 0;

        maximumRotation = rotationSensor.getMaximumRange();
        minimumRotation = - maximumRotation;
        //register listener when we create this
        //check updates every 10ms
        sensorManager.registerListener(this, rotationSensor, 10000);
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            rotationValues = event.values;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    public Pair<Double, Double> getAcceleration(){
        Pair<Double, Double> accel = new Pair<>((double)rotationValues[0],
                (double)rotationValues[1]);
        //should map them to -1 and 1 before returning them
        Log.i("SENSOR", "MAX RANGE: "+ maximumRotation);
        return accel;
    }

    public void close(){
        sensorManager.unregisterListener(this);
    }
}
