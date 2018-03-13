package com.example.awesomeness.designatedride.util;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.awesomeness.designatedride.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SynAck extends AppCompatActivity {

    private static final String TAG = "SynAck";

    //Alert Box
    private AlertDialog.Builder confirmation;
    private AlertDialog dialogBox;

    //Widgets
    private Button acceptButton;
    private Button declineButton;
    private TextView riderName;
    private View text_box;

    private Timer timer;

    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private String userid;
    private String key;
    private String pairKey;
    private String riderRating;
    private String message;
    private Query obtainKey;
    private Query obtainRiderRating;
    private Query obtainPairKey;

    private Map aWriteInfo;
    private Map aExchangeInfo;

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
                obtainPairKey = mDatabaseReference.child(Constants.PAIR).child(key).child(Constants.PAIR_KEY);
                obtainPairKey.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        pairKey = dataSnapshot.getValue(String.class);
                        obtainRiderRating = mDatabaseReference.child(Constants.TEXT_BOX).child(pairKey).child(Constants.USER_RATING);
                        obtainRiderRating.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                riderRating = dataSnapshot.getValue(String.class);
                                message = riderRating + "\n" + "Now";
                                riderName.setText(message);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        confirmation = new AlertDialog.Builder(SynAck.this);
        initWidgets();
        confirmation.setView(text_box);
        dialogBox = confirmation.create();
        dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        aExchangeInfo = new HashMap();
        aWriteInfo = new HashMap();

        dialogBox.show();

        timer = new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run(){
                dialogBox.dismiss();
                mDatabaseReference.child(Constants.PACKET).child(key).child(Constants.SEQ_ACK).setValue(4);
                timer.cancel();
                finish();
            }
        },10000);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBox.dismiss();
                timer.cancel();

                mDatabaseReference.child(Constants.PACKET).child(key).child(Constants.SEQ_ACK).setValue(2);
                mDatabaseReference.child(Constants.AVAILABLE_GEOLOCATION).child(key).removeValue();

                finish();
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBox.dismiss();
                timer.cancel();
                mDatabaseReference.child(Constants.PACKET).child(key).child(Constants.SEQ_ACK).setValue(0);
                mDatabaseReference.child(Constants.PACKET).child(key).child(Constants.IS_AVAILABLE).setValue("true");

                finish();
            }
        });

    }

    private void initWidgets()
    {
        text_box = getLayoutInflater().inflate(R.layout
                .text_box, null);
        acceptButton = (Button)text_box.findViewById(R.id.acceptButton);
        declineButton = (Button)text_box.findViewById(R.id.declineButton);
        riderName = (TextView)text_box.findViewById(R.id.rider_name);
    }
}
