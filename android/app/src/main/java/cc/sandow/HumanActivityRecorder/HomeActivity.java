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
    SharedPreferences sharedPreferences;

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

        String msg = HARApplication.getLastMessage();
        if (msg != "") { // If I was offline and something happened
            txtServiceMessage.setText(msg);
            HARApplication.setLastMessage("");
        }
        txtName.setText(sharedPreferences.getString("subject_name", ""));
        txtMail.setText(sharedPreferences.getString("subject_email", ""));
        txtSubject.setText(sharedPreferences.getString("subject_id", ""));
        txtSessionID.setText(sharedPreferences.getString("session_id", ""));
        txtActivityID.setText(sharedPreferences.getString("activity_id", ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ExceptionHandler.register(this, "https://unibe.sandow.cc/exception.php");
        setContentView(R.layout.activity_home);
        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        HARApplication.setAppContext(this);
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

    private static int countLines(String str){
        String[] lines = str.split("\r\n|\r|\n");
        return  lines.length;
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
        if (countLines(txtServiceMessage.getText().toString()) >= 7) {
            txtServiceMessage.setText(event.message);
        } else {
            txtServiceMessage.append(event.message);
        }
        txtServiceMessage.append("\n");
    }

    // Activate Collector-Service
    private void moveToMeasurement() {
        // Clear Status Text
        txtServiceMessage.setText(String.format("Recording Scheduled for %ss\n",sharedPreferences.getString("activityDuration", "180")));
        ((HARApplication) this.getApplication()).setCollectorJobID(Util.scheduleJob(this));
    }
    private void moveToSettings() {
        Intent intent =  new Intent(HomeActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}