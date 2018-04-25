package com.example.awesomeness.designatedride._RiderActivities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.awesomeness.designatedride.R;

public class RiderEditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";

    private Context ctx = RiderEditProfileActivity.this;
    private EditText fname_et, lname_et;
    private String birthMonth_str, birthDay_str, birthYear_str;
    private EditText streetAddress_et, cityAndState_et, email_et, v_email_et, phone_et;
    private EditText insurance_et, doctorsname_et, username_et;
    private String wheelChairAccess;
    private ImageButton saveBtn, cancelBtn;
    private RadioGroup radioGroup;
    private RadioButton yes_radioBtn, no_radioBtn;
    private Spinner monthSpinner, daySpinner, yearSpinner;

    private String[] monthArray, dayArray, yearArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_edit_profile);

        initWidgets();
        Log.d(TAG, "onCreate: ");

        monthArray = getResources().getStringArray(R.array.month);
        dayArray = getResources().getStringArray(R.array.day);
        yearArray = getResources().getStringArray(R.array.year);
        getSpinnerValues();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + birthMonth_str);
                Log.d(TAG, "onClick: " + birthDay_str);
                Log.d(TAG, "onClick: " + birthYear_str);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(RiderProfileActivity.class);
            }
        });
    }

    private void saveUpdatedInfo() {
        //Todo: get data from edit text and store to Firebase.
    }

    private void getSpinnerValues() {
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                birthMonth_str = monthArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                birthDay_str = dayArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                birthYear_str = yearArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initWidgets() {
        fname_et = (EditText) findViewById(R.id.fname_et_editProf);
        lname_et = (EditText) findViewById(R.id.lname_et_editProf);
        streetAddress_et = (EditText) findViewById(R.id.address_st_et_editProf);
        cityAndState_et = (EditText) findViewById(R.id.address_ctAndState_et_editProf);
        email_et = (EditText) findViewById(R.id.email_et_editProf);
        v_email_et = (EditText) findViewById(R.id.verify_email_et_editProf);
        phone_et = (EditText) findViewById(R.id.phone_et_editProf);
        insurance_et = (EditText) findViewById(R.id.insurance_et_editProf);
        doctorsname_et = (EditText) findViewById(R.id.doctor_et_editProf);
        username_et = (EditText) findViewById(R.id.username_et_editProf);
        saveBtn = (ImageButton) findViewById(R.id.save_btn_editProf);
        cancelBtn = (ImageButton) findViewById(R.id.cancel_btn_editProf);
        radioGroup = (RadioGroup) findViewById(R.id.wheelchair_radiogroup_editProf);
        yes_radioBtn = (RadioButton) findViewById(R.id.yes_radioBtn_editProf);
        no_radioBtn = (RadioButton) findViewById(R.id.no_radioBtn_editProf);
        monthSpinner = (Spinner) findViewById(R.id.month_spinner_editProf);
        daySpinner = (Spinner) findViewById(R.id.day_spinner_editProf);
        yearSpinner = (Spinner) findViewById(R.id.year_spinner_editProf);

        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(ctx, R.array
                        .month,
                android.R.layout.simple_spinner_dropdown_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(ctx, R.array.day,
                android.R.layout.simple_spinner_dropdown_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);

        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(ctx, R.array.year,
                android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);




    }

    private void goToActivity(Class activityClass) {
        startActivity(new Intent(ctx, activityClass));
        finish();
    }
}
