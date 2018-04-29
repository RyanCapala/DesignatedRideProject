package com.example.awesomeness.designatedride.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.awesomeness.designatedride.R;
import com.example.awesomeness.designatedride.util.Constants;
import com.example.awesomeness.designatedride.util.UserDataHelper;
import com.example.awesomeness.designatedride._DriverActivities.DriverActivity;
import com.example.awesomeness.designatedride._RiderActivities.RiderActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StartLogoActivity extends AppCompatActivity {

    private static final String TAG = "StartLogoActivity";

    // Widgets
    ProgressBar progressBar;
    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;

    //Global Var
    private String uid;
    private String mode;
    private String uName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_logo);
        initWidgets();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
        mDatabaseReference.keepSynced(true);
        checkForSavedState();
    }
    private void checkForSavedState() {
        UserDataHelper.AccountInfoContainer container = UserDataHelper.loadLocalUser(getApplicationContext());
        Log.d(TAG, "checkForSavedState: USERINFO:" + container.email + container.password + container.userType);

        if (container.containsInvalidData()) {
            gotoActivity(LoginActivity.class);
            Log.d(TAG, "checkForSavedState: Did not find saved state");
            return;
        }
        Log.d(TAG, "checkForSavedState: Found saved state");
        loginUser(container.email, container.password);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (!(grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            finish();
        }
    }

    private void loginUser(String email, String pwd) {
        mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(this, new
                OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mUser = mAuth.getCurrentUser();
                            uid = mUser.getUid();
                            Log.d(TAG, "onComplete: <<<< Signed In >>>>");

                            //Check user mode if "Rider" or "Driver"
                            //then, send user to each specific page.
                            getUserData();
                        } else {
                            Log.d(TAG, "onComplete: No previous state loaded");
                            gotoActivity(LoginActivity.class);
                        }


                    }
                });
    }

    private void getUserData() {
        //get username
        mDatabaseReference
                .child(Constants.USER)
                .child(uid)
                .child(Constants.PROFILE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uName = dataSnapshot.child(Constants.FIRSTNAME).getValue(String.class);
                mode = dataSnapshot.child(Constants.USERMODE).getValue(String.class);
                SharedPreferences sp = getSharedPreferences(Constants.SF_UNAME_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor spe = sp.edit();
                spe.putString(uid, uName);
                spe.commit();

                Log.d(TAG, "onDataChange: MODE:" + mode + ", NAME:" + uName);
                gotoCorrectView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: getUserName was cancelled: " + databaseError.getMessage());
            }
        });
    }

    private void gotoCorrectView(){
        switch (mode){
            case Constants.DRIVER:
                 mDatabaseReference.child(Constants.DRIVER).child(uid).child(Constants.GEOKEY).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String driverKey = dataSnapshot.getValue(String.class);
                        if(driverKey != null) {
                            mDatabaseReference.child(Constants.ONLINE).child(driverKey).child(Constants.CONNECTED).setValue("true");
                            mDatabaseReference.child(Constants.ONLINE).child(driverKey).child(Constants.CONNECTED).onDisconnect().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                gotoActivity(DriverActivity.class); break;
            case Constants.RIDER:
                gotoActivity(RiderActivity.class);
                break;
            default: gotoActivity(LoginActivity.class);
                Toast.makeText(StartLogoActivity.this,
                        "Credentials Not Valid!", Toast.LENGTH_LONG).show();
        }
    }
    private void gotoActivity(Class activityClass) {
        Intent intent = new Intent(StartLogoActivity.this, activityClass);
        //Log.d(TAG, "gotoActivity: " + uName);
        //intent.putExtra(Constants.INTENT_KEY_NAME, uName);
        startActivity(intent);
        this.finish();
    }
    private void initWidgets(){
        progressBar = findViewById(R.id.startPageLogo_progessbar);
    }
}
