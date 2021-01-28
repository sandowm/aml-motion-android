package com.example.accelerometry_3;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SendDataPage extends AppCompatActivity {
    TextView txtSuccess;
    private final ExternalData externalData;
    ExecutorService executorService = Executors.newFixedThreadPool(4);

    public SendDataPage(){
        externalData = new ExternalData(executorService);
    }
    private TextView status_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_data_page);
        status_txt = (TextView)findViewById(R.id.status);
        status_txt.setText(getString(R.string.sendStatus_sending,"unknown"));
        externalData.sendData(measurement.getSendeableArray(), new RepositoryCallback<HttpResult>() {
            @Override
            public void onComplete(Result<HttpResult> result) {
                if (result instanceof Result.Success) {
                    status_txt.setText(getString(R.string.sendStatus_sending,((Result.Success<HttpResult>) result).data));
                } else {
                    status_txt.setText(getString(R.string.sendStatus_sending,"Error"));
                }
            }
        });
    }




}