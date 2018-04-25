package com.example.awesomeness.designatedride.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by awesome on 2/25/18.
 */

public class ProfileHelper {

    private static final String TAG = "ProfileHelper";
    //Firebase
    private DatabaseReference mDatabaseReference, mDatabaseReference2;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private Context ctx;
    private HashMap<String, String> childMap;
    private String child_fname, child_lname, child_email, child_mode;

    private EditText firstNameET, lastNameET, emailET;

    //=========================
    private TextView fullName_TV, userAddress_TV;
    private TextView firstName_TV, lastName_TV, age_TV, email_TV, phone_TV;
    private TextView insurance_TV, coverage_TV, doctorname_TV;
    private TextView userName_TV, wheelchair_TV;
    private CircleImageView profileImage;
    private FloatingActionButton editProfile_fab;
    //=========================

    public ProfileHelper(DatabaseReference mDatabaseReference, FirebaseUser mUser, HashMap<String, String> childMap,Context ctx, EditText firstNameET, EditText lastNameET, EditText emailET) {
        this.mDatabaseReference = mDatabaseReference;
        this.mUser = mUser;
        this.childMap = childMap;
        this.ctx = ctx;
        this.firstNameET = firstNameET;
        this.lastNameET = lastNameET;
        this.emailET = emailET;
        mDatabaseReference2 = mDatabaseReference;
    }

    public ProfileHelper(DatabaseReference mDatabaseReference,
                         FirebaseUser mUser,
                         Context ctx,
                         HashMap<String, String> childMap,
                         TextView userAddress_TV,
                         TextView fullName_TV,
                         TextView firstName_TV,
                         TextView lastName_TV,
                         TextView age_TV,
                         TextView email_TV,
                         TextView phone_TV,
                         TextView insurance_TV,
                         TextView coverage_TV,
                         TextView doctorname_TV,
                         TextView userName_TV,
                         TextView wheelchair_TV,
                         CircleImageView profileImage) {
        this.mDatabaseReference = mDatabaseReference;
        this.mUser = mUser;
        this.ctx = ctx;
        this.childMap = childMap;
        this.fullName_TV = fullName_TV;
        this.userAddress_TV = userAddress_TV;
        this.firstName_TV = firstName_TV;
        this.lastName_TV = lastName_TV;
        this.age_TV = age_TV;
        this.email_TV = email_TV;
        this.phone_TV = phone_TV;
        this.insurance_TV = insurance_TV;
        this.coverage_TV = coverage_TV;
        this.doctorname_TV = doctorname_TV;
        this.userName_TV = userName_TV;
        this.wheelchair_TV = wheelchair_TV;
        this.profileImage = profileImage;
    }

    //----------------------------------------------------------------------------------------------
    public void populateUserInfo() {

        mDatabaseReference
                .child(Constants.USER)
                .child(mUser.getUid())
                .child(Constants.PROFILE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            String key = d.getKey();
                            String val = (String) d.getValue();
                            childMap.put(key, val);
                        }

                        child_fname = childMap.get(Constants.FIRSTNAME);
                        child_lname = childMap.get(Constants.LASTNAME);
                        //child_email = childMap.get(Constants.EMAIL);
                        child_mode  = childMap.get(Constants.USERMODE);
                        firstNameET.setText(child_fname);
                        lastNameET.setText(child_lname);
                        //emailET.setText(child_email);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        child_email = mUser.getEmail();
        emailET.setText(child_email);

    }

    public void populateUserInfo1() {
        mDatabaseReference
                .child(Constants.USER)
                .child(mUser.getUid())
                .child(Constants.PROFILE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            String key = d.getKey();
                            String val = (String) d.getValue();
                            childMap.put(key, val);
                        }

                        //set TextView
                        fullName_TV.setText(childMap.get(Constants.FULLNAME));
                        firstName_TV.setText(childMap.get(Constants.FIRSTNAME));
                        lastName_TV.setText(childMap.get(Constants.LASTNAME));
                        userName_TV.setText(childMap.get(Constants.USERNAME));
                        userAddress_TV.setText(childMap.get(Constants.ADDRESS));
                        age_TV.setText(childMap.get(Constants.AGE));
                        email_TV.setText(childMap.get(Constants.EMAIL));
                        phone_TV.setText(childMap.get(Constants.PHONE));
                        insurance_TV.setText(childMap.get(Constants.INSURANCE));
                        coverage_TV.setText(childMap.get(Constants.COVERAGE));
                        doctorname_TV.setText(childMap.get(Constants.DOCTORNAME));
                        wheelchair_TV.setText(childMap.get(Constants.WHEELCHAIR));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    //----------------------------------------------------------------------------------------------
    public void updateAccount() {
        Checker checker = new Checker();
        String fname = firstNameET.getText().toString().trim();
        String lname = lastNameET.getText().toString().trim();
        String email = emailET.getText().toString().trim();


        DatabaseReference dbref = mDatabaseReference
                .child(Constants.USER)
                .child(mUser.getUid())
                .child(Constants.PROFILE);


        String itemsUpdated = "";

        if (checker.fieldCheckingNoPwd(fname, lname, email, firstNameET, lastNameET, emailET)) {
            if (checker.checkName(fname, lname, firstNameET, lastNameET)) {

                if (!checker.compareString(fname, child_fname)) {
                    dbref.child(Constants.FIRSTNAME).setValue(fname);
                    itemsUpdated = itemsUpdated + " \nfirstName: " + fname;

                    //Store updated fname to Shared Pref
                    storeFNametoSharedPref(fname, mUser.getUid());
                }

                if (!checker.compareString(lname, child_lname)) {
                    dbref.child(Constants.LASTNAME).setValue(lname);
                    itemsUpdated = itemsUpdated + " \nlastName: " + lname;
                }

            }

            if (!checker.compareString(email, child_email)) {
                if (checker.validateEmail(email)) {
                    dbref.child(Constants.EMAIL).setValue(email);
                    mUser.updateEmail(email);
                    itemsUpdated = itemsUpdated + " \nemail: " + email;

                    //check the 'userMode' so we can also update the rider/driver data
                    if (child_mode.equals(Constants.RIDER)) {
                        //update the Rider email data
                        DatabaseReference dbref2 = mDatabaseReference2
                                .child(Constants.RIDER)
                                .child(mUser.getUid());

                        dbref2.child(Constants.EMAIL).setValue(email);

                    } else if (child_mode.equals(Constants.DRIVER)) {
                        //update the Driver email data
                        DatabaseReference dbref2 = mDatabaseReference2
                                .child(Constants.DRIVER)
                                .child(mUser.getUid());

                        dbref2.child(Constants.EMAIL).setValue(email);
                    }

                } else {
                    emailET.setError(Constants.ERR_EMAIL_PATTERN);
                }

            }

            if (!itemsUpdated.isEmpty()) {
                Toast.makeText(ctx, Constants.ACCT_UPDATED + itemsUpdated, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ctx, Constants.NO_UPDATE, Toast.LENGTH_LONG).show();
            }

        }


    }

    //----------------------------------------------------------------------------------------------
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)ctx.getSystemService(Context
                .INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //----------------------------------------------------------------------------------------------
    private void storeFNametoSharedPref(String fname, String uid) {
        //SharedPreferences sf = getSharedPreferences(Constants.SF_UNAME_PREF, Context
        // .MODE_PRIVATE);
        SharedPreferences sf = ctx.getSharedPreferences(Constants.SF_UNAME_PREF, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString(uid, fname);
        editor.apply();
    }

}
