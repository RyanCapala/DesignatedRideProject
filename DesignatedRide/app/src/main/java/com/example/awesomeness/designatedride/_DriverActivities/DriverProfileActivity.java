package com.example.awesomeness.designatedride._DriverActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.awesomeness.designatedride.R;
import com.example.awesomeness.designatedride.Util.Constants;
import com.example.awesomeness.designatedride.Util.ProfileDialogHelper;
import com.example.awesomeness.designatedride.Util.ProfileHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DriverProfileActivity extends AppCompatActivity {

    //----firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    //----Widgets---
    private EditText firstNameET;
    private EditText lastNameET;
    private EditText emailET;
    private Button updateProfileBtn;
    private TextView resetPwdTV;
    private TextView closeTV;
    private ScrollView scrollView;

    private String _userId;
    private HashMap<String, String> childMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        Intent intent = getIntent();
        String userid = intent.getStringExtra(Constants.INTENT_KEY);
        childMap = new HashMap<>();

        initFirebase();
        initWidgets();

        final ProfileHelper profileHelper = new ProfileHelper(mDatabaseReference, mUser, childMap, DriverProfileActivity.this, firstNameET, lastNameET, emailET);

        //populate EditText fields
        profileHelper.populateUserInfo();

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileHelper.updateAccount();
                hideKeyboard();
            }
        });

        resetPwdTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.password_reset_dialog, null);
                ProfileDialogHelper dialogHelper = new ProfileDialogHelper(DriverProfileActivity.this, view, mAuth, mUser);
                dialogHelper.createResetPwdDialog();
            }
        });

        closeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTextFields();
                gotoActivity();
            }
        });

    }//End of onCreate

    //--------------------------------------------------------------------------
    private void initWidgets() {

        firstNameET = (EditText) findViewById(R.id.firstNameET_drUpdate);
        lastNameET = (EditText) findViewById(R.id.lastNameET_drUpdate);
        emailET = (EditText) findViewById(R.id.emailET_drUpdate);
        updateProfileBtn = (Button) findViewById(R.id.updateProfileBtn_drUpdate);
        resetPwdTV = (TextView) findViewById(R.id.resetPwdTV_dUpdate);
        closeTV = (TextView) findViewById(R.id.closeTV_dUpdate);
        scrollView = (ScrollView) findViewById(R.id.scrollView_dProfile);

    }

    //----------------------------------------------------------------------------------------------
    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
        mDatabaseReference.keepSynced(true);
    }

    //----------------------------------------------------------------------------------------------
    private void gotoActivity() {
        startActivity(new Intent(DriverProfileActivity.this, DriverActivity.class));
        finish();
    }

    //----------------------------------------------------------------------------------------------
    private void clearTextFields() {
        firstNameET.setText("");
        lastNameET.setText("");
        emailET.setText("");
    }

    //----------------------------------------------------------------------------------------------
    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context
                .INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(scrollView.getWindowToken(), 0);
    }

}
