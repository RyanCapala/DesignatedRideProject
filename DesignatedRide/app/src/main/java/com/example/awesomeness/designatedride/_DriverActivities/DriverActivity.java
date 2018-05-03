
package com.example.awesomeness.designatedride._DriverActivities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.awesomeness.designatedride._RiderActivities.RiderActivity;
import com.example.awesomeness.designatedride.activities.LoginActivity;
import com.example.awesomeness.designatedride.R;
import com.example.awesomeness.designatedride.util.Constants;
import com.example.awesomeness.designatedride.util.ProfileDialogHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverActivity extends AppCompatActivity {
    private static final String TAG = "DriverActivity";

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;

    private Context context;


    //---Old widget variables--//
    //private CircleImageView profileImage;
    //private ImageButton profileBtn;
    //private ImageButton pickupRiderBtn;
    //private ImageButton settingsBtn;

    //---New widgets---//
    private CircleImageView profileImage;
    private ImageButton viewProfile_btn, viewAvailability_btn, viewSchedule_btn, viewMap_btn;
    private ImageButton logout_btn, make_available_btn;
    private TextView driverName;
    private View parentView;    //for snackbar

    private static final int ERROR_DIALOG_REQUEST = 9001;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        Toast.makeText(this, "Driver Page", Toast.LENGTH_LONG).show();

        initWidgets();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
        mDatabaseReference.keepSynced(true);


        String uname = getFNameFromShrPref(mUser.getUid());
        Snackbar.make(parentView, "Welcome " + uname + "!", Snackbar.LENGTH_LONG).show();
        setUserSpecificName();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo: profile image
            }
        });

        //------------------------
        viewProfile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Profile", Toast.LENGTH_SHORT).show();
                gotoActivity(DriverProfileActivity.class);
            }
        });

        //------------------------
        viewAvailability_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(DriverAvailableActivity.class);
            }
        });

        //------------------------
        viewSchedule_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Schedule", Toast.LENGTH_SHORT).show();
            }
        });

        //------------------------
        viewMap_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 gotoActivity(DriverMapActivity.class);
                //Toast.makeText(context, "View Map", Toast.LENGTH_SHORT).show();

            }
        });

        //------------------------
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View confirm_dialog_view = getLayoutInflater().inflate(R.layout
                        .confirmation_dialog, null);
                ProfileDialogHelper profileDialogHelper = new ProfileDialogHelper(DriverActivity
                        .this, confirm_dialog_view, mAuth, mUser);
                profileDialogHelper.createConfirmationPrompt();
            }
        });

        //------------------------
        make_available_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DriverActivity.this, "Make Available clicked", Toast
                        .LENGTH_SHORT).show();
            }
        });


//        pickupRiderBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isServicesOk()) {
//                    gotoActivityWithArrowBack(DriverMapActivity.class);
//                }
//            }
//        });



    }//End of onCreate


    //----------------------------------------------------------------------------------------------
    private void gotoActivity(Class activityClass) {
        startActivity(new Intent(DriverActivity.this, activityClass));
        finish();
    }

    //----------------------------------------------------------------------------------------------
    private void gotoActivityWithArrowBack(Class activityClass) {
        startActivity(new Intent(DriverActivity.this, activityClass));
    }

    //----------------------------------------------------------------------------------------------
    private void initWidgets() {
        profileImage = (CircleImageView) findViewById(R.id.driverAct_img);
//        profileBtn = (ImageButton) findViewById(R.id.viewProfileImgBtn_driver);
//        pickupRiderBtn = (ImageButton) findViewById(R.id.pickupRiderImgBtn_driver);
//        settingsBtn = (ImageButton) findViewById(R.id.settingsImgBtn_driver);

        viewProfile_btn = (ImageButton) findViewById(R.id.driverAct_view_prof_btn);
        viewAvailability_btn = (ImageButton) findViewById(R.id.driverAct_availability_btn);
        viewSchedule_btn = (ImageButton) findViewById(R.id.driverAct_schedule_btn);
        viewMap_btn = (ImageButton) findViewById(R.id.driverAct_view_map_btn);
        logout_btn = (ImageButton) findViewById(R.id.driverAct_logout_btn);
        make_available_btn = (ImageButton) findViewById(R.id.driverAct_available_btn);
        driverName = (TextView) findViewById(R.id.driverAct_name);
        parentView = findViewById(R.id.activity_driver_layout);

        context = DriverActivity.this;
    }

    //----------------------------------------------------------------------------------------------
    public boolean isServicesOk() {
        Log.d(TAG, "isServicesOk: Checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable
                (DriverActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOk: Google services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOk: An error occured, but it can be fixed!");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(DriverActivity
                    .this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(DriverActivity.this, "Cant make map request", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    //----------------------------------------------------------------------------------------------
    private String getFNameFromShrPref(String uid) {
        String fname = "";
        SharedPreferences sf = getSharedPreferences(Constants.SF_UNAME_PREF, Context.MODE_PRIVATE);
        fname = sf.getString(uid, "");
        return fname;

        /*
        *Note:
        *       if the welcome snackbar only displays 'Welcome !' its because your account has been
        *       created already and your first name has not been stored in the shared preference.
        *       It should display 'Welcome yourName!'.
        *       But, if you edit your profile name, it will automatically store it to the SF.
        *       But, for new user, it will store its first name to shared pref when they register.
        *
         */
    }

    private void setUserSpecificName() {
        String name = getFNameFromShrPref(mUser.getUid());
        if (name != null && name.length() > 0) {
            driverName.setText(name + "!");
        }
    }

}
