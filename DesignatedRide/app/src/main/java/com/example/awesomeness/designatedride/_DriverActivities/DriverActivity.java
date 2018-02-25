
package com.example.awesomeness.designatedride._DriverActivities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.awesomeness.designatedride.Activities.LoginActivity;
import com.example.awesomeness.designatedride.Util.ProfileDialogHelper;
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


    //---
    CircleImageView profileImage;
    ImageButton profileBtn;
    ImageButton pickupRiderBtn;
    ImageButton settingsBtn;

    private static final int ERROR_DIALOG_REQUEST = 9001;

    //----------
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button updateProfileBtn, logoutBtn, yesButton, noButton;
    private TextView cancelTV;


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
    private void signOutUser() {
        if (mUser != null && mAuth != null) {
            mAuth.signOut();
            gotoActivity(LoginActivity.class);
        }
    }

    /***
     //----------------------------------------------------------------------------------------------
     private void createPopupDialog() {

     dialogBuilder = new AlertDialog.Builder(this);
     View view = getLayoutInflater().inflate(R.layout.profile_dialog_popup, null);

     updateProfileBtn = (Button) view.findViewById(R.id.btn_updateProfile_drvrPopup);
     logoutBtn = (Button) view.findViewById(R.id.btn_logout_drvrPopup);
     cancelTV = (TextView) view.findViewById(R.id.tv_cancelLink_drvrPopup);

     dialogBuilder.setView(view);
     dialog = dialogBuilder.create();
     dialog.show();

     //-----------
     updateProfileBtn.setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View v) {

    }
    });

     //-----------
     logoutBtn.setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View v) {
    dialog.dismiss();

    //will delay the next dialog
    new Handler().postDelayed(new Runnable() {
    @Override public void run() {
    showConfirmationDialog();
    }
    }, 100);

    }
    });

     //-----------
     cancelTV.setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View v) {
    dialog.dismiss();
    }
    });

     }//End of createPopupDialog

     //----------------------------------------------------------------------------------------------
     private void showConfirmationDialog() {

     final AlertDialog _dialog;
     AlertDialog.Builder _dialogBuilder;
     View view = getLayoutInflater().inflate(R.layout.confirmation_dialog, null);
     _dialogBuilder = new AlertDialog.Builder(this);
     yesButton = (Button) view.findViewById(R.id.yesButton);
     noButton = (Button) view.findViewById(R.id.noButton);

     _dialogBuilder.setView(view);
     _dialog = _dialogBuilder.create();
     _dialog.show();

     yesButton.setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View v) {

    signOutUser();

    }
    });

     noButton.setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View v) {

    _dialog.dismiss();

    }
    });

     }//End of showConfirmationDialog
     ***/


}
