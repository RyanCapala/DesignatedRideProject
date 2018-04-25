package com.example.awesomeness.designatedride._RiderActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.awesomeness.designatedride.R;
import com.example.awesomeness.designatedride.util.Constants;
import com.example.awesomeness.designatedride.util.ProfileDialogHelper;
import com.example.awesomeness.designatedride.util.ProfileHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class RiderProfileActivity extends AppCompatActivity {
    private static final String TAG = "RiderProfileActivity";


    // old widgets
//    private EditText firstNameET;
//    private EditText lastNameET;
//    private EditText emailET;
//    private Button updateProfileBtn;
//    private TextView resetPwdTV;
//    private TextView closeTV;
//    private ScrollView scrollView;

    // new widgets
    private TextView fullName_TV, userAddress_TV;
    private TextView firstName_TV, lastName_TV, age_TV, email_TV, phone_TV;
    private TextView insurance_TV, coverage_TV, doctorname_TV;
    private TextView userName_TV, wheelchair_TV;
    private CircleImageView profileImage;
    private FloatingActionButton editProfile_fab;

    private Context context = RiderProfileActivity.this;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String userid = intent.getStringExtra(Constants.INTENT_KEY);

        childMap = new HashMap<>();
        initFirebase();
        initWidgets();

        ProfileHelper profileHelper = new ProfileHelper(mDatabaseReference, mUser, context,
                childMap, userAddress_TV, fullName_TV, firstName_TV, lastName_TV, age_TV,
                email_TV, phone_TV, insurance_TV, coverage_TV, doctorname_TV, userName_TV,
                wheelchair_TV, profileImage);

        profileHelper.populateUserInfo1();

        editProfile_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(RiderEditProfileActivity.class);
            }
        });

        //========================================================================================//
        /*
        final ProfileHelper profileHelper = new ProfileHelper(mDatabaseReference, mUser, childMap, RiderProfileActivity.this, firstNameET, lastNameET, emailET);
        //populate EditText fields
        profileHelper.populateUserInfo();

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileHelper.updateAccount();
                profileHelper.hideKeyboard(v);

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
         */
        //========================================================================================//



    }

    // For the Arrow back
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                gotoActivity(RiderActivity.class);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    //----------------------------------------------------------------------------------------------


    private void initWidgets() {
        fullName_TV = (TextView) findViewById(R.id.user_fullName_tv_rdr);
        userAddress_TV = (TextView) findViewById(R.id.user_address_tv_rdr);
        firstName_TV = (TextView) findViewById(R.id.user_fname_tv_rdr);
        lastName_TV = (TextView) findViewById(R.id.user_lname_tv_rdr);
        age_TV = (TextView) findViewById(R.id.user_age_tv_rdr);
        email_TV = (TextView) findViewById(R.id.user_email_tv_rdr);
        phone_TV = (TextView) findViewById(R.id.user_phone_tv_rdr);
        insurance_TV = (TextView) findViewById(R.id.user_ins_tv_rdr);
        coverage_TV = (TextView) findViewById(R.id.user_coverage_tv_rdr);
        doctorname_TV = (TextView) findViewById(R.id.user_docsname_tv_rdr);
        userName_TV = (TextView) findViewById(R.id.user_name_tv_rdr);
        wheelchair_TV = (TextView) findViewById(R.id.user_wheelchair_tv_rdr);
        profileImage = (CircleImageView) findViewById(R.id.user_profileImgView_rdr);
        editProfile_fab = (FloatingActionButton) findViewById(R.id.update_profile_fab_rdr);
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
    private void gotoActivity(Class activityClass) {
        startActivity(new Intent(RiderProfileActivity.this, activityClass));
        finish();
    }

    //----------------------------------------------------------------------------------------------
    /*
    private void clearEditTextField() {
        firstNameET.setText("");
        lastNameET.setText("");
        emailET.setText("");
    }

    private void initWidgets() {
        firstNameET = (EditText) findViewById(R.id.firstNameET_rdUpdate);
        lastNameET = (EditText) findViewById(R.id.lastNameET_rdUpdate);
        emailET = (EditText) findViewById(R.id.emailET_rdUpdate);
        updateProfileBtn = (Button) findViewById(R.id.updateProfileBtn_rdUpdate);
        resetPwdTV = (TextView) findViewById(R.id.resetPwdTV_rUpdate);
        closeTV = (TextView) findViewById(R.id.closeTV_rUpdate);
        scrollView = (ScrollView) findViewById(R.id.scrollView_profile);
    }
     */



}
