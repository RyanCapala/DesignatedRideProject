package com.example.awesomeness.designatedride._DriverActivities;

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
public class DriverViewProfileActivity extends AppCompatActivity {
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
        PUBLIC_NAME, CAR_MODEL, CAR_MAKE, CAR_YEAR,
        RATING, WHEEL_CHAIR
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_view_profile);
        initWidgets();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
        mDatabaseReference.keepSynced(true);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DriverViewProfileActivity.this, "Edit profile button pressed!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void initWidgets(){
        initLabels();
        editProfileButton = findViewById(R.id.editProfileImageButton_driver);
    }

    private void initLabels(){
        setTabularLabelText((ConstraintLayout) findViewById(R.id.viewProfileDriver_gen_fName),
                getResources().getString(R.string.rider_profile_gen_info_fname));
        setTabularLabelText((ConstraintLayout) findViewById(R.id.viewProfileDriver_gen_lName),
                getResources().getString(R.string.rider_profile_gen_info_lname));
        setTabularLabelText((ConstraintLayout) findViewById(R.id.viewProfileDriver_gen_age),
                getResources().getString(R.string.rider_profile_gen_info_age));
        setTabularLabelText((ConstraintLayout) findViewById(R.id.viewProfileDriver_gen_email),
                getResources().getString(R.string.rider_profile_gen_info_email));
        setTabularLabelText((ConstraintLayout) findViewById(R.id.viewProfileDriver_gen_phoneNumber),
                getResources().getString(R.string.rider_profile_gen_info_phonenumber));

        setTabularLabelText((ConstraintLayout) findViewById(R.id.viewProfileDriver_car_make),
                getResources().getString(R.string.driver_profile_car_make));
        setTabularLabelText((ConstraintLayout) findViewById(R.id.viewProfileDriver_car_model),
                getResources().getString(R.string.driver_profile_car_model));
        setTabularLabelText((ConstraintLayout) findViewById(R.id.viewProfileDriver_car_year),
                getResources().getString(R.string.driver_profile_car_year));
        setTabularLabelText((ConstraintLayout) findViewById(R.id.viewProfileDriver_wheelchair_access),
                getResources().getString(R.string.driver_profile_wheelchair_access));


        setTabularLabelText((ConstraintLayout) findViewById(R.id.viewProfileDriver_public_publicName),
                getResources().getString(R.string.rider_profile_public_info_public_name));
        setTabularLabelText((ConstraintLayout) findViewById(R.id.viewProfileDriver_rating),
                getResources().getString(R.string.driver_profile_rating));
    }

    // TODO: 2/27/2018 Retreive relevent data from database, whether local or Firebase
    private void retrieveUserData(){

    }
    private void setTabularItemText(riderInfoItem itemType, String value){
        int id = -1;
        switch (itemType){
            case FIRST_NAME: id = R.id.viewProfileDriver_gen_fName; break;
            case LAST_NAME: id = R.id.viewProfileDriver_gen_lName; break;
            case AGE: id = R.id.viewProfileDriver_gen_age; break;
            case EMAIL: id = R.id.viewProfileDriver_gen_email; break;
            case PHONE_NUMBER: id = R.id.viewProfileDriver_gen_phoneNumber; break;
            case CAR_MODEL: id = R.id.viewProfileDriver_car_model; break;
            case CAR_MAKE: id = R.id.viewProfileDriver_car_make; break;
            case CAR_YEAR: id = R.id.viewProfileDriver_car_year; break;
            case WHEEL_CHAIR: id = R.id.viewProfileDriver_wheelchair_access; break;
            case PUBLIC_NAME: id = R.id.viewProfileDriver_public_publicName; break;
            case RATING: id = R.id.viewProfileDriver_rating; break;
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
