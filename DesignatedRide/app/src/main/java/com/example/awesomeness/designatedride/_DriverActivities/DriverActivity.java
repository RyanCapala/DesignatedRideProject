
package com.example.awesomeness.designatedride._DriverActivities;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.awesomeness.designatedride.Activities.LoginActivity;
import com.example.awesomeness.designatedride.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverActivity extends AppCompatActivity {
    private static final String TAG = "DriverActivity";

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;

    private String _Driver = "Driver";
    private String _isAvaliable = "isAvaliable";
    private String userid;
    private String _AvaliableGeolocation = "AvaliableGeoLocation";
    private String _geoKey = "geoKey";
    private String key = "";

    private GeoFire mGeoFire;
    private DatabaseReference mGeoLocationRef;

    private FusedLocationProviderClient mLocation;

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
        userid = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
        mGeoLocationRef = FirebaseDatabase.getInstance().getReference().child(_AvaliableGeolocation);
        mGeoFire = new GeoFire(mGeoLocationRef);
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
                    //temporary using this button as a trigger to make the Driver avaliable for queries for RiderMapActivity.
                    mDatabaseReference.child(_Driver).child(userid).child(_geoKey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            key = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mLocation = LocationServices.getFusedLocationProviderClient(DriverActivity.this);
                    mLocation.getLastLocation().addOnSuccessListener(DriverActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            mGeoFire.setLocation(key, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {
                                    mDatabaseReference.child(_AvaliableGeolocation).child(key).child(_isAvaliable).setValue("true");
                                }
                            });
                        }
                    });

                    mDatabaseReference.child(_AvaliableGeolocation).child(key).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            if(dataSnapshot.getKey().equals(_isAvaliable)) {
                                mDatabaseReference.child(_AvaliableGeolocation).child(key).removeValue();
                                gotoActivity(DriverMapActivity.class);
                            }
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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
