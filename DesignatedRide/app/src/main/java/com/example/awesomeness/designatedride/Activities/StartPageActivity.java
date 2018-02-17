package com.example.awesomeness.designatedride.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.awesomeness.designatedride.R;

public class StartPageActivity extends AppCompatActivity {

    private TextView loginLink, registerLink;
    private final int ACCESS_FINE_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        initWidgets();

        ActivityCompat.requestPermissions(StartPageActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_FINE_LOCATION);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(LoginActivity.class);
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(RegisterActivity.class);
            }
        });

    }

    private void initWidgets() {
        loginLink = (TextView) findViewById(R.id.linkToLoginPage_start);
        registerLink = (TextView) findViewById(R.id.linkToRegisterPage_start);
    }

    private void gotoActivity(Class activityClass) {
        startActivity(new Intent(StartPageActivity.this, activityClass));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(!(grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            finish();
        }
    }

}
