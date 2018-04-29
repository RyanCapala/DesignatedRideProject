package com.example.awesomeness.designatedride._RiderActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.awesomeness.designatedride.R;
import com.example.awesomeness.designatedride.util.Checker;
import com.example.awesomeness.designatedride.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class RiderEditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";

    private Context ctx = RiderEditProfileActivity.this;
    private EditText fname_et, lname_et;
    private String birthMonth_str, birthDay_str, birthYear_str;
    private EditText streetAddress_et, cityAndState_et;
    private EditText email_et, v_email_et, password_et, v_password_et, phone_et;
    private EditText insurance_et, doctorsname_et, username_et;
    private ImageButton saveBtn, cancelBtn;
    private RadioGroup radioGroup;
    private RadioButton yes_radioBtn, no_radioBtn, radioButton;
    private Spinner monthSpinner, daySpinner, yearSpinner;


    //Firebase
    private DatabaseReference mDdatabaseRef, mDbRef_gv, mDbRef_sv;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    //================
    private HashMap<String, String> childMap;
    private String[] monthArray, dayArray, yearArray;
    private String wheelChairAccess;
    private String new_selected_radio_btn;

    private int monthPosition, dayPosition, yearPosition;

    private Checker checker;
    private View parentView;

    //==========================
    private String child_fname;
    private String child_lname;
    private String child_fullName;
    private String child_streetAdd;
    private String child_cityAndState;
    private String child_fullAddress;
    private String child_email;
    private String child_v_email;
    private String child_password;
    private String child_v_password;
    private String child_phone;
    private String child_insurance;
    private String child_doctor;
    private String child_username;
    private String child_mode;
    private int child_bMonth_pos, child_bDay_pos, child_bYear_pos;
    private String strchild_bMonth_pos, strchild_bDay_pos, strchild_bYear_pos;

    private String child_age;
    private String child_wheelchairNeeded;
    //==========================

    private String str_age;
    private String itemsUpdated = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_edit_profile);

        //-- Back Arrow at the ActionBar--//
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        initWidgets();
        initFirebase();

        new_selected_radio_btn = getSelectedRB();
        Log.d(TAG, "onCreate: selectedRB " + new_selected_radio_btn);

        //populate editText fields
        getStoredValues();


        monthArray = getResources().getStringArray(R.array.month);
        dayArray = getResources().getStringArray(R.array.day);
        yearArray = getResources().getStringArray(R.array.year);
        getSpinnerValues();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUpdatedInfo();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(RiderProfileActivity.class);
            }
        });


    }//End onCreate



    //===== For back arrow ======//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goToActivity(RiderProfileActivity.class);
        }
        return super.onOptionsItemSelected(item);
    }


    private void getStoredValues() {
        mDbRef_gv
                .child(Constants.USER)
                .child(mUser.getUid())
                .child(Constants.PROFILE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            String key = d.getKey();
                            String value = (String) d.getValue();
                            childMap.put(key, value);
                        }
                        setChildValues(childMap);
                        setFields();
                        setRadioButton();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

//        mDbRef_gv
//                .child(Constants.USER)
//                .child(mUser.getUid())
//                .child(Constants.PROFILE)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for (DataSnapshot d : dataSnapshot.getChildren()) {
//                            String key = d.getKey();
//                            String value = (String) d.getValue();
//                            childMap.put(key, value);
//                        }
//                        setChildValues(childMap);
//                        setFields();
//                        setRadioButton();
//                        Log.d(TAG, "onDataChange: child radioBtn " + child_wheelchairNeeded );
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });



    }

    private void setChildValues(HashMap<String, String> childMap) {

        child_fname = childMap.get(Constants.FIRSTNAME);
        child_lname = childMap.get(Constants.LASTNAME);
        child_fullName = childMap.get(Constants.FULLNAME);
        child_streetAdd = childMap.get(Constants.ADDRESS);
        child_cityAndState = childMap.get(Constants.CITYANDSTATE);
        child_fullAddress = childMap.get(Constants.FULL_ADDRESS);
        child_email = childMap.get(Constants.EMAIL);
        child_phone = childMap.get(Constants.PHONE);
        child_insurance = childMap.get(Constants.INSURANCE);
        child_doctor = childMap.get(Constants.DOCTORNAME);
        child_username = childMap.get(Constants.USERNAME);
        child_mode = childMap.get(Constants.USERMODE);
        child_age = childMap.get(Constants.AGE);
        child_wheelchairNeeded = childMap.get(Constants.WHEELCHAIR);
        strchild_bMonth_pos = childMap.get(Constants.BIRTH_MONTH_POS);
        strchild_bDay_pos = childMap.get(Constants.BIRTH_DAY_POS);
        strchild_bYear_pos = childMap.get(Constants.BIRTH_YEAR_POS);

        setLocationOfSpinnerFromDataPos(convertToInt(strchild_bMonth_pos), convertToInt
                (strchild_bDay_pos), convertToInt(strchild_bYear_pos));


    }

    private void setFields() {
        fname_et.setText(child_fname);
        lname_et.setText(child_lname);
        streetAddress_et.setText(child_streetAdd);
        cityAndState_et.setText(child_cityAndState);
        email_et.setText(child_email);
        v_email_et.setText(child_email);
        phone_et.setText(child_phone);
        insurance_et.setText(child_insurance);
        doctorsname_et.setText(child_doctor);
        username_et.setText(child_username);

    }

    //============================================================================================//
    /*
    This version of 'saveUpdatedInfo()' checks if each fields if there are any changes, and
    stores it to firebase.
     */
//    private void saveUpdatedInfo() {
//        new_selected_radio_btn = getSelectedRB();
//        Log.d(TAG, "saveUpdatedInfo: new_selected_RB = " + new_selected_radio_btn);
//
//        str_age = getAge(String.valueOf(monthPosition), String.valueOf(dayPosition),
//                String.valueOf(yearPosition));
//
//        //Get Strings
//        String fname = fname_et.getText().toString().trim();
//        String lname = lname_et.getText().toString().trim();
//        String streetAdd = streetAddress_et.getText().toString().trim();
//        String cityAndState = cityAndState_et.getText().toString().trim();
//        String email = email_et.getText().toString().trim();
//        String v_email = v_email_et.getText().toString().trim();
//        String password = password_et.getText().toString().trim();
//        String v_password = v_password_et.getText().toString().trim();
//        String phone = phone_et.getText().toString().trim();
//        String insurance = insurance_et.getText().toString().trim();
//        String doctor = doctorsname_et.getText().toString().trim();
//        String username = username_et.getText().toString().trim();
//
//        int bMonth_pos = monthPosition;
//        int bDay_pos = dayPosition;
//        int bYear_pos = yearPosition;
//
//        String fullAddress = streetAdd + "\n" + cityAndState;
//        String fullname = fname + " " + lname;
//
//        String str_bMonth_pos = convertToString(bMonth_pos);
//        String str_bDay_pos = convertToString(bDay_pos);
//        String str_bYear_pos = convertToString(bYear_pos);
//
//
//        wheelChairAccess = new_selected_radio_btn;
//
//        DatabaseReference dbref = mDbRef_sv
//                .child(Constants.USER)
//                .child(mUser.getUid())
//                .child(Constants.PROFILE);
//
//        if (fieldChecking(fname, lname, streetAdd, cityAndState, email, v_email, phone, insurance, doctor, username)) {
//
//            //=== Update fname and lname ===//
//            //Check if name doesnt have special characters
//            if (checker.checkName(fname, lname, fname_et, lname_et)) {
//                // If fname != child_fname, then save fname
//                if (!checker.compareString(fname, child_fname)) {
//                    dbref.child(Constants.FIRSTNAME).setValue(fname);
//                    itemsUpdated = itemsUpdated + "\n" + Constants.FIRSTNAME + fname;
//                }
//                if (!checker.compareString(lname, child_lname)) {
//                    dbref.child(Constants.LASTNAME).setValue(lname);
//                    itemsUpdated = itemsUpdated + "\n" + Constants.LASTNAME + lname;
//                }
//
//                // Update full name
//                if (!checker.compareString(fullname, child_fullName)) {
//                    dbref.child(Constants.FULLNAME).setValue(fullname);
//                }
//            }
//
//            //=== Update email ===//
//            if (!checker.compareString(email, child_email)) {
//                if (checker.validateEmail(email)) {
//                    if (checker.compareString(email, v_email)) {
//
//                        //--- To update the 'User Profile' Branch email accoount --//
//                        dbref.child(Constants.EMAIL).setValue(email);
//                        itemsUpdated = itemsUpdated + "\n" + Constants.EMAIL;
//
//                        //--- To update the firebase email account----//
//                        mUser.updateEmail(email);
//
//                        //--- We also need to update the email in the rider/driver Branch --//
//                        if (child_mode.equals(Constants.RIDER)) {
//                            DatabaseReference dref = mDdatabaseRef
//                                    .child(Constants.RIDER)
//                                    .child(mUser.getUid());
//                            dref.child(Constants.EMAIL).setValue(email);
//                        } else if (child_mode.equals(Constants.DRIVER)) {
//                            DatabaseReference dref = mDdatabaseRef
//                                    .child(Constants.DRIVER)
//                                    .child(mUser.getUid());
//                            dref.child(Constants.EMAIL).setValue(email);
//                        }
//
//                    } else {
//                        email_et.setError(Constants.ERR_EMAIL_MATCH);
//                        v_email_et.setError(Constants.ERR_EMAIL_MATCH);
//                    }
//                } else {
//                    email_et.setError(Constants.ERR_EMAIL_PATTERN);
//                }
//            }
//
//            //=== Update Address ===//
//            if (!checker.compareString(streetAdd, child_streetAdd)) {
//                dbref.child(Constants.ADDRESS).setValue(streetAdd);
//                itemsUpdated = itemsUpdated + "\n" + Constants.ADDRESS;
//            }
//
//            //=== Update City and State ==//
//            if (!checker.compareString(cityAndState, child_cityAndState)) {
//                dbref.child(Constants.CITYANDSTATE).setValue(cityAndState);
//                itemsUpdated = itemsUpdated + "\n" + Constants.CITYANDSTATE;
//            }
//
//            //=== Update Full Address ===//
//            if (!checker.compareString(fullAddress, child_fullAddress)) {
//                dbref.child(Constants.FULL_ADDRESS).setValue(fullAddress);
//                itemsUpdated = itemsUpdated + "\n" + Constants.FULL_ADDRESS;
//            }
//
//            //=== Update Phone number ===//
//            if (!checker.compareString(phone, child_phone)) {
//                dbref.child(Constants.PHONE).setValue(phone);
//                itemsUpdated = itemsUpdated + "\n" + Constants.PHONE;
//            }
//
//            //=== Update Insurance ===//
//            if (!checker.compareString(insurance, child_insurance)) {
//                dbref.child(Constants.INSURANCE).setValue(insurance);
//                itemsUpdated = itemsUpdated + "\n" + Constants.INSURANCE;
//            }
//
//            //=== Update Doctor's Name ===//
//            if (!checker.compareString(doctor, child_doctor)) {
//                dbref.child(Constants.DOCTORNAME).setValue(doctor);
//                itemsUpdated = itemsUpdated + "\n" + Constants.DOCTORNAME;
//            }
//
//            //=== Update Username ===//
//            if (!checker.compareString(username, child_username)) {
//                dbref.child(Constants.USERNAME).setValue(username);
//                itemsUpdated = itemsUpdated + "\n" + Constants.USERNAME;
//            }
//
//
//        }
//
//        //=== Update Password if User enters a password, else Ignore empty field
//        if (!password.isEmpty() || !v_password.isEmpty()) {
//            if (checker.pwdFieldChecking(password, v_password, password_et, v_password_et)) {
//                if (checker.pwdmatch(password, v_password, password_et, v_password_et)) {
//                    if (checker.checkPwd(password) && checker.checkPwd(v_password)) {
//                        mUser.updatePassword(password);
//                        itemsUpdated = itemsUpdated + "\nPassword";
//                    }
//                }
//            }
//        }
//
//        //=== Update DOB spinner values ===//
//        if (!checker.compareString(str_bMonth_pos, strchild_bMonth_pos)) {
//            dbref.child(Constants.BIRTH_MONTH_POS).setValue(str_bMonth_pos);
//        }
//
//        if (!checker.compareString(str_bDay_pos, strchild_bDay_pos)) {
//            dbref.child(Constants.BIRTH_DAY_POS).setValue(str_bDay_pos);
//        }
//
//        if (!checker.compareString(str_bYear_pos, strchild_bYear_pos)) {
//            dbref.child(Constants.BIRTH_YEAR_POS).setValue(str_bYear_pos);
//        }
//
//        //=== Store Age ===//
//        if (!str_age.isEmpty()) {
//            if (!checker.compareString(str_age, child_age)) {
//                dbref.child(Constants.AGE).setValue(str_age);
//                itemsUpdated = itemsUpdated + "\n" + Constants.AGE;
//            }
//        }
//
//        //=== Store Wheelchair access ===//
//        if (!wheelChairAccess.isEmpty()) {
//            if (!checker.compareString(wheelChairAccess, child_wheelchairNeeded)) {
//                dbref.child(Constants.WHEELCHAIR).setValue(wheelChairAccess);
//                itemsUpdated = itemsUpdated + "\nWheelchair access";
//            }
//
//        }
//
//        if (!itemsUpdated.isEmpty()) {
//            Toast.makeText(RiderEditProfileActivity.this, Constants.ACCT_UPDATED + itemsUpdated, Toast
//                    .LENGTH_LONG).show();
//        } else {
//            Toast.makeText(getApplicationContext(), Constants.NO_UPDATE, Toast.LENGTH_LONG).show();
//        }
//
//    }
    //============================================================================================//


    //============================================================================================//
    /*
    This version of 'saveUpdatedInfo()' stores the edited fields into a Hashmap, and stores the
    Hashmap into firebase at the end.
     */
    private void saveUpdatedInfo() {

        Map profile = new HashMap();
        new_selected_radio_btn = getSelectedRB();
        Log.d(TAG, "saveUpdatedInfo: new_selected_RB = " + new_selected_radio_btn);

        str_age = getAge(String.valueOf(monthPosition), String.valueOf(dayPosition),
                String.valueOf(yearPosition));

        //Get Strings
        String fname = fname_et.getText().toString().trim();
        String lname = lname_et.getText().toString().trim();
        String streetAdd = streetAddress_et.getText().toString().trim();
        String cityAndState = cityAndState_et.getText().toString().trim();
        String email = email_et.getText().toString().trim();
        String v_email = v_email_et.getText().toString().trim();
        String password = password_et.getText().toString().trim();
        String v_password = v_password_et.getText().toString().trim();
        String phone = phone_et.getText().toString().trim();
        String insurance = insurance_et.getText().toString().trim();
        String doctor = doctorsname_et.getText().toString().trim();
        String username = username_et.getText().toString().trim();

        int bMonth_pos = monthPosition;
        int bDay_pos = dayPosition;
        int bYear_pos = yearPosition;

        String fullAddress = streetAdd + "\n" + cityAndState;
        String fullname = fname + " " + lname;

        String str_bMonth_pos = convertToString(bMonth_pos);
        String str_bDay_pos = convertToString(bDay_pos);
        String str_bYear_pos = convertToString(bYear_pos);


        wheelChairAccess = new_selected_radio_btn;

        DatabaseReference dbref = mDbRef_sv
                .child(Constants.USER)
                .child(mUser.getUid())
                .child(Constants.PROFILE);

        if (fieldChecking(fname, lname, streetAdd, cityAndState, email, v_email, phone, insurance, doctor, username)) {

            //=== Update fname and lname ===//
            //Check if name doesnt have special characters
            if (checker.checkName(fname, lname, fname_et, lname_et)) {
                // If fname != child_fname, then save fname
                if (!checker.compareString(fname, child_fname)) {
                    //dbref.child(Constants.FIRSTNAME).setValue(fname);
                    profile.put(Constants.FIRSTNAME, fname);
                    itemsUpdated = itemsUpdated + "\n" + Constants.FIRSTNAME + fname;
                }
                if (!checker.compareString(lname, child_lname)) {
                    //dbref.child(Constants.LASTNAME).setValue(lname);
                    profile.put(Constants.LASTNAME, lname);
                    itemsUpdated = itemsUpdated + "\n" + Constants.LASTNAME + lname;
                }

                // Update full name
                if (!checker.compareString(fullname, child_fullName)) {
                    //dbref.child(Constants.FULLNAME).setValue(fullname);
                    profile.put(Constants.FULLNAME, fullname);
                }
            }

            //=== Update email ===//
            if (!checker.compareString(email, child_email)) {
                if (checker.validateEmail(email)) {
                    if (checker.compareString(email, v_email)) {

                        //--- To update the 'User Profile' Branch email accoount --//
                        //dbref.child(Constants.EMAIL).setValue(email);
                        profile.put(Constants.EMAIL, email);
                        itemsUpdated = itemsUpdated + "\n" + Constants.EMAIL;

                        //--- To update the firebase email account----//
                        mUser.updateEmail(email);

                        //--- We also need to update the email in the rider/driver Branch --//
                        if (child_mode.equals(Constants.RIDER)) {
                            DatabaseReference dref = mDdatabaseRef
                                    .child(Constants.RIDER)
                                    .child(mUser.getUid());
                            dref.child(Constants.EMAIL).setValue(email);
                        } else if (child_mode.equals(Constants.DRIVER)) {
                            DatabaseReference dref = mDdatabaseRef
                                    .child(Constants.DRIVER)
                                    .child(mUser.getUid());
                            dref.child(Constants.EMAIL).setValue(email);
                        }

                    } else {
                        email_et.setError(Constants.ERR_EMAIL_MATCH);
                        v_email_et.setError(Constants.ERR_EMAIL_MATCH);
                    }
                } else {
                    email_et.setError(Constants.ERR_EMAIL_PATTERN);
                }
            }

            //=== Update Address ===//
            if (!checker.compareString(streetAdd, child_streetAdd)) {
                //dbref.child(Constants.ADDRESS).setValue(streetAdd);
                profile.put(Constants.ADDRESS, streetAdd);
                itemsUpdated = itemsUpdated + "\n" + Constants.ADDRESS;
            }

            //=== Update City and State ==//
            if (!checker.compareString(cityAndState, child_cityAndState)) {
                //dbref.child(Constants.CITYANDSTATE).setValue(cityAndState);
                profile.put(Constants.CITYANDSTATE, cityAndState);
                itemsUpdated = itemsUpdated + "\n" + Constants.CITYANDSTATE;
            }

            //=== Update Full Address ===//
            if (!checker.compareString(fullAddress, child_fullAddress)) {
                //dbref.child(Constants.FULL_ADDRESS).setValue(fullAddress);
                profile.put(Constants.FULL_ADDRESS, fullAddress);
                itemsUpdated = itemsUpdated + "\n" + Constants.FULL_ADDRESS;
            }

            //=== Update Phone number ===//
            if (!checker.compareString(phone, child_phone)) {
                //dbref.child(Constants.PHONE).setValue(phone);
                profile.put(Constants.PHONE, phone);
                itemsUpdated = itemsUpdated + "\n" + Constants.PHONE;
            }

            //=== Update Insurance ===//
            if (!checker.compareString(insurance, child_insurance)) {
                //dbref.child(Constants.INSURANCE).setValue(insurance);
                profile.put(Constants.INSURANCE, insurance);
                itemsUpdated = itemsUpdated + "\n" + Constants.INSURANCE;
            }

            //=== Update Doctor's Name ===//
            if (!checker.compareString(doctor, child_doctor)) {
                //dbref.child(Constants.DOCTORNAME).setValue(doctor);
                profile.put(Constants.DOCTORNAME, doctor);
                itemsUpdated = itemsUpdated + "\n" + Constants.DOCTORNAME;
            }

            //=== Update Username ===//
            if (!checker.compareString(username, child_username)) {
                //dbref.child(Constants.USERNAME).setValue(username);
                profile.put(Constants.USERNAME, username);
                itemsUpdated = itemsUpdated + "\n" + Constants.USERNAME;
            }


        }

        //=== Update Password if User enters a password, else Ignore empty field
        if (!password.isEmpty() || !v_password.isEmpty()) {
            if (checker.pwdFieldChecking(password, v_password, password_et, v_password_et)) {
                if (checker.pwdmatch(password, v_password, password_et, v_password_et)) {
                    if (checker.checkPwd(password) && checker.checkPwd(v_password)) {
                        mUser.updatePassword(password);
                        itemsUpdated = itemsUpdated + "\nPassword";
                    }
                }
            }
        }

        //=== Update DOB spinner values ===//
        if (!checker.compareString(str_bMonth_pos, strchild_bMonth_pos)) {
            //dbref.child(Constants.BIRTH_MONTH_POS).setValue(str_bMonth_pos);
            profile.put(Constants.BIRTH_MONTH_POS, str_bMonth_pos);
            itemsUpdated = itemsUpdated + "\nMonth";
        }

        if (!checker.compareString(str_bDay_pos, strchild_bDay_pos)) {
            //dbref.child(Constants.BIRTH_DAY_POS).setValue(str_bDay_pos);
            profile.put(Constants.BIRTH_DAY_POS, str_bDay_pos);
            itemsUpdated = itemsUpdated + "\nDay";
        }

        if (!checker.compareString(str_bYear_pos, strchild_bYear_pos)) {
            //dbref.child(Constants.BIRTH_YEAR_POS).setValue(str_bYear_pos);
            profile.put(Constants.BIRTH_YEAR_POS, str_bYear_pos);
            itemsUpdated = itemsUpdated + "\nYear";
        }

        //=== Store Age ===//
        if (!str_age.isEmpty()) {
            if (!checker.compareString(str_age, child_age)) {
                //dbref.child(Constants.AGE).setValue(str_age);
                profile.put(Constants.AGE, str_age);
                itemsUpdated = itemsUpdated + "\n" + Constants.AGE;
            }
        }

        //=== Store Wheelchair access ===//
        if (!wheelChairAccess.isEmpty()) {
            if (!checker.compareString(wheelChairAccess, child_wheelchairNeeded)) {
                //dbref.child(Constants.WHEELCHAIR).setValue(wheelChairAccess);
                profile.put(Constants.WHEELCHAIR, wheelChairAccess);
                itemsUpdated = itemsUpdated + "\nWheelchair access";
            }

        }

        if (!itemsUpdated.isEmpty()) {

            dbref.updateChildren(profile, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError,
                                       DatabaseReference databaseReference) {

                    if (databaseError != null) {
                        Toast.makeText(getApplicationContext(), "Eror Updating Profile.", Toast
                                .LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RiderEditProfileActivity.this, Constants.ACCT_UPDATED + itemsUpdated, Toast
                                .LENGTH_LONG).show();
                        itemsUpdated = "";
                        //goToActivity(RiderProfileActivity.class);
                    }

                }
            });
        } else {
            Toast.makeText(getApplicationContext(), Constants.NO_UPDATE, Toast.LENGTH_LONG).show();
        }

    }
    //=====================================


    //-------Field Checking-----------//
    private boolean fieldChecking(String fname, String lname, String streetAdd, String cityAndState,
                                  String email, String v_email, String phone, String insurance,
                                  String doctor, String username) {

        boolean flag = true;
        if (fname.isEmpty()) {
            fname_et.setError("First Name Required");
            flag = false;
        }
        if (lname.isEmpty()) {
            lname_et.setError("Last name is required");
            flag = false;
        }
        if (streetAdd.isEmpty()) {
            streetAddress_et.setError("Street address required");
            flag = false;
        }
        if (cityAndState.isEmpty()) {
            cityAndState_et.setError("City and State required");
            flag = false;
        }
        if (email.isEmpty()) {
            email_et.setError("Email required");
            flag = false;
        }
        if (v_email.isEmpty()) {
            v_email_et.setError("Email required");
            flag = false;
        }
        if (phone.isEmpty()) {
            phone_et.setError("Phone number required");
            flag = false;
        }
        if (insurance.isEmpty()) {
            insurance_et.setError("Insurance required");
            flag = false;
        }
        if (doctor.isEmpty()) {
            doctorsname_et.setError("Doctor's name required");
            flag = false;
        }
        if (username.isEmpty()) {
            username_et.setError("Username required");
            flag = false;
        }

        return flag;

    }

    private void getSpinnerValues() {
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                birthMonth_str = monthArray[position];
                monthPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                birthDay_str = dayArray[position];
                dayPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                birthYear_str = yearArray[position];
                yearPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initWidgets() {
        fname_et = (EditText) findViewById(R.id.fname_et_editProf);
        lname_et = (EditText) findViewById(R.id.lname_et_editProf);
        streetAddress_et = (EditText) findViewById(R.id.address_st_et_editProf);
        cityAndState_et = (EditText) findViewById(R.id.address_ctAndState_et_editProf);
        email_et = (EditText) findViewById(R.id.email_et_editProf);
        v_email_et = (EditText) findViewById(R.id.verify_email_et_editProf);
        password_et = (EditText) findViewById(R.id.password_et_editProf);
        v_password_et = (EditText) findViewById(R.id.v_password_et_editProf);
        phone_et = (EditText) findViewById(R.id.phone_et_editProf);
        insurance_et = (EditText) findViewById(R.id.insurance_et_editProf);
        doctorsname_et = (EditText) findViewById(R.id.doctor_et_editProf);
        username_et = (EditText) findViewById(R.id.username_et_editProf);
        saveBtn = (ImageButton) findViewById(R.id.save_btn_editProf);
        cancelBtn = (ImageButton) findViewById(R.id.cancel_btn_editProf);
        radioGroup = (RadioGroup) findViewById(R.id.wheelchair_radiogroup_editProf);
        yes_radioBtn = (RadioButton) findViewById(R.id.yes_radioBtn_editProf);
        no_radioBtn = (RadioButton) findViewById(R.id.no_radioBtn_editProf);
        monthSpinner = (Spinner) findViewById(R.id.month_spinner_editProf);
        daySpinner = (Spinner) findViewById(R.id.day_spinner_editProf);
        yearSpinner = (Spinner) findViewById(R.id.year_spinner_editProf);

        parentView = findViewById(R.id.rider_view);

        checker = new Checker();
        childMap = new HashMap<>();

        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(ctx, R.array
                        .month,
                R.layout.spinner_layout);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(ctx, R.array.day,
                R.layout.spinner_layout);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);

        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(ctx, R.array.year,
                R.layout.spinner_layout);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);




    }

    private void initFirebase() {
        mDatabase = FirebaseDatabase.getInstance();
        mDdatabaseRef = mDatabase.getReference();
        mDbRef_gv = mDatabase.getReference();
        mDbRef_sv = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

    }

    private void goToActivity(Class activityClass) {
        Intent intent = new Intent(RiderEditProfileActivity.this, activityClass);
        startActivity(intent);
        finish();
    }

    //---------Radio Buttons---------//
    private String getSelectedRB() {
        String str;
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        str = (String) radioButton.getText();

        return str;

    }

    private String convertToString(int val) {
        return String.valueOf(val);
    }

    private int convertToInt(String str) {
        return Integer.valueOf(str);
    }

    private void setLocationOfSpinnerFromDataPos(int m, int d, int y) {
        monthSpinner.setSelection(m);
        daySpinner.setSelection(d);
        yearSpinner.setSelection(y);
    }


    private String getAge(String month, String day, String year) {

        int ypos = Integer.valueOf(year);
        int y = Integer.valueOf(yearArray[ypos]);
        int m = Integer.valueOf(month);
        int d = Integer.valueOf(day);

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(y, m, d);
        int yourAge = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        dob.add(Calendar.YEAR, yourAge);
        if (today.before(dob)) {
            yourAge--;
        }

        return String.valueOf(yourAge);
    }

    private void setRadioButton() {
        String yes = "Yes";
        String no = "No";
        if (child_wheelchairNeeded == null) {
            radioGroup.check(R.id.no_radioBtn_editProf);
        } else if (checker.compareString(child_wheelchairNeeded, yes)) {
            radioGroup.check(R.id.yes_radioBtn_editProf);
        } else {
            radioGroup.check(R.id.no_radioBtn_editProf);
        }

    }

}
