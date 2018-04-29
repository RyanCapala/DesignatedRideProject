package com.example.awesomeness.designatedride._DriverActivities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.awesomeness.designatedride.R;
import com.example.awesomeness.designatedride._RiderActivities.RiderEditProfileActivity;
import com.example.awesomeness.designatedride.util.Checker;
import com.example.awesomeness.designatedride.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DriverEditProfileActivity extends AppCompatActivity {
    private static final String TAG = "DrvrEditProfActivity";

    private Context context = DriverEditProfileActivity.this;
    private Class drvrProfAct = DriverProfileActivity.class;

    //----Firebase-----//
    private DatabaseReference mDbRef, dbRef_get, dbRef_set;
    private FirebaseDatabase mDataBase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    //----Widgets------//
    private EditText fname_ET, lname_ET, street_ET, ctAndSt_ET;
    private EditText email_ET, vEmail_ET, pwd_ET, vPwd_ET, phone_ET;
    private EditText cMake_ET, cModel_ET, cYear_ET, uName_ET;
    private ImageButton saveBtn, cancelBtn;
    private RadioButton radioBtn;
    private RadioGroup radioGroup;
    private Spinner m_Spinner, d_Spinner, y_Spinner;

    private Checker checker;
    private HashMap<String, String> cMap;

    //---- Strings ------//
    private String child_fname, child_lname, child_fullName, child_street, child_ctAndSt;
    private String child_email, child_vEmail, child_pwd, child_vPwd, child_phone;
    private String child_cMake, child_cModel, child_cYear, child_uName;
    private String child_month_pos, child_day_pos, child_year_pos;
    private String child_wheelchair_needed, child_mode, child_fullAddress, child_age;

    private String[] monthArray, dayArray, yearArray;
    private int mPos_int, dPos_int, yPos_int;
    private String new_selected_radio_btn;

    private String itemsUpdated = "";

    /*
     * Todo: figuring out why it keeps going to 'DriverActivity' without sending an intent to it.
     * Ill try to fix it later, but in the mean time, ill be working on CS460 Compilers. FML!!!
     */




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_edit_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        cMap = (HashMap<String, String>) intent.getSerializableExtra(Constants.DR_CHILDMAP_KEY);

        initFirebase();
        initWidgets();
        setChildValues();
        setFields();
        setStringArrayForSpinnerUse();
        getSpinnerValues();
        new_selected_radio_btn = getSelectedRB();



        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUpdatedInfo();
            }
        });

    }// End of onCreate

    //--------------------------------------------------------------------------------------------//
    // for the back Arrow
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                gotoActivity(drvrProfAct);
        }
        return super.onOptionsItemSelected(item);
    }


    //--------------------------------------------------------------------------------------------//
    private void gotoActivity(Class drvrProfAct) {
        startActivity(new Intent(context, drvrProfAct));
        finish();
    }


    //--------------------------------------------------------------------------------------------//
    private void initWidgets() {
        fname_ET = (EditText) findViewById(R.id.driver_fname_et_editProf);
        lname_ET = (EditText) findViewById(R.id.driver_lname_et_editProf);
        street_ET = (EditText) findViewById(R.id.driver_address_st_et_editProf);
        ctAndSt_ET = (EditText) findViewById(R.id.driver_address_ctAndState_et_editProf);
        email_ET = (EditText) findViewById(R.id.driver_email_et_editProf);
        vEmail_ET = (EditText) findViewById(R.id.driver_verify_email_et_editProf);
        pwd_ET = (EditText) findViewById(R.id.driver_password_et_editProf);
        vPwd_ET = (EditText) findViewById(R.id.driver_v_password_et_editProf);
        phone_ET = (EditText) findViewById(R.id.driver_phone_et_editProf);
        cMake_ET = (EditText) findViewById(R.id.driver_carmake_et_editProf);
        cModel_ET = (EditText) findViewById(R.id.driver_carmodel_et_editProf);
        cYear_ET = (EditText) findViewById(R.id.driver_caryear_et_editProf);
        uName_ET = (EditText) findViewById(R.id.driver_username_et_editProf);

        saveBtn = (ImageButton) findViewById(R.id.driver_save_btn_editProf);
        cancelBtn = (ImageButton) findViewById(R.id.driver_cancel_btn_editProf);
        radioGroup = (RadioGroup) findViewById(R.id.driver_wheelchair_radiogroup_editProf);
        m_Spinner = (Spinner) findViewById(R.id.driver_month_spinner_editProf);
        d_Spinner = (Spinner) findViewById(R.id.driver_day_spinner_editProf);
        y_Spinner = (Spinner) findViewById(R.id.driver_year_spinner_editProf);

        checker = new Checker();

        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(context, R.array
                        .month, R.layout.spinner_layout);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_Spinner.setAdapter(monthAdapter);

        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(context, R.array.day,
                R.layout.spinner_layout);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        d_Spinner.setAdapter(dayAdapter);

        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(context, R.array.year,
                R.layout.spinner_layout);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        y_Spinner.setAdapter(yearAdapter);
    }

    //--------------------------------------------------------------------------------------------//
    private void initFirebase() {

        mDataBase = FirebaseDatabase.getInstance();
        mDbRef = mDataBase.getReference();
        dbRef_get = mDataBase.getReference();
        dbRef_set = mDataBase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

    }

    //--------------------------------------------------------------------------------------------//
    private void setChildValues() {
        /*

        child_vEmail_ET, child_pwd_ET, child_vPwd_ET,

         */
        child_fname = cMap.get(Constants.FIRSTNAME);
        child_lname = cMap.get(Constants.LASTNAME);
        child_fullName = cMap.get(Constants.FULLNAME);
        child_street = cMap.get(Constants.ADDRESS);
        child_ctAndSt = cMap.get(Constants.CITYANDSTATE);
        child_email = cMap.get(Constants.EMAIL);
        child_phone = cMap.get(Constants.PHONE);
        child_cMake = cMap.get(Constants.CAR_MAKE);
        child_cModel = cMap.get(Constants.CAR_MODEL);
        child_cYear = cMap.get(Constants.CAR_YEAR);
        child_uName = cMap.get(Constants.USERNAME);
        child_month_pos = cMap.get(Constants.BIRTH_MONTH_POS);
        child_day_pos = cMap.get(Constants.BIRTH_DAY_POS);
        child_year_pos = cMap.get(Constants.BIRTH_YEAR_POS);
        child_wheelchair_needed = cMap.get(Constants.WHEELCHAIR);
        child_mode = cMap.get(Constants.USERMODE);
        child_fullAddress = cMap.get(Constants.FULL_ADDRESS);
        child_age = cMap.get(Constants.AGE);

        setLocationOfSpinnerFromDataPos(Integer.valueOf(child_month_pos), Integer.valueOf
                (child_day_pos), Integer.valueOf(child_year_pos));


    }

    //--------------------------------------------------------------------------------------------//
    private void setFields() {
        fname_ET.setText(child_fname);
        lname_ET.setText(child_lname);
        street_ET.setText(child_street);
        ctAndSt_ET.setText(child_ctAndSt);
        email_ET.setText(child_email);
        vEmail_ET.setText(child_email);
        phone_ET.setText(child_phone);
        cMake_ET.setText(child_cMake);
        cModel_ET.setText(child_cModel);
        cYear_ET.setText(child_cYear);
        uName_ET.setText(child_uName);

        setRadioBtn();

    }


    //--------------------------------------------------------------------------------------------//
    private void setLocationOfSpinnerFromDataPos(int m, int d, int y) {
        m_Spinner.setSelection(m);
        d_Spinner.setSelection(d);
        y_Spinner.setSelection(y);
    }

    //--------------------------------------------------------------------------------------------//
    private void setStringArrayForSpinnerUse() {
        monthArray = getResources().getStringArray(R.array.month);
        dayArray = getResources().getStringArray(R.array.day);
        yearArray = getResources().getStringArray(R.array.year);
    }

    //--------------------------------------------------------------------------------------------//
    private void getSpinnerValues() {
        m_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPos_int = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        d_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dPos_int = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        y_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yPos_int = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //--------------------------------------------------------------------------------------------//
    private void saveUpdatedInfo() {
        Map profile = new HashMap();
        new_selected_radio_btn = getSelectedRB();

        String age_str = getAge(mPos_int, dPos_int, yPos_int);
        String fname = fname_ET.getText().toString().trim();
        String lname = lname_ET.getText().toString().trim();
        String street = street_ET.getText().toString().trim();
        String ctAndSt = ctAndSt_ET.getText().toString().trim();
        String email = email_ET.getText().toString().trim();
        String vemail = vEmail_ET.getText().toString().trim();
        String pwd = pwd_ET.getText().toString().trim();
        String vpwd = vPwd_ET.getText().toString().trim();
        String phone = phone_ET.getText().toString().trim();
        String carMake = cMake_ET.getText().toString().trim();
        String carModel = cModel_ET.getText().toString().trim();
        String carYear = cYear_ET.getText().toString().trim();
        String uname = uName_ET.getText().toString().trim();
        String m_pos = String.valueOf(mPos_int);
        String d_pos = String.valueOf(dPos_int);
        String y_pos = String.valueOf(yPos_int);
        String whlChr_access = new_selected_radio_btn;
        String fullName = fname + " " + lname;
        String fullAddress = street + "\n" + ctAndSt;



        DatabaseReference dbref = dbRef_set.child(Constants.USER)
                .child(mUser.getUid())
                .child(Constants.PROFILE);
        if (fieldChecking(fname, lname, street, ctAndSt, email, vemail, phone, carMake, carModel,
                carYear, uname)) {
            if (checker.checkName(fname, lname, fname_ET, lname_ET)) {
                if (!checker.compareString(fname, child_fname)) {
                    profile.put(Constants.FIRSTNAME, fname);
                    updateStr(fname);
                }
                if (!checker.compareString(lname, child_lname)) {
                    profile.put(Constants.LASTNAME, lname);
                    updateStr(lname);
                }
                if (!checker.compareString(fullName, child_fullName)) {
                    profile.put(Constants.FULLNAME, fullName);
                    updateStr(fullName);
                }
            }

            if (!checker.compareString(email, child_email)) {
                if (checker.validateEmail(email)) {
                    if (checker.compareString(email, vemail)) {
                        profile.put(Constants.EMAIL, email);
                        updateStr(email);

                        mUser.updateEmail(email);

                        if (child_mode.equals(Constants.DRIVER)) {
                            DatabaseReference dref = mDbRef.child(Constants.DRIVER)
                                    .child(mUser.getUid());
                            dref.child(Constants.EMAIL).setValue(email);
                        }
                    } else {
                        email_ET.setError(Constants.ERR_EMAIL_MATCH);
                        vEmail_ET.setError(Constants.ERR_EMAIL_MATCH);
                    }
                } else {
                    email_ET.setError(Constants.ERR_EMAIL_PATTERN);
                }
            }

            if (!checker.compareString(street, child_street)) {
                profile.put(Constants.ADDRESS, street);
                updateStr(street);
            }

            if (!checker.compareString(ctAndSt, child_ctAndSt)) {
                profile.put(Constants.CITYANDSTATE, ctAndSt);
                updateStr(ctAndSt);
            }

            if (!checker.compareString(fullAddress, child_fullAddress)) {
                profile.put(Constants.FULL_ADDRESS, fullAddress);
                updateStr(fullAddress);
            }

            if (!checker.compareString(phone, child_phone)) {
                profile.put(Constants.PHONE, phone);
                updateStr(phone);
            }

            if (!checker.compareString(carMake, child_cMake)) {
                profile.put(Constants.CAR_MAKE, carMake);
                updateStr(carMake);
            }

            if (!checker.compareString(carModel, child_cModel)) {
                profile.put(Constants.CAR_MODEL, carModel);
                updateStr(carModel);
            }

            if (!checker.compareString(carYear, child_cYear)) {
                profile.put(Constants.CAR_YEAR, carYear);
                updateStr(carYear);
            }

            if (!checker.compareString(uname, child_uName)) {
                profile.put(Constants.USERNAME, uname);
                updateStr(uname);
            }

            if (!pwd.isEmpty() || !vpwd.isEmpty()) {
                if (checker.pwdFieldChecking(pwd, vpwd, pwd_ET, vPwd_ET)) {
                    if (checker.pwdmatch(pwd, vpwd, pwd_ET, vPwd_ET)) {
                        if (checker.checkPwd(pwd) && checker.checkPwd(vpwd)) {
                            mUser.updatePassword(pwd);
                            updateStr("Password updated.");
                        }
                    }
                }
            }

            if (!checker.compareString(m_pos, child_month_pos)) {
                profile.put(Constants.BIRTH_MONTH_POS, m_pos);
                updateStr("Month");
            }
            if (!checker.compareString(d_pos, child_day_pos)) {
                profile.put(Constants.BIRTH_DAY_POS, d_pos);
                updateStr("Day");
            }
            if (!checker.compareString(y_pos, child_year_pos)) {
                profile.put(Constants.BIRTH_YEAR_POS, y_pos);
                updateStr("Year");
            }

            if (!age_str.isEmpty()) {
                if (!checker.compareString(age_str, child_age)) {
                    profile.put(Constants.AGE, age_str);
                    updateStr(age_str);
                }
            }

            if (!whlChr_access.isEmpty()) {
                if (!checker.compareString(whlChr_access, child_wheelchair_needed)) {
                    profile.put(Constants.WHEELCHAIR, whlChr_access);
                    updateStr(whlChr_access);
                }
            }

            if (!getUpdatedStr().isEmpty()) {
                dbref.updateChildren(profile, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError,
                                           DatabaseReference databaseReference) {

                        if (databaseError != null) {
                            Toast.makeText(getApplicationContext(), "Error updating your profile" +
                                                                    ".", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(DriverEditProfileActivity.this,
                                    Constants.ACCT_UPDATED + "\n" + getUpdatedStr(),
                                    Toast.LENGTH_LONG).show();
                            itemsUpdated = "";
                        }

                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), Constants.NO_UPDATE, Toast.LENGTH_LONG).show();
            }

        }



    }

    //--------------------------------------------------------------------------------------------//
    private String getSelectedRB() {
        String str;
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioBtn = findViewById(radioId);
        str = (String) radioBtn.getText();
        return str;
    }

    //--------------------------------------------------------------------------------------------//
    private String getAge (int mpos, int dpos, int ypos) {
        int actualYear = Integer.valueOf(yearArray[ypos]);

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(actualYear, mpos, dpos);
        int yourAge = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        dob.add(Calendar.YEAR, yourAge);
        if (today.before(dob)) {
            yourAge--;
        }

        return String.valueOf(yourAge);

    }

    //--------------------------------------------------------------------------------------------//
    private boolean fieldChecking(String fname, String lname, String streetAdd, String cityAndState,
                                  String email, String v_email, String phone, String carMake,
                                  String carModel, String carYear, String username) {

        boolean flag = true;
        if (fname.isEmpty()) {
            fname_ET.setError("First Name Required");
            flag = false;
        }
        if (lname.isEmpty()) {
            lname_ET.setError("Last name is required");
            flag = false;
        }
        if (streetAdd.isEmpty()) {
            street_ET.setError("Street address required");
            flag = false;
        }
        if (cityAndState.isEmpty()) {
            ctAndSt_ET.setError("City and State required");
            flag = false;
        }
        if (email.isEmpty()) {
            email_ET.setError("Email required");
            flag = false;
        }
        if (v_email.isEmpty()) {
            vEmail_ET.setError("Email required");
            flag = false;
        }
        if (phone.isEmpty()) {
            phone_ET.setError("Phone number required");
            flag = false;
        }
        if (carMake.isEmpty()) {
            cMake_ET.setError("Car Make required");
            flag = false;
        }
        if (carModel.isEmpty()) {
            cModel_ET.setError("Car Model required");
            flag = false;
        }
        if (carYear.isEmpty()) {
            cYear_ET.setError("Year required");
            flag = false;
        }
        if (username.isEmpty()) {
            uName_ET.setError("Username required");
            flag = false;
        }

        return flag;

    }

    //--------------------------------------------------------------------------------------------//
    private void updateStr(String str) {
        itemsUpdated = itemsUpdated + str + "\n";
    }

    //--------------------------------------------------------------------------------------------//
    private String getUpdatedStr() {
        return itemsUpdated;
    }

    //--------------------------------------------------------------------------------------------//
    private void setRadioBtn() {
        String yes = "Yes";
        if (child_wheelchair_needed == null) {
            radioGroup.check(R.id.driver_no_radioBtn_editProf);
        } else if (checker.compareString(child_wheelchair_needed, yes)) {
            radioGroup.check(R.id.driver_yes_radioBtn_editProf);
        } else {
            radioGroup.check(R.id.driver_no_radioBtn_editProf);
        }
    }

}
