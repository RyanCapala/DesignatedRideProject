package com.example.awesomeness.designatedride.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.example.awesomeness.designatedride.Model.User;
import com.example.awesomeness.designatedride.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

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
    private ProgressDialog mProgressDialog;
    private Uri resultUri = null;
    private boolean isStatus;   // toggle position for rider or driver
    //private StorageReference mFirebaseStorage; //will be used for image storage
    //public static final int GALLERY_CODE = 1; //will be used for images


    //Strings below needs to be exact same as the database child
    private String _User = "User";
    private String _Profile = "Profile";
    private String _Rider = "Rider";
    private String _Driver = "Driver";
    private String _UserEmail = "userEmail";

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

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(RegisterActivity.this, "Rider", Toast.LENGTH_LONG).show();
                    isStatus = isChecked;
                } else {
                    Toast.makeText(RegisterActivity.this, "Driver", Toast.LENGTH_LONG).show();
                    isStatus = isChecked;
                }
            }
        });


    }

    private void createNewAccount() {

        final String fname = firstName.getText().toString().trim();
        final String lname = lastName.getText().toString().trim();
        final String em = userEmailET.getText().toString().trim();
        final String pwd = userPwd.getText().toString().trim();
        String vPwd = verifyPwd.getText().toString().trim();


        if (!TextUtils.isEmpty(fname) && !TextUtils.isEmpty(lname) &&
                !TextUtils.isEmpty(em) && !TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(vPwd)) {
            if (pwd.equals(vPwd)) {

                mProgressDialog.setMessage("Creating Account...");
                mProgressDialog.show();
                mAuth.createUserWithEmailAndPassword(em, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String userid = mAuth.getCurrentUser().getUid();
                            String emptyImg = "";
                            if (isStatus) {
                                uMode = "Rider";
                                insertToDatabase(mDatabaseReference, userid, uMode, em, fname,
                                        lname);
                                gotoActivity(RiderActivity.class);
                            } else {
                                uMode = "Driver";
                                insertToDatabase(mDatabaseReference, userid, uMode, em, fname,
                                        lname);
                                gotoActivity(DriverActivity.class);
                            }

                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration Failed. Email exist!", Toast.LENGTH_LONG).show();
                        }

                        mProgressDialog.dismiss();
                    }
                });

            } else {
                Toast.makeText(RegisterActivity.this, "Password doesn't match!",
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    //private void insertToDatabase(String userId, DatabaseReference mDatabaseReference, String fname, String lname, Uri resultUri) {
    private void insertToDatabase(DatabaseReference mDatabaseReference, String userId, String mode,
                                  String email, String fname, String lname) {

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
        DatabaseReference currentUserDB = mDatabaseReference.child(_User).child(userId);
        User mUser = new User(userId, mode, fname, lname, email);
        currentUserDB.child(_Profile).setValue(mUser);

        /***************************************************************************
        //store an instance of the user to a different node "Rider" or "Driver"
        //Structure:
        //  Rider or Driver
        //                  -> userId
        //                               --> userEmail:email
        ****************************************************************************/
        HashMap<String, String> userInfo = new HashMap<>();
        userInfo.put(_UserEmail, email);
        if (mode.equals(_Driver)) {
            DatabaseReference currUdB = mDbRef.child(_Driver);
            currUdB.child(userId).setValue(userInfo);

        } else {
            DatabaseReference currUdB = mDbRef.child(_Rider);
            currUdB.child(userId).setValue(userInfo);
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
    }


}
