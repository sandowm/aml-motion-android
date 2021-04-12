package cc.sandow.HumanActivityRecorder;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Random;

import static cc.sandow.HumanActivityRecorder.Util.sendToUI;

public class SensorService  extends Service implements SensorEventListener {
    private static final String LOG_TAG = "AccLoggerService";
    private SensorManager sensorManager = null;
    private Sensor sensorAcc, sensorGyr = null;
    public int accLine, gyrLine, multiPartIndex = 0;
    private static int MAXLINES=60000;
    private static int expectedDelay = 20000;  // 20ms Sensor Delay == 50 Hz
    private float[][] accData, gyrData;
    private JSONObject postData;
    SharedPreferences sharedPreferences;
    Double maxSensorTimestamp = Double.MAX_VALUE;
    private Integer serviceInstance = new Random().nextInt(); // With this multiple parts can be reconsolidated on the server
    Boolean collectionFinished = Boolean.FALSE;
    Integer versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;
    long startTimestamp = Long.MAX_VALUE;
    Double durationns;


    public SensorService() {
        // 60000 lines, with 6 measurements each
        accData = new float[MAXLINES][4];
        gyrData = new float[MAXLINES][4];
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //ExceptionHandler.register(this, "https://unibe.sandow.cc/exception.php");
        Util.createNotificationChannel();
        Util.signalStatusRecording(this,0);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAcc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorGyr = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, sensorAcc,
                expectedDelay);
        sensorManager.registerListener(this, sensorGyr,
                expectedDelay);
        Log.d(LOG_TAG,"SensorService StartCommand received");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        durationns = Float.parseFloat(sharedPreferences.getString("activityDuration","180")) * Math.pow(10,9);
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
        // Happens only the first time, this is called
        if (maxSensorTimestamp == Double.MAX_VALUE) {
            maxSensorTimestamp = event.timestamp + durationns;
            startTimestamp = event.timestamp;
        }
        Log.d(LOG_TAG,String.format("Event ts: %d, durationns: %f, max ts: %f",event.timestamp, durationns,maxSensorTimestamp));
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Every 0-Element is a timestamp in ms since start of this
            accData[accLine][0] = (float) event.timestamp / 1000000;  // make it milliseconds
            // grab the values
            StringBuilder sb = new StringBuilder();
            for (float value : event.values)
                sb.append(String.valueOf(value)).append(" | ");

            Log.d(LOG_TAG, "received sensor values are: " + sb.toString());
            for (int i = 0; i < event.values.length; i++) {
                accData[accLine][1+i] = event.values[i];
            }
            accLine++;
            if (accLine % 50 == 0 ) {
                sendToUI("M", String.format("%d Acc, %d Gyr Events",accLine,gyrLine));
                Util.signalStatusRecording(this, (int) ((event.timestamp - startTimestamp) / 1000000000));
            }
        } else {
            // Every 0-Element is a timestamp in ms since start of this
            gyrData[gyrLine][0] = (float) event.timestamp / 1000000;  // make it milliseconds
            for (int i = 0; i < event.values.length; i++) {
                gyrData[gyrLine][1+i] = event.values[i];
            }
            gyrLine++;
        }
        // This ends the service
        if (maxSensorTimestamp < event.timestamp) {
            Log.i(LOG_TAG,String.format("Ending SensorService at maxTimestamp: %f, event.timestamp: %d",maxSensorTimestamp,event.timestamp));
            sendData();
            Util.unschedule(this, ((HARApplication) this.getApplication()).getCollectorJobID());
            Util.signalStoppedRecording(this);
            sensorManager.unregisterListener(this);
            collectionFinished = Boolean.TRUE;
        }
        if (accLine >= MAXLINES || gyrLine >= MAXLINES) {
            sendData();
            // Data is copied before sending, so we can safely reset the indices
            accLine = 0;
            gyrLine = 0;
            multiPartIndex ++;
        }
    }

    public JSONObject prepareData() {
        JSONObject postData = new JSONObject();
        try {
            Gson gson = new Gson();
            if (accLine >0) postData.put("acc", new JSONArray(gson.toJson(Arrays.copyOfRange(accData, 0, accLine - 1))));
            if (gyrLine >0) postData.put("gyr", new JSONArray(gson.toJson(Arrays.copyOfRange(gyrData, 0, gyrLine - 1))));
            postData.put("appVersionCode", versionCode.toString());
            postData.put("subjectID", sharedPreferences.getString("subject_id", ""));
            postData.put("subjectName", sharedPreferences.getString("subject_name", ""));
            postData.put("subjectEMail", sharedPreferences.getString("subject_email", ""));
            postData.put("sessionID", sharedPreferences.getString("session_id", ""));
            postData.put("activityID", sharedPreferences.getString("activity_id", ""));
            postData.put("nominalDurationSeconds",sharedPreferences.getString("activityDuration", "0"));
            postData.put("multiPartIndex", multiPartIndex);
            postData.put("phoneModel", Build.MODEL);
            postData.put("serviceInstance", serviceInstance);
            postData.put("appVersion",versionName + " (" + versionCode.toString() + ")");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

    public void sendData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = sharedPreferences.getString("url", "https://unibe.sandow.cc/my-university.php");
        sendToUI("M", getString(R.string.sendStatus_sending,url));

        JSONObject sendableData = prepareData();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, sendableData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                sendToUI("M", getString(R.string.sendStatus_result, response.toString()));
                if (collectionFinished) stopSelf();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (collectionFinished) stopSelf();
            }
        });
        queue.add(jsonObjectRequest);
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG,"Beeing Shutdown");
        sensorManager.unregisterListener(this);
        super.onDestroy();
    }
}
