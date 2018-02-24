package com.example.awesomeness.designatedride._DriverActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.awesomeness.designatedride.R;
import com.example.awesomeness.designatedride.Util.Constants;

public class DriverProfileActivity extends AppCompatActivity {

    //----Widgets---
    private EditText firstNameET;
    private EditText lastNameET;
    private EditText emailET;
    private EditText passwordET;
    private EditText verifyPwdET;
    private Button updateProfileBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        Intent intent = getIntent();
        String userid = intent.getStringExtra(Constants.INTENT_KEY);
        Toast.makeText(this, userid + "\nDriver Profile", Toast.LENGTH_LONG).show();

        /**
         * Todo: populate editText with the user info using the userId that was passed by intent.
         */

        initWidgets();

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity();
            }
        });

    }

    //--------------------------------------------------------------------------
    private void initWidgets() {

        firstNameET = (EditText) findViewById(R.id.firstNameET_drUpdate);
        lastNameET = (EditText) findViewById(R.id.lastNameET_drUpdate);
        emailET = (EditText) findViewById(R.id.emailET_drUpdate);
        passwordET = (EditText) findViewById(R.id.passwordET_drUpdate);
        verifyPwdET = (EditText) findViewById(R.id.verifyPwdET_drUpdate);
        updateProfileBtn = (Button) findViewById(R.id.updateProfileBtn_drUpdate);

    }

    private void gotoActivity() {
        startActivity(new Intent(DriverProfileActivity.this, DriverActivity.class));
        finish();
    }

}
