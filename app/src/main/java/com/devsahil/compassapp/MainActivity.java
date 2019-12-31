package com.devsahil.compassapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Float azimuthAngle;
    private SensorManager compassSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private TextView tv_degrees;
    private ImageView iv_compass;
    private float currentDegree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compassSensorManager= (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = compassSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = compassSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    @Override
    protected void onResume() {
        super.onResume();
        compassSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        compassSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        compassSensorManager.unregisterListener(this);
    }

    private float[] accelRead;
    private float[] magneticRead;

    @Override
    public void onSensorChanged(SensorEvent event) {
        tv_degrees = (TextView) findViewById(R.id.tv_degrees);
        iv_compass = (ImageView) findViewById(R.id.iv_compass);
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accelRead = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            magneticRead = event.values;

        if (accelRead != null && magneticRead != null){
            float R[] = new float[9];
            float I[] = new float[9];
            boolean successfulRead = SensorManager.getRotationMatrix(R, I, accelRead, magneticRead);

            if (successfulRead){
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuthAngle = orientation[0];
                float degrees = (azimuthAngle * 180f) / 3.14f;
                int intDegress = Math.round(degrees);
                tv_degrees.setText(Integer.toString(intDegress) + (char) 0x00B0 + " to absolute north.");

                RotateAnimation rotate = new
                        RotateAnimation(currentDegree, - intDegress,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(100);
                rotate.setFillAfter(true);
                iv_compass.startAnimation(rotate);
                currentDegree = -intDegress;
            }


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
