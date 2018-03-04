
package com.example.awesomeness.designatedride._DriverActivities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverActivity extends AppCompatActivity {
    private static final String TAG = "DriverActivity";

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;


    //---
    private CircleImageView profileImage;
    private ImageButton profileBtn;
    private ImageButton pickupRiderBtn;
    private ImageButton settingsBtn;
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

        Intent intent = getIntent();
        String uname = intent.getStringExtra(Constants.INTENT_KEY_NAME);
        Snackbar.make(parentView, "Welcome " + uname + "!", Snackbar.LENGTH_LONG).show();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo: profile image
            }
        });

        //------------------------
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //createPopupDialog();
                View profile_dialog_view = getLayoutInflater().inflate(R.layout.profile_dialog_popup, null);
                View confirm_dialog_view = getLayoutInflater().inflate(R.layout.confirmation_dialog, null);
                ProfileDialogHelper profileDialogHelper = new ProfileDialogHelper(DriverActivity.this, profile_dialog_view, confirm_dialog_view, mAuth, mUser, DriverProfileActivity.class, LoginActivity.class);
                profileDialogHelper.createPopupDialog();
            }
        });

        //------------------------
        pickupRiderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isServicesOk()) {
                    gotoActivityWithArrowBack(DriverMapActivity.class);
                }
            }
        });

        //------------------------
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Todo: implement settings activity
            }
        });


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
        profileImage = (CircleImageView) findViewById(R.id.profileImgView_driver);
        profileBtn = (ImageButton) findViewById(R.id.viewProfileImgBtn_driver);
        pickupRiderBtn = (ImageButton) findViewById(R.id.pickupRiderImgBtn_driver);
        settingsBtn = (ImageButton) findViewById(R.id.settingsImgBtn_driver);
        parentView = findViewById(R.id.activity_driver_layout);
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
}
