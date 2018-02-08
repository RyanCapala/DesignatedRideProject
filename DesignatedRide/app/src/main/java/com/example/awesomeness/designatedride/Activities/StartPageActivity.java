package com.example.awesomeness.designatedride.Activities;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        initWidgets();


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
}
