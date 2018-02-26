package com.example.awesomeness.designatedride._RiderActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RiderProfileActivity extends AppCompatActivity {
    private static final String TAG = "RiderProfileActivity";


    //--widgets
    private EditText firstNameET;
    private EditText lastNameET;
    private EditText emailET;
    private Button updateProfileBtn;
    private TextView resetPwdTV;
    private TextView closeTV;

    //--firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;


    private HashMap<String, String> childMap;
    private String child_fname, child_lname, child_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_profile);

        Intent intent = getIntent();
        String userid = intent.getStringExtra(Constants.INTENT_KEY);

        childMap = new HashMap<>();
        initFirebase();
        initWidgets();

        final ProfileHelper profileHelper = new ProfileHelper(mDatabaseReference, mUser, childMap, RiderProfileActivity.this, firstNameET, lastNameET, emailET);
        //populate EditText fields
        profileHelper.populateUserInfo();

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileHelper.updateAccount();
            }
        });

        resetPwdTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.password_reset_dialog, null);
                ProfileDialogHelper dialogHelper = new ProfileDialogHelper(RiderProfileActivity.this, view, mAuth, mUser);
                dialogHelper.createResetPwdDialog();
            }
        });

        closeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearEditTextField();
                gotoActivity();
            }
        });

    }


    //----------------------------------------------------------------------------------------------
    private void initWidgets() {
        firstNameET = (EditText) findViewById(R.id.firstNameET_rdUpdate);
        lastNameET = (EditText) findViewById(R.id.lastNameET_rdUpdate);
        emailET = (EditText) findViewById(R.id.emailET_rdUpdate);
        updateProfileBtn = (Button) findViewById(R.id.updateProfileBtn_rdUpdate);
        resetPwdTV = (TextView) findViewById(R.id.resetPwdTV_rUpdate);
        closeTV = (TextView) findViewById(R.id.closeTV_rUpdate);
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
        startActivity(new Intent(RiderProfileActivity.this, RiderActivity.class));
        finish();
    }

    //----------------------------------------------------------------------------------------------
    private void clearEditTextField() {
        firstNameET.setText("");
        lastNameET.setText("");
        emailET.setText("");
    }

}
