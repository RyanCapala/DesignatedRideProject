package com.example.awesomeness.designatedride.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.awesomeness.designatedride.R;
import com.example.awesomeness.designatedride.Util.Constants;
import com.example.awesomeness.designatedride.Util.UserDataHelper;
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

public class StartPageActivity extends AppCompatActivity {
    private static final String TAG = "StartPageActivity";
    private TextView loginLink, registerLink;
    private final int ACCESS_FINE_LOCATION = 0;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;

    //Global Var
    private String uid;
    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
        mDatabaseReference.keepSynced(true);
        checkForSavedState();

        initWidgets();

        //ActivityCompat.requestPermissions(StartPageActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_FINE_LOCATION);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(LoginActivity.class);
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(RegisterActivity.class);
            }
        });


    }

    private void initWidgets() {
        loginLink = (TextView) findViewById(R.id.linkToLoginPage_start);
        registerLink = (TextView) findViewById(R.id.linkToRegisterPage_start);
    }

    private void gotoActivity(Class activityClass) {
        startActivity(new Intent(StartPageActivity.this, activityClass));
        finish();
    }
    private void checkForSavedState(){
        UserDataHelper.AccountInfoContainer container = UserDataHelper.loadLocalUser(getApplicationContext());
        Log.d(TAG, "checkForSavedState: USERINFO:" +container.email + container.password + container.userType);
        if(container.containsInvalidData()) {
            Log.d(TAG, "checkForSavedState: Did not find saved state");
            return;
        }
        loginUser(container.email, container.password);
        Log.d(TAG, "checkForSavedState: Found saved state");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(!(grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            finish();
        }
    }

    private void loginUser(String email, String pwd) {
        final String testpwd = pwd;
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
                                GetUserType();
                        }else {
                            Log.d(TAG, "onComplete: No previous state loaded");
                        }

                    }
                });
    }//End of loginUser

    private String GetUserType() {
        mDatabaseReference.
                child(Constants.USER).
                child(uid).
                child(Constants.PROFILE).
                child(Constants.USERMODE).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mode = dataSnapshot.getValue(String.class);
                        if (mode.equals(Constants.DRIVER)) {

                            gotoActivity(DriverActivity.class);

                        } else if (mode.equals(Constants.RIDER)) {

                            gotoActivity(RiderActivity.class);


                        } else {
                            Toast.makeText(StartPageActivity.this,
                                    "Credentials Not Valid!", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return mode;
    }

}
