package com.example.awesomeness.designatedride.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.awesomeness.designatedride.R;
import com.example.awesomeness.designatedride.util.Constants;
import com.example.awesomeness.designatedride._DriverActivities.DriverActivity;
import com.example.awesomeness.designatedride._RiderActivities.RiderActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePasswordActivity extends AppCompatActivity {
    public static final String TAG = "ChangePasswordActivity";

    //Widgets
    private EditText userPwd;
    private EditText verifyPwd;
    private Button loginBtn;
    private ProgressDialog mProgressDialog;


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;

    //Global Variable
    private String uid;
    private String mode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);

        initWidgets();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
        mDatabaseReference.keepSynced(true);

        Toast.makeText(ChangePasswordActivity.this,"Temporary Password Revoked, please create a new password.", Toast.LENGTH_LONG).show();

        if(TextUtils.isEmpty(userPwd.getText().toString().trim())) { userPwd.setError("Password must be at least 8 characters " +
                "containing at least one of each: lower case (a-z), upper case (A-Z), number (0-9)");}

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pwd = userPwd.getText().toString().trim();
                final String vpwd = verifyPwd.getText().toString().trim();
                mProgressDialog.setMessage("Logging in ...");

                if (fieldChecking(pwd,vpwd)) {
                    if(!checkPwd(pwd)) { pwdMessage(true,true); }
                    else if(pwdmatch(pwd,vpwd)) {
                        mProgressDialog.show();
                        mUser = mAuth.getCurrentUser();
                        mUser.updatePassword(pwd);
                        uid = mUser.getUid();
                        Log.d(TAG, "onComplete: <<<< Signed In >>>>");

                        //Check user mode if "Rider" or "Driver"
                        //then, send user to each specific page.
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

                                            gotoActivity(DriverActivity.class, true);
                                            clearEditText();

                                        } else if (mode.equals(Constants.RIDER)) {

                                            gotoActivity(RiderActivity.class, true);
                                            clearEditText();

                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                }
            }
        });


    }//End of onCreate

    private void initWidgets() {
        userPwd = (EditText) findViewById(R.id.passwrodET_reg);
        verifyPwd = (EditText) findViewById(R.id.verifyPwdET_reg);
        loginBtn = (Button) findViewById(R.id.userLoginBtn_log);
        mProgressDialog = new ProgressDialog(this);
    }

    private void clearEditText() {
        userPwd.setText("");
        verifyPwd.setText("");
    }

    private void gotoActivity(Class activityClass, boolean isDismiss) {

        if (isDismiss) {
            mProgressDialog.dismiss();
        }
        startActivity(new Intent(ChangePasswordActivity.this, activityClass));
        finish();
    }

    private void gotoActivity(Class activityClass) {
        startActivity(new Intent(ChangePasswordActivity.this, activityClass));
        finish();
    }

    private boolean fieldChecking(String pwd, String vpwd){
        boolean flag = true;

        if(pwd.isEmpty()) { pwdMessage(true,false); flag = false;}
        if(vpwd.isEmpty()) { pwdMessage(false,true); flag = false;}

        return flag;
    }

    private boolean pwdmatch(String pwd, String vpwd){
        if(!pwd.equals(vpwd)) {
            userPwd.setError("Passwords don't match!");
            verifyPwd.setError("Passwords don't match!");
            return false;
        }
        else
            return true;
    }

    private void pwdMessage(boolean userpwd, boolean vpwd) {
        String errorMsg = "Password must be at least 8 characters " +
                "containing at least one of each: lower case (a-z), upper case (A-Z), number (0-9)";

        if(userpwd) {
            userPwd.setError(errorMsg);
        }
        if(vpwd) {
            verifyPwd.setError(errorMsg);
        }

    }

    private boolean checkPwd(String pwd){
        return (pwd.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{3,}") && pwd.length() >= 8);
    }
}