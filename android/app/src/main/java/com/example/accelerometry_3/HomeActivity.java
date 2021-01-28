package com.example.accelerometry_3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    String name, email, SubjectNumber;

    EditText edtTxtName, edtTxtMail, edtTxtNumber;

    TextView txtName, txtMail, txtSubject;

    Button btnSubmit, btnToAccGyr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Define the text input fields
        edtTxtName = (EditText) findViewById(R.id.edtTxtName);
        edtTxtMail = (EditText) findViewById(R.id.edtTxtMail);
        edtTxtNumber = (EditText) findViewById(R.id.edtTxtNumber);

        //Define the text views
        txtName = (TextView) findViewById(R.id.txtName);
        txtMail = (TextView) findViewById(R.id.txtMail);
        txtSubject = (TextView) findViewById(R.id.txtSubject);

        //define the button to submit the data and define what it does when clicked
        btnSubmit =  (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = edtTxtName.getText().toString();
                email = edtTxtMail.getText().toString();
                SubjectNumber = edtTxtNumber.getText().toString();
                txtName.setText(name);
                txtMail.setText(email);
                txtSubject.setText(SubjectNumber);

            }
        });


        //Define the Button to the next page and define what it does when clicked
        btnToAccGyr = (Button)findViewById(R.id.btnToAccGyr);
        btnToAccGyr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToMeasurement();
            }
        });
    }

    //function to move to the next page
    private void moveToMeasurement(){
        Intent intent =  new Intent(HomeActivity.this, measurement.class);
        startActivity(intent);
    }
}