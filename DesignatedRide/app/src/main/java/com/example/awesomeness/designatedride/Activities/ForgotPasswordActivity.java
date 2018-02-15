package com.example.awesomeness.designatedride.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {
    public static final String TAG = "ForgotPasswordActivity";

    //Widgets
    private EditText userEmail;
    private Button loginBtn;
    private ProgressDialog mProgressDialog;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;

    private String _User = "User";
    private String _UserEmail = "userEmail";
    private String _Profile = "Profile";

    // TODO: change button, to say Submit rather than login. And a back button to go to home page.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        initWidgets();

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();

        if(TextUtils.isEmpty(userEmail.getText().toString().trim())) { userEmail.setError("Enter email address");}

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = userEmail.getText().toString().trim();

                if (!email.isEmpty()) {
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this,"Password reset email sent to " + email, Toast.LENGTH_LONG).show();
                                gotoActivity(LoginActivity.class,true);
                            }
                            else {
                                if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                    Log.e("Password reset not sent", task.getException().getMessage());
                                    Toast.makeText(ForgotPasswordActivity.this, "Email address doesn't exist!", Toast.LENGTH_LONG).show();
                                }
                                if (task.getException() instanceof FirebaseTooManyRequestsException) {
                                    Log.e("Unusual activity.", task.getException().getMessage());
                                    Toast.makeText(ForgotPasswordActivity.this,"Unusual activity, please try again later.",Toast.LENGTH_LONG).show();
                                }
                                else
                                    Log.wtf("Password reset not sent",task.getException().getMessage());
                            }
                        }
                    });
                }
                else
                    userEmail.setError("Email address is required.");
            }
        });


    }//End of onCreate

    private void initWidgets() {
        userEmail = (EditText) findViewById(R.id.userEmailET_log);
        loginBtn = (Button) findViewById(R.id.userLoginBtn_log);
        mProgressDialog = new ProgressDialog(this);
    }

    private void clearEditText() {
        userEmail.setText("");
    }

    private void gotoActivity(Class activityClass, boolean isDismiss) {

        if (isDismiss) {
            mProgressDialog.dismiss();
        }
        startActivity(new Intent(ForgotPasswordActivity.this, activityClass));
        finish();
    }
}
