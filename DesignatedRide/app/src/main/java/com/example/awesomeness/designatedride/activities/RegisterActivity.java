package com.example.awesomeness.designatedride.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.awesomeness.designatedride.model.User;
import com.example.awesomeness.designatedride.R;
import com.example.awesomeness.designatedride.util.Constants;
import com.example.awesomeness.designatedride.util.UserDataHelper;
import com.example.awesomeness.designatedride._DriverActivities.DriverActivity;
import com.example.awesomeness.designatedride._RiderActivities.RiderActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    //Widgets
    private EditText firstName;
    private EditText lastName;
    private EditText userEmailET;
    private EditText userPwd;
    private EditText verifyPwd;
    private Button registerBtn;
    private TextView loginLinkTV;
    private ToggleButton toggleButton;
    private ScrollView scrollView;

    //Firebase
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDbRef;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private String mPushKey;
    private ProgressDialog mProgressDialog;
    private Uri resultUri = null;
    private boolean isStatus;   // toggle position for rider or driver
    //private StorageReference mFirebaseStorage; //will be used for image storage
    //public static final int GALLERY_CODE = 1; //will be used for images


    private String uMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDatabase = FirebaseDatabase.getInstance();
        //mDatabaseReference = mDatabase.getReference().child(_User);
        mDatabaseReference = mDatabase.getReference();
        mDbRef = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);

        initWidgets();
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });

        loginLinkTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(LoginActivity.class);
            }
        });

        toggleButton.setOnCheckedChangeListener(new CompoundButton
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(RegisterActivity.this, "Rider", Toast.LENGTH_LONG)
                            .show();
                    isStatus = isChecked;
                } else {
                    Toast.makeText(RegisterActivity.this, "Driver", Toast.LENGTH_LONG)
                            .show();
                    isStatus = isChecked;
                }
            }
        });

        if (TextUtils.isEmpty(userPwd.getText().toString().trim())) {
            pwdMessage(true, false);
        }

    }


    private void createNewAccount() {

        final String fname = firstName.getText().toString().trim();
        final String lname = lastName.getText().toString().trim();
        final String em = userEmailET.getText().toString().trim();
        final String pwd = userPwd.getText().toString().trim();
        String vPwd = verifyPwd.getText().toString().trim();

        if (fieldChecking(fname, lname, em, pwd, vPwd)) {
            if (!checkPwd(pwd)) {
                pwdMessage(true, true);
            } else if (!checkName(fname, lname)) {
                ;
            } else if (pwdmatch(pwd, vPwd)) {
                mProgressDialog.setMessage("Creating Account...");
                mProgressDialog.show();
                mAuth.createUserWithEmailAndPassword(em, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userid = mAuth.getCurrentUser().getUid();
                            String emptyImg = "";
                            final FirebaseUser user = mAuth.getCurrentUser();
                            Map userInfo = new HashMap();
                            Map drInfo = new HashMap();

                            if (isStatus)
                                uMode = "Rider";
                            else
                                uMode = "Driver";

                            //store fname to Shared pref
                            storeFNametoSharedPref(fname, userid);

                            userInfo.put(Constants.USERID, userid);
                            userInfo.put(Constants.USERMODE, uMode);
                            userInfo.put(Constants.FIRSTNAME, fname);
                            userInfo.put(Constants.LASTNAME, lname);
                            userInfo.put(Constants.EMAIL, em);
                            userInfo.put(Constants.USEREMAILVERIFIED, String.valueOf
                                    (user.isEmailVerified()));
                            drInfo.put(Constants.EMAIL, em);
                            drInfo.put(Constants.USER_RATING, "No Feedback");
                            Map writeInfo = new HashMap();
                            writeInfo.put(Constants.USER + "/" + userid + "/" +
                                    Constants.PROFILE + "/", userInfo);


                            if (uMode.equals(Constants.DRIVER)) {
                                mPushKey = FirebaseDatabase.getInstance().getReference
                                        (Constants.DRIVER + "/" + userid + "/").push()
                                        .getKey();
                                drInfo.put(Constants.GEOKEY, mPushKey);
                                writeInfo.put(Constants.DRIVER + "/" + userid + "/",
                                        drInfo);
                            } else {
                                writeInfo.put(Constants.RIDER + "/" + userid + "/",
                                        drInfo);
                                mPushKey = FirebaseDatabase.getInstance().getReference
                                        (Constants.RIDER + "/" + userid + "/").push()
                                        .getKey();
                                drInfo.put(Constants.GEOKEY, mPushKey);
                                writeInfo.put(Constants.RIDER + "/" + userid + "/",
                                        drInfo);
                            }

                            mDatabaseReference.updateChildren(writeInfo, new
                                    DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       DatabaseReference
                                                               databaseReference) {
                                    if (databaseError != null) {
                                        Log.wtf("Write Error", databaseError.getMessage
                                                ());
                                        Toast.makeText(RegisterActivity.this, Constants.ERR_CRT_ACT, Toast.LENGTH_LONG).show();
                                        clearEditText();
                                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void>
                                                                           task) {
                                                if (!task.isSuccessful()) {
                                                    Log.wtf("Non-Deleted User Account",
                                                            task.getException()
                                                                    .getMessage());
                                                }
                                            }
                                        });
                                    } else {
                                        user.sendEmailVerification();
                                        Toast.makeText(RegisterActivity.this,
                                                "Verification email sent to " + user
                                                        .getEmail(), Toast.LENGTH_LONG)
                                                .show();
                                        UserDataHelper.saveUserInfo
                                                (getApplicationContext(), user.getEmail
                                                        (), pwd, uMode);
                                        if (isStatus) {
                                            gotoActivity(RiderActivity.class);
                                        } else {
                                            Intent intent = new Intent("PREF");
                                            sendBroadcast(intent);
                                            gotoActivity(DriverActivity.class);
                                        }
                                    }

                                }
                            });
                        } else if (task.getException() instanceof
                                FirebaseAuthUserCollisionException) {
                            Toast.makeText(RegisterActivity.this, "Registration Failed." +
                                    " Email exist!", Toast.LENGTH_LONG).show();
                        } else if (task.getException() instanceof
                                FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(RegisterActivity.this, "Registration Failed." +
                                    " Email incorrect!", Toast.LENGTH_LONG).show();
                            userEmailET.setError("Email address is required");
                        } else if (task.getException() instanceof
                                FirebaseAuthWeakPasswordException) {
                            // This code should never run (Error checking purposes)
                            Toast.makeText(RegisterActivity.this, "Registration Failed." +
                                    " Weak password!", Toast.LENGTH_LONG).show();
                            pwdMessage(true, true);
                        } else {
                            Log.wtf("Authentication creation error", task.getException
                                    ().getMessage());
                        }

                        mProgressDialog.dismiss();
                    }
                });
            }
        }
    }

    //private void insertToDatabase(String userId, DatabaseReference
    // mDatabaseReference, String fname, String lname, Uri resultUri) {
    private void insertToDatabase(DatabaseReference mDatabaseReference, String userId,
                                  String mode, String email, String fname, String lname) {

        // TODO: user profile image.

        /*************************************************************************
         //when mUser is stored in the DB, it will be the "Profile's" children.
         // Firebase Data Structure:
         //  User
         //      -> userId
         //                  -> Profile
         //                              --> userEmail
         //                              --> userFirstname
         //                              --> userLastname
         //                              --> userId
         //                              --> userImage
         //                              --> userMode
         ***************************************************************************/
        DatabaseReference currentUserDB = mDatabaseReference.child(Constants.USER)
                .child(Constants.USERID).child(Constants.PROFILE);
        User mUser = new User(userId, mode, fname, lname, email);
        currentUserDB.setValue(mUser);


        /***************************************************************************
         //store an instance of the user to a different node "Rider" or "Driver"
         //Structure:
         //  Rider or Driver
         //                  -> userId
         //                               --> userEmail:email
         ****************************************************************************/
        HashMap<String, String> userInfo = new HashMap<>();
        userInfo.put(Constants.EMAIL, email);
        if (mode.equals(Constants.DRIVER)) {
            DatabaseReference currUdB = mDbRef.child(Constants.DRIVER).child(userId);
            currUdB.setValue(userInfo);

        } else {
            DatabaseReference currUdB = mDbRef.child(Constants.RIDER).child(userId);
            currUdB.setValue(userInfo);
        }
    }


    private void gotoActivity(Class activityClass) {
        mProgressDialog.dismiss();
        startActivity(new Intent(RegisterActivity.this, activityClass));
        finish();
    }

    private void initWidgets() {
        firstName = (EditText) findViewById(R.id.firstNameET_reg);
        lastName = (EditText) findViewById(R.id.lastNameET_reg);
        userEmailET = (EditText) findViewById(R.id.emailET_reg);
        userPwd = (EditText) findViewById(R.id.passwrodET_reg);
        verifyPwd = (EditText) findViewById(R.id.verifyPwdET_reg);
        registerBtn = (Button) findViewById(R.id.registerBtn_reg);
        loginLinkTV = (TextView) findViewById(R.id.loginLinkTV_reg);
        scrollView = (ScrollView) findViewById(R.id.scrollView_reg);
        toggleButton = (ToggleButton) findViewById(R.id.toggleBtn_log);
        scrollView = (ScrollView) findViewById(R.id.scrollView_reg);
    }

    private void clearEditText() {
        firstName.setText("");
        lastName.setText("");
        userEmailET.setText("");
        userPwd.setText("");
        verifyPwd.setText("");
    }

    private boolean checkPwd(String pwd) {
        return (pwd.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{3,}") && pwd.length()
                >= 8);
    }

    private boolean checkName(String fname, String lname) {
        boolean flag = true;
        if (fname.matches("(.*[0-9].*)|(.*[@#$%^&+=.{}(),\"].*)|(.*[\\s].*)")) {
            firstName.setError("Name can't include numbers ,special characters, or " +
                    "spaces");
            flag = false;
        }
        if (lname.matches("(.*[0-9].*)|(.*[@#$%^&+={}(),\"].*)|(.*[\\s].*)")) {
            lastName.setError("Name can't include numbers ,special characters or spaces");
            flag = false;
        }

        return flag;
    }

    private void pwdMessage(boolean userpwd, boolean vpwd) {
        String errorMsg = "Password must be at least 8 characters " + "containing at " +
                "least one of each: lower case (a-z), upper case (A-Z), number (0-9)";

        if (userpwd) {
            userPwd.setError(errorMsg);
        }
        if (vpwd) {
            verifyPwd.setError(errorMsg);
        }

    }

    private boolean pwdmatch(String pwd, String vpwd) {
        if (!pwd.equals(vpwd)) {
            userPwd.setError("Passwords don't match!");
            verifyPwd.setError("Passwords don't match!");
            return false;
        } else
            return true;
    }

    private boolean fieldChecking(String fname, String lname, String em, String pwd,
                                  String vpwd) {
        boolean flag = true;

        if (fname.isEmpty()) {
            firstName.setError("First name is required");
            flag = false;
        }
        if (lname.isEmpty()) {
            lastName.setError("Last name is required");
            flag = false;
        }
        if (em.isEmpty()) {
            userEmailET.setError("Email address is required");
            flag = false;
        }
        if (pwd.isEmpty()) {
            pwdMessage(true, false);
            flag = false;
        }
        if (vpwd.isEmpty()) {
            pwdMessage(false, true);
            flag = false;
        }

        return flag;
    }

    //function to store the firstname to shared preference
    //so we dont have to call firebase for it
    private void storeFNametoSharedPref(String fname, String uid) {
        SharedPreferences sf = getSharedPreferences(Constants.SF_UNAME_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString(uid, fname);
        editor.apply();
    }

}


