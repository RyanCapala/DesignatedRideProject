package com.example.awesomeness.designatedride._RiderActivities;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.awesomeness.designatedride.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// TODO: 2/27/2018 Add the date circle on top of action bar
public class RiderViewProfileActivity extends AppCompatActivity {
    private static final String TAG = "RiderViewProfileActivity";

    // Firebase
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;

    // Widgets
    ImageButton editProfileButton;

    private enum riderInfoItem{
        FIRST_NAME, LAST_NAME, AGE, EMAIL, PHONE_NUMBER,
        INSURANCE_CO, RIDE_COVERAGE, DOCTOR_GEN_NAME,
        PUBLIC_NAME, WHEEL_CHAIR_REQ
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_view_profile);
        initWidgets();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
        mDatabaseReference.keepSynced(true);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RiderViewProfileActivity.this, "Edit profile button pressed!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void initWidgets(){
        initLabels();
        editProfileButton = findViewById(R.id.editProfileImageButton_rider);
    }

    private void initLabels(){
        setTabularLabelText((ConstraintLayout) findViewById(R.id.viewProfileRider_gen_fName),
                getResources().getString(R.string.rider_profile_gen_info_fname));
        setTabularLabelText((ConstraintLayout) findViewById(R.id.viewProfileRider_gen_lName),
                getResources().getString(R.string.rider_profile_gen_info_lname));
        setTabularLabelText((ConstraintLayout) findViewById(R.id.viewProfileRider_gen_age),
                getResources().getString(R.string.rider_profile_gen_info_age));
        setTabularLabelText((ConstraintLayout) findViewById(R.id.viewProfileRider_gen_email),
                getResources().getString(R.string.rider_profile_gen_info_email));
        setTabularLabelText((ConstraintLayout) findViewById(R.id.viewProfileRider_gen_phoneNumber),
                getResources().getString(R.string.rider_profile_gen_info_phonenumber));

        setTabularLabelText((ConstraintLayout) findViewById(R.id.viewProfileRider_med_insuranceCompany),
                getResources().getString(R.string.rider_profile_med_info_insurance_company));
        setTabularLabelText((ConstraintLayout) findViewById(R.id.viewProfileRider_med_riderCoverage),
                getResources().getString(R.string.rider_profile_med_info_ride_coverage));
        setTabularLabelText((ConstraintLayout) findViewById(R.id.viewProfileRider_med_doctorGenName),
                getResources().getString(R.string.rider_profile_med_info_gen_doctor_name));

        setTabularLabelText((ConstraintLayout) findViewById(R.id.viewProfileRider_public_publicName),
                getResources().getString(R.string.rider_profile_public_info_public_name));
        setTabularLabelText((ConstraintLayout) findViewById(R.id.viewProfileRider_public_wheelChairReq),
                getResources().getString(R.string.rider_profile_public_info_wheelchair_required));
    }

    // TODO: 2/27/2018 Retreive relevent data from database, whether local or Firebase 
    private void retrieveUserData(){

    }
    private void setTabularItemText(riderInfoItem itemType, String value){
        int id = -1;
        switch (itemType){
            case FIRST_NAME: id = R.id.viewProfileRider_gen_fName; break;
            case LAST_NAME: id = R.id.viewProfileRider_gen_lName; break;
            case AGE: id = R.id.viewProfileRider_gen_age; break;
            case EMAIL: id = R.id.viewProfileRider_gen_age; break;
            case PHONE_NUMBER: id = R.id.viewProfileRider_gen_phoneNumber; break;
            case INSURANCE_CO: id = R.id.viewProfileRider_med_insuranceCompany; break;
            case RIDE_COVERAGE: id = R.id.viewProfileRider_med_riderCoverage; break;
            case DOCTOR_GEN_NAME: id = R.id.viewProfileRider_med_doctorGenName; break;
            case PUBLIC_NAME: id = R.id.viewProfileRider_public_publicName; break;
            case WHEEL_CHAIR_REQ: id = R.id.viewProfileRider_public_wheelChairReq; break;
        }
        if(id != -1 && value != null){
            TextView valueText = findViewById(id);
            valueText.setText(value);
        }

    }
    private void setTabularLabelText(ConstraintLayout layout, String text){
        TextView label = (TextView)layout.getChildAt(0);
        label.setText(text);
    }



}
