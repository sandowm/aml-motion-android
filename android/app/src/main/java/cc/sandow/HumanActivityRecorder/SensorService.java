package cc.sandow.HumanActivityRecorder;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class SensorService  extends Service implements SensorEventListener {
    private static final String DEBUG_TAG = "AccLoggerService";

    private SensorManager sensorManager = null;
    private Sensor sensorAcc, sensorGyr = null;
    public int accLine, gyrLine = 0;
    private static int MAXLINES=40000;
    private float[][] accData, gyrData;
    private JSONObject postData;
    SharedPreferences sharedPreferences;

    Integer versionCode = BuildConfig.VERSION_CODE;

    public SensorService() {
        // 40000 lines, with 6 measurements each
        accData = new float[MAXLINES][4];
        gyrData = new float[MAXLINES][4];
    }

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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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
            // Every 0-Element is a timestamp in ms since start of this
            accData[accLine][0] = (float) event.timestamp / 1000000;
            // grab the values
            StringBuilder sb = new StringBuilder();
            for (float value : event.values)
                sb.append(String.valueOf(value)).append(" | ");

            Log.d(DEBUG_TAG, "received sensor valures are: " + sb.toString());
            for (int i = 0; i < event.values.length; i++) {
                accData[accLine][1+i] = event.values[i];
            }
            accLine++;
            if (accLine % 50 == 0) EventBus.getDefault().post(new ServiceEvent(String.format("Got %d Acc Events",accLine)));
        } else {
            for (int i = 0; i < event.values.length; i++) {
                gyrData[gyrLine][1+i] = event.values[i];
            }
        }
        StringBuilder sb = new StringBuilder();
        for (float value : gyrData[gyrLine])
            sb.append(String.valueOf(value)).append(" | ");
        Log.i(DEBUG_TAG,"SensorService Sensor changed: " + sb.toString());
        // Give User Feedback
        if (gyrLine >= 100 || accLine >= 400) {
            sendData();
            sensorManager.unregisterListener(this);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

    public void sendData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://unibe.sandow.cc/my-university.php";
        EventBus.getDefault().post(new ServiceEvent(getString(R.string.sendStatus_sending,url)));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, prepareData(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                EventBus.getDefault().post(new ServiceEvent(getString(R.string.sendStatus_result, response.toString())));
                stopSelf();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                stopSelf();
            }
        });

        queue.add(jsonObjectRequest);
    }

    @Override
    public void onDestroy() {
        Log.i(DEBUG_TAG,"Beeing Shutdown");
        sensorManager.unregisterListener(this);
        super.onDestroy();
    }
}
