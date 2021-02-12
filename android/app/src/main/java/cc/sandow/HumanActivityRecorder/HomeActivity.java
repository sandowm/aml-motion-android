package cc.sandow.HumanActivityRecorder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class HomeActivity extends AppCompatActivity {
    String name, email, SubjectNumber;

    TextView txtName, txtMail, txtSubject,txtSessionID,txtActivityID;

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

    //function to move to the next page
    private void moveToMeasurement(){
        Intent intent =  new Intent(HomeActivity.this, measurement.class);
        startActivity(intent);
    }
    private void moveToSettings(){
        Intent intent =  new Intent(HomeActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}