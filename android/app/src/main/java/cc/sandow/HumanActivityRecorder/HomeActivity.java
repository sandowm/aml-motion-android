package cc.sandow.HumanActivityRecorder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class HomeActivity extends AppCompatActivity {
    String name, email, SubjectNumber;

    TextView txtName, txtMail, txtSubject,txtSessionID,txtActivityID, txtServiceMessage;

    ImageButton btnToAccGyr;
    ImageButton btnSettings;
    @Override
    protected void onPostResume() {
        super.onPostResume();
        //Define the text views
        txtName = (TextView) findViewById(R.id.txtName);
        txtMail = (TextView) findViewById(R.id.txtMail);
        txtSubject = (TextView) findViewById(R.id.txtSubject);
        txtSessionID = (TextView) findViewById(R.id.txtSessionID);
        txtActivityID = (TextView) findViewById(R.id.txtActivityID);
        txtServiceMessage = (TextView) findViewById(R.id.txtServiceMessage);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        txtName.setText(sharedPreferences.getString("subject_name", ""));
        txtMail.setText(sharedPreferences.getString("subject_email", ""));
        txtSubject.setText(sharedPreferences.getString("subject_id", ""));
        txtSessionID.setText(sharedPreferences.getString("session_id", ""));
        txtActivityID.setText(sharedPreferences.getString("activity_id", ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Define the Button to the next page and define what it does when clicked
        btnToAccGyr = (ImageButton)findViewById(R.id.btnPlay);
        btnToAccGyr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToMeasurement();
            }
        });
        btnSettings = (ImageButton)findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToSettings();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ServiceEvent event) {
        // Put the server response on the UI
        txtServiceMessage.setText(event.message);
    }
    // Activate Collector-Service
    private void moveToMeasurement() {
        // Clear Status Text
        txtServiceMessage.setText("Started Activity");
        Intent intent = new Intent(getApplicationContext(), SensorService.class );
        startService(intent);
    }
    private void moveToSettings() {
        Intent intent =  new Intent(HomeActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}