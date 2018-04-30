package com.example.awesomeness.designatedride._DriverActivities;

import android.content.Intent;
import android.media.tv.TvContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.awesomeness.designatedride.R;

public class DriverAvailableActivity extends AppCompatActivity {

    private SwitchCompat available_toggle_switch, accept_toggle_switch;
    private ImageButton setTime_btn, save_btn, cancel_btn;
    private EditText radius_ET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_available);

        initWidgets();

        if (!available_toggle_switch.isChecked()) {
            Toast.makeText(this, "Availability is On", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Availability is Off", Toast.LENGTH_SHORT).show();
        }

        setTime_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(DriverActivity.class);
            }
        });
    }

    private void initWidgets() {
        available_toggle_switch = (SwitchCompat) findViewById(R.id.drvAvl_toggle_available);
        accept_toggle_switch = (SwitchCompat) findViewById(R.id.drvAvl_toggle_accept);
        setTime_btn = (ImageButton) findViewById(R.id.drvAvl_set_btn);
        save_btn= (ImageButton) findViewById(R.id.drvAvl_save_btn);
        cancel_btn = (ImageButton) findViewById(R.id.drvAvl_cancel_btn);
        radius_ET = (EditText) findViewById(R.id.drvAvl_radius_ET);

    }

    private void gotoActivity(Class activityClass) {
        startActivity(new Intent(DriverAvailableActivity.this, activityClass));
        finish();
    }
}
