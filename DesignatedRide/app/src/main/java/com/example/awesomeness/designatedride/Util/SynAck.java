package com.example.awesomeness.designatedride.util;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.awesomeness.designatedride.R;
import com.example.awesomeness.designatedride._DriverActivities.DriverMapActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;

public class SynAck extends AppCompatActivity {

    //Alert Box
    private AlertDialog.Builder confirmation;
    private AlertDialog dialogBox;

    //Widgets
    private Button yesButton;
    private Button noButton;
    private TextView txt;
    private View confirm_dialog_view;

    private Timer timer;

    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private String userid;
    private String key;
    private Query obtainKey;

    public static boolean ttl = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synack);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        userid = mAuth.getCurrentUser().getUid();

        obtainKey = mDatabaseReference.child(Constants.DRIVER).child(userid).child(Constants.GEOKEY);
        obtainKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                key = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        confirmation = new AlertDialog.Builder(SynAck.this);
        initWidgets();
        String message = "Rider Available" + "\n" + "Give Ride?";
        txt.setText(message);
        confirmation.setView(confirm_dialog_view);
        dialogBox = confirmation.create();
        dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBox.dismiss();
                mDatabaseReference.child(Constants.LOCATION).child(key).child(Constants.CONFIRMATION).setValue("true");
                mDatabaseReference.child(Constants.AVAILABLE_GEOLOCATION).child(key).removeValue();
                DriverMapActivity.exchange = false;
                DriverMapActivity.isOn = false;
                finish();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBox.dismiss();
                mDatabaseReference.child(Constants.LOCATION).child(key).child(Constants.IS_AVAILABLE).setValue("true");
                finish();
            }
        });

        dialogBox.show();

        if(ttl) {
            deleteLocation();
            DriverMapActivity.isOn = false;
        }
    }

    private void initWidgets()
    {
        confirm_dialog_view = getLayoutInflater().inflate(R.layout
                .confirmation_dialog, null);
        yesButton = (Button)confirm_dialog_view.findViewById(R.id.yesButton);
        noButton = (Button)confirm_dialog_view.findViewById(R.id.noButton);
        txt = (TextView)confirm_dialog_view.findViewById(R.id.textAlert);
    }

    private void deleteLocation(){
        mDatabaseReference.child(Constants.AVAILABLE_GEOLOCATION).child(key).removeValue();
        mDatabaseReference.child(Constants.LOCATION).child(key).removeValue();
    }

}