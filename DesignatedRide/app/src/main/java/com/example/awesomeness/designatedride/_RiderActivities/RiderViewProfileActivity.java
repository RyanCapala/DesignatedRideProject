package com.example.awesomeness.designatedride._RiderActivities;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.awesomeness.designatedride.R;

public class RiderViewProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_view_profile);
        initWidgets();
    }

    private void initWidgets(){
        initLabels();
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
    private void setTabularLabelText(ConstraintLayout layout, String text){
        TextView label = (TextView)layout.getChildAt(0);
        label.setText(text);
    }

}
