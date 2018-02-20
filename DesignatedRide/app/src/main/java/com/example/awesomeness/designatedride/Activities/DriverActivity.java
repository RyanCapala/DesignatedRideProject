package com.example.awesomeness.designatedride.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.awesomeness.designatedride.R;
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

    private String cUserProfileImage = "userImage";

    //---
    CircleImageView profileImage;
    ImageButton profileBtn;
    ImageButton pickupRiderBtn;
    ImageButton settingsBtn;

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

        //------------------------
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                // TODO: 2/17/18 set location for the signout
                //temporary location for signout
                if (mUser != null && mAuth != null) {
                    mAuth.signOut();
                    gotoActivity(LoginActivity.class);
                }
            }
        });


    }//End of onCreate


    //----------------------------------------------------------------------------------------------
    private void gotoActivity(Class activityClass) {
        startActivity(new Intent(DriverActivity.this, activityClass));
        finish();
    }

    private void gotoActivityWithArrowBack(Class activityClass) {
        startActivity(new Intent(DriverActivity.this, activityClass));
    }

    private void initWidgets(){
        profileImage = (CircleImageView) findViewById(R.id.profileImgView_driver);
        profileBtn = (ImageButton) findViewById(R.id.viewProfileImgBtn_driver);
        pickupRiderBtn = (ImageButton) findViewById(R.id.pickupRiderImgBtn_driver);
        settingsBtn = (ImageButton) findViewById(R.id.settingsImgBtn_driver);
    }

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
