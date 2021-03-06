package cc.sandow.HumanActivityRecorder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;


public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    Integer versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;
    TextView txtVersion;
    Intent nextIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtVersion = (TextView) findViewById(R.id.txtVersion);
        txtVersion.setText(versionName + " (" + versionCode.toString() + ")");
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPreferences.getString("subject_name", "") == "") {
                    nextIntent = new Intent(MainActivity.this, SettingsActivity.class);
                } else {
                    nextIntent = new Intent(MainActivity.this, HomeActivity.class);
                }
                startActivity(nextIntent);
                finish();
            }
        }, SPLASH_TIME_OUT);



    }
}