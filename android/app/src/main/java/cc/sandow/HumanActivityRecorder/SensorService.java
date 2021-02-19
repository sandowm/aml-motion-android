package cc.sandow.HumanActivityRecorder;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class SensorService  extends Service implements SensorEventListener {
    private static final String DEBUG_TAG = "AccLoggerService";

    private SensorManager sensorManager = null;
    private Sensor sensorAcc, sensorGyr = null;
    public int dataLine = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAcc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorGyr = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, sensorAcc,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorGyr,
                SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(DEBUG_TAG,"SensorService StartCommand received");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // grab the values
            StringBuilder sb = new StringBuilder();
            for (float value : event.values)
                sb.append(String.valueOf(value)).append(" | ");

            Log.d(DEBUG_TAG, "received sensor valures are: " + sb.toString());
            for (int i = 0; i < event.values.length; i++) {
                measurement.sendeableArray[dataLine][i] = event.values[i];
            }
            dataLine++;
        } else {
            for (int i = 0; i < event.values.length; i++) {
                measurement.sendeableArray[dataLine][2+i] = event.values[i];
            }
        }
        StringBuilder sb = new StringBuilder();
        for (float value : measurement.sendeableArray[dataLine])
            sb.append(String.valueOf(value)).append(" | ");
        Log.i(DEBUG_TAG,"SensorService Sensor changed: " + sb.toString());
        // grab the values and timestamp
        //...
        // stop the sensor and service
        if (dataLine >= 100) {
        sensorManager.unregisterListener(this);
        stopSelf(); }
    }

}
