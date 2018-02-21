package com.example.awesomeness.designatedride.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.awesomeness.designatedride.R;
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

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;

    //Widgets
    private EditText userEmail;
    private EditText userPwd;
    private Button loginBtn;
    private TextView registerLinkTV;
    private TextView forgotPasswordTV;
    private ProgressDialog mProgressDialog;

    //Database Child names
    private String cUserMode = "userMode";
    private String cUser = "User";
    private String cProfile = "Profile";
    private String modeRider = "Rider";
    private String modeDriver = "Driver";


    //Global Var
    private String uid;
    private String mode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
        mDatabaseReference.keepSynced(true);
        initWidgets();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    Log.d(TAG, "onAuthStateChanged: <<<< Found User >>>");
                } else {
                    Log.d(TAG, "onAuthStateChanged: <<<< User signed out!! >>>>");
                }

            }
        };

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString().trim();
                String pwd = userPwd.getText().toString().trim();

                if (fieldChecking(email,pwd)) {
                        mProgressDialog.setMessage("Logging in...");
                        mProgressDialog.show();
                        loginUser(email, pwd);
                }
            }
        });

        forgotPasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(ForgotPasswordActivity.class, true);
            }
        });

        registerLinkTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(RegisterActivity.class, true);

            }
        });


    }//End of onCreate



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void loginUser(String email, String pwd) {
        final String testpwd = pwd;
        mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(this, new
                OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (!checkPwd(testpwd)) {
                                gotoActivity(ChangePasswordActivity.class, false);
                            }
                            else {
                                mUser = mAuth.getCurrentUser();
                                uid = mUser.getUid();
                                Log.d(TAG, "onComplete: <<<< Signed In >>>>");

                                //Check user mode if "Rider" or "Driver"
                                //then, send user to each specific page.
                                mDatabaseReference.
                                        child(cUser).
                                        child(uid).
                                        child(cProfile).
                                        child(cUserMode).
                                        addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                mode = dataSnapshot.getValue(String.class);
                                                if (mode.equals(modeDriver)) {

                                                    gotoActivity(DriverActivity.class, true);
                                                    clearEditText();

                                                } else if (mode.equals(modeRider)) {

                                                    gotoActivity(RiderActivity.class, true);
                                                    clearEditText();

                                                } else {
                                                    Toast.makeText(LoginActivity.this,
                                                            "Credentials Not Valid!",
                                                            Toast.LENGTH_LONG).show();
                                                    clearEditText();
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }else {
                            Toast.makeText(LoginActivity.this, "Failed Sign In!",
                                    Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                            clearEditText();
                        }

                    }
                });
    }//End of loginUser

    private void initWidgets() {
        userEmail = (EditText) findViewById(R.id.userEmailET_log);
        userPwd = (EditText) findViewById(R.id.userPassET_log);
        loginBtn = (Button) findViewById(R.id.userLoginBtn_log);
        registerLinkTV = (TextView) findViewById(R.id.registerLinkTV_log);
        forgotPasswordTV = (TextView) findViewById(R.id.forgotPasswordTV_log);
        mProgressDialog = new ProgressDialog(this);
    }

    private void clearEditText() {
        userEmail.setText("");
        userPwd.setText("");
    }

    private void gotoActivity(Class activityClass, boolean isDismiss) {

        if (isDismiss) {
            mProgressDialog.dismiss();
        }
        startActivity(new Intent(LoginActivity.this, activityClass));
        finish();
    }

    private boolean fieldChecking(String email ,String pwd){
        boolean flag = true;

        if(email.isEmpty()) { userEmail.setError("Email address is required."); flag = false;}
        if(pwd.isEmpty()) {userPwd.setError("Password is required."); flag = false;}
        return flag;
    }

    private boolean checkPwd(String pwd){
        return (pwd.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{3,}") && pwd.length() >= 8);
    }
}
