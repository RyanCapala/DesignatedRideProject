package com.example.awesomeness.designatedride._DriverActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.awesomeness.designatedride.R;
import com.example.awesomeness.designatedride._RiderActivities.RiderActivity;
import com.example.awesomeness.designatedride.util.Constants;
import com.example.awesomeness.designatedride.util.ProfileDialogHelper;
import com.example.awesomeness.designatedride.util.ProfileHelper;
import com.example.awesomeness.designatedride.util.Vehicle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverProfileActivity extends AppCompatActivity {

    //----firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference, mDbRef;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    //----Widgets---
//    private EditText firstNameET;
//    private EditText lastNameET;
//    private EditText emailET;
//    private Button updateProfileBtn;
//    private TextView resetPwdTV;
//    private TextView closeTV;

    //-------New Widgets----------//
    private CircleImageView profileImage;
    private RatingBar ratingBar;
    private ImageButton editProfile_btn;
    private TextView fullName_TV, rating_TV, firstName_TV, lastName_TV, age_TV, email_TV, phone_TV;
    private TextView carModel_TV, carYear_TV, carMake_TV, wheelchair_TV, userName_TV;
    private ScrollView scrollView;
    private Context context;

    ProfileHelper profileHelper;

    private String _userId;
    private HashMap<String, String> childMap;

    private Vehicle vehicle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);
        //--- Back arrow---//
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String userid = intent.getStringExtra(Constants.INTENT_KEY);
        childMap = new HashMap<>();

        initFirebase();
        initWidgets();

        vehicle = new Vehicle(carMake_TV, carModel_TV, carYear_TV);

        profileHelper = new ProfileHelper(mDatabaseReference, mUser, context,
                childMap, fullName_TV, firstName_TV, lastName_TV, age_TV, email_TV, phone_TV,
                userName_TV, rating_TV, wheelchair_TV, profileImage, vehicle, ratingBar);
        profileHelper.populateDriverInfo();



        //=========================== RATING BAR =================================================//
        /*
         * TO MAKE THE RATING BAR NON CHANGEABLE BY THE DRIVER, SET THE 'isIndicator=true' and the
         * DRIVER WONT BE ABLE TO CHANGE IT.
         */
        //String default_rating = String.valueOf(ratingBar.getRating());
        //Toast.makeText(context, "Defualt rating = " + default_rating, Toast.LENGTH_SHORT).show();
//        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                Toast.makeText(context, "Rating: " + rating, Toast.LENGTH_SHORT).show();
//            }
//        });
        //=========================== RATING BAR =================================================//

        editProfile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(DriverEditProfileActivity.class);
            }
        });

//        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                profileHelper.updateAccount();
//                profileHelper.hideKeyboard(v);
//            }
//        });
//
//        resetPwdTV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                View view = getLayoutInflater().inflate(R.layout.password_reset_dialog, null);
//                ProfileDialogHelper dialogHelper = new ProfileDialogHelper(DriverProfileActivity.this, view, mAuth, mUser);
//                dialogHelper.createResetPwdDialog();
//            }
//        });
//
//        closeTV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clearTextFields();
//                gotoActivity();
//            }
//        });

        /*
         * Todo: Implement star ratings
         */

    }//End of onCreate

    //--------------------------------------------------------------------------------------------//
    //---- For back arrow ----//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                gotoActivity(DriverActivity.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //--------------------------------------------------------------------------
    private void initWidgets() {

        profileImage = (CircleImageView) findViewById(R.id.driverProf_img);
        editProfile_btn = (ImageButton) findViewById(R.id.driverProf_edit_btn);
        ratingBar = (RatingBar) findViewById(R.id.driverProf_ratingBar);
        fullName_TV = (TextView) findViewById(R.id.driverProf_fullName_tv);
        rating_TV = (TextView) findViewById(R.id.driverProf_rating_tv);
        firstName_TV = (TextView) findViewById(R.id.driverProf_fname_tv);
        lastName_TV = (TextView) findViewById(R.id.driverProf_lname_tv);
        age_TV = (TextView) findViewById(R.id.driverProf_age_tv);
        email_TV = (TextView) findViewById(R.id.driverProf_email_tv);
        phone_TV = (TextView) findViewById(R.id.driverProf_phone_tv);
        carMake_TV = (TextView) findViewById(R.id.driverProf_carmake_tv);
        carModel_TV = (TextView) findViewById(R.id.driverProf_carmodel_tv);
        carYear_TV = (TextView) findViewById(R.id.driverProf_caryear_tv);
        wheelchair_TV = (TextView) findViewById(R.id.driverProf_wheelchair_tv);
        userName_TV = (TextView) findViewById(R.id.driverProf_username_tv);

//        firstNameET = (EditText) findViewById(R.id.firstNameET_drUpdate);
//        lastNameET = (EditText) findViewById(R.id.lastNameET_drUpdate);
//        emailET = (EditText) findViewById(R.id.emailET_drUpdate);
//        updateProfileBtn = (Button) findViewById(R.id.updateProfileBtn_drUpdate);
//        resetPwdTV = (TextView) findViewById(R.id.resetPwdTV_dUpdate);
//        closeTV = (TextView) findViewById(R.id.closeTV_dUpdate);
        scrollView = (ScrollView) findViewById(R.id.scrollView_driverProf);
        ratingBar = (RatingBar) findViewById(R.id.driverProf_ratingBar);
        context = DriverProfileActivity.this;

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
        //startActivity(new Intent(DriverProfileActivity.this, activityClass));
        Intent intent = new Intent(DriverProfileActivity.this, activityClass);
        intent.putExtra(Constants.DR_CHILDMAP_KEY, profileHelper.getChildMap());
        startActivity(intent);
        finish();
    }

    //----------------------------------------------------------------------------------------------


    //----------------------------------------------------------------------------------------------
    private void clearTextFields() {
//        firstNameET.setText("");
//        lastNameET.setText("");
//        emailET.setText("");
    }


}
