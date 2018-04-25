package com.example.awesomeness.designatedride.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.awesomeness.designatedride.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPasswordActivity extends AppCompatActivity {
    public static final String TAG = "ForgotPasswordActivity";

    //Widgets
    private EditText userEmailET;
    private Button loginBtn;
    private ProgressDialog mProgressDialog;
    private TextView loginLinkTV, registerLinkTV;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;


    // TODO: change button, to say Submit rather than login.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        initWidgets();

        loginLinkTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(LoginActivity.class);
            }
        });

        registerLinkTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(RegisterActivity.class);
            }
        });

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();

        // I commented this out and moved it inside the onClick, because it will set the error
        // message every time this page loads up since the email is empty when the page appears

//        if(TextUtils.isEmpty(userEmailET.getText().toString().trim())) {
//            userEmailET.setError("Enter email address");
//        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = userEmailET.getText().toString().trim();
                if(TextUtils.isEmpty(email)) {
                    userEmailET.setError("Enter email address");
                } else {
                    mProgressDialog.setMessage("Sending Password Reset Email ...");

                    if (fieldChecking(email)) {
                        mProgressDialog.show();
                        mAuth = FirebaseAuth.getInstance();
                        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotPasswordActivity.this,"Password reset email sent to " + email, Toast.LENGTH_LONG).show();
                                    gotoActivity(LoginActivity.class,true);
                                }
                                else {
                                    mProgressDialog.dismiss();
                                    if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                        //Log.e("Password reset not sent", task.getException()
                                        //        .getMessage());
                                        Toast.makeText(ForgotPasswordActivity.this, "Email address doesn't exist!", Toast.LENGTH_LONG).show();
                                    }
                                    if (task.getException() instanceof FirebaseTooManyRequestsException) {
                                        //Log.e("Unusual activity.", task.getException()
                                        //        .getMessage());
                                        Toast.makeText(ForgotPasswordActivity.this,"Unusual activity, please try again later.",Toast.LENGTH_LONG).show();
                                    }
                                    
                                }
                            }
                        });
                    }
                }

            }
        });


    }//End of onCreate

    private void initWidgets() {
        userEmailET = (EditText) findViewById(R.id.userEmailET_log);
        loginBtn = (Button) findViewById(R.id.userLoginBtn_log);
        mProgressDialog = new ProgressDialog(this);
        loginLinkTV = (TextView) findViewById(R.id.loginLinkTV_log);
        registerLinkTV = (TextView) findViewById(R.id.registerLinkTV_log);
    }

    private void clearEditText() {
        userEmailET.setText("");
    }

    private void gotoActivity(Class activityClass, boolean isDismiss) {

        if (isDismiss) {
            mProgressDialog.dismiss();
        }
        startActivity(new Intent(ForgotPasswordActivity.this, activityClass));
        finish();
    }
    private void gotoActivity(Class activityClass) {
        startActivity(new Intent(ForgotPasswordActivity.this, activityClass));
        finish();
    }

    private boolean fieldChecking(String email){
        boolean flag = true;

        if(email.isEmpty()) { userEmailET.setError("Email address is required"); flag = false;}

        return flag;
    }
}

