package cc.sandow.HumanActivityRecorder;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileWriter;


public class measurement extends AppCompatActivity implements SensorEventListener{
    //name all the elements of the page
    TextView x_txt, y_txt, z_txt, txtTitleAcc;
    TextView x_txt_g, y_txt_g, z_txt_g, txtTitleGyro, len_txt;

    ImageButton btnStartRecording, btnStopRecording, btnSaveData, btnSettings;

    private Sensor Accelerometer;
    private Sensor Gyroscope;

    private Accelerometer accelerometer;
    private Gyroscope gyroscope;

    private FileWriter writer;

    private static int MAXLINES=40000;
    public static float[][] sendeableArray;
    public int dataLine;

    public measurement() {
        // 40000 lines, with 6 measurements each
        sendeableArray = new float[MAXLINES][6];
        dataLine = 0;
    }

    public static float[][] getSendeableArray() {
        return sendeableArray;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);

        //set the java classes for Acc and gyro
        accelerometer = new Accelerometer(this);
        gyroscope =  new Gyroscope(this);

        //set the Listener for accelerometer sensor change and define the string that are input
        accelerometer.setListener(new Accelerometer.Listener() {
            @Override
            public void onTranslation(float tx, float ty, float tz) {
                x_txt.setText("X: " + tx);
                y_txt.setText("Y: " + ty);
                z_txt.setText("Z: " + tz);
                sendeableArray[dataLine][0]=tx;
                sendeableArray[dataLine][1]=ty;
                sendeableArray[dataLine][2]=tz;
                dataLine += 1;
                len_txt.setText("Array "+dataLine);
                // TODO overrun handling needs to be better
                if (dataLine >MAXLINES) dataLine = 0;
                // TODO Just for testing barometer
                if (sendeableArray.length >= 7) {
                z_txt_g.setText("Zp: " + sendeableArray[7][4]);}
            }
        });

        //set the Listener for gyroscpoe sensor change and define the string that are input
        gyroscope.setListener(new Gyroscope.Listener() {
            @Override
            public void onRotation(float rx, float ry, float rz) {
                x_txt_g.setText("X: " + rx);
                y_txt_g.setText("Y: " + ry);
                //z_txt_g.setText("Z: " + rz);
                sendeableArray[dataLine][3]=rx;
                sendeableArray[dataLine][4]=ry;
                sendeableArray[dataLine][5]=rz;
            }
        });

        //Assign TextViews Accelerometer
        x_txt = (TextView)findViewById(R.id.x_txt);
        y_txt = (TextView)findViewById(R.id.y_txt);
        z_txt = (TextView)findViewById(R.id.z_txt);

        len_txt = (TextView)findViewById(R.id.len_txt);

        //Assign TextViews Gyro
        x_txt_g = (TextView)findViewById(R.id.x_txt_g);
        y_txt_g = (TextView)findViewById(R.id.y_txt_g);
        z_txt_g = (TextView)findViewById(R.id.z_txt_g);

        //Define the buttons
        btnStartRecording =  findViewById(R.id.btnStart);
        btnStopRecording  = findViewById(R.id.btnPause);
        btnSaveData = findViewById(R.id.btnSave);

        //Define what happens when the buttons are clicked
        btnStartRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartClick(); //function see below
            }
        });

        btnStopRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStopClick(); //function see below
            }
        });

        btnSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSendPage(); //function see below
            }
        });
        btnSettings = (ImageButton)findViewById(R.id.btnSettings2);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(measurement.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

    }

    //Function to go to the next page
    private void goToSendPage() {
        Intent intent =  new Intent(measurement.this, SendDataPage.class);
        startActivity(intent);
    }

    //functions to start and stop data writing and to send the data to the server
    private void onStartClick() {
        onResume();
    }

    private void onStopClick() {
        onPause();
    }

    //functions for Data writing
    @Override
    protected void onResume(){
        super.onResume();

        accelerometer.register();
        gyroscope.register();
        Intent intent = new Intent(getApplicationContext(), SensorService.class );
        startService(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = new Intent(getApplicationContext(), SensorService.class );
        stopService(intent);

        accelerometer.unregister();
        gyroscope.unregister();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not in use

    }
}