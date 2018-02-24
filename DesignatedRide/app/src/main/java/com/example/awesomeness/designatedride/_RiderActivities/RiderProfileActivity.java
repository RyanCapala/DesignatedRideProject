package com.example.awesomeness.designatedride._RiderActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.awesomeness.designatedride.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RiderProfileActivity extends AppCompatActivity {
    private static final String TAG = "RiderProfileActivity";

    private String INTENT_KEY = "userid";

    //--widgets
    private EditText rUserName;
    private EditText rLastName;
    private EditText rEmail;
    private EditText rPassword;
    private EditText rVPassword;
    private Button rUpdateProfileBtn;

    //--firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_profile);

        Intent intent = getIntent();
        String userid = intent.getStringExtra(INTENT_KEY);
        Toast.makeText(this, userid + "\nRider Profile", Toast.LENGTH_LONG).show();

        /**
         * Todo: populate editText with the user infor using the userId that was passed by intent.
         */

        initWidgets();

        rUpdateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity();
            }
        });
    }


    //----------------------------------------------------------------------------------------------
    private void initWidgets() {
        rUserName = (EditText) findViewById(R.id.firstNameET_rdUpdate);
        rLastName = (EditText) findViewById(R.id.lastNameET_rdUpdate);
        rEmail = (EditText) findViewById(R.id.emailET_rdUpdate);
        rPassword = (EditText) findViewById(R.id.passwrodET_rdUpdate);
        rVPassword = (EditText) findViewById(R.id.verifyPwdET_rdUpdate);
        rUpdateProfileBtn = (Button) findViewById(R.id.updateProfileBtn_rdUpdate);
    }

    //----------------------------------------------------------------------------------------------
    private void gotoActivity() {
        startActivity(new Intent(RiderProfileActivity.this, RiderActivity.class));
        finish();
    }

}
