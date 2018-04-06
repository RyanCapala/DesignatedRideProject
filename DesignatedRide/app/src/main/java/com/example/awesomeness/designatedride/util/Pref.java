package com.example.awesomeness.designatedride.util;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.awesomeness.designatedride.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Pref extends AppCompatActivity {
    private EditText distanceText;
    private EditText from;
    private EditText to;
    private Button updatePreference;
    private TextView closeTV;
    private ScrollView scrollView;

    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private String userid;
    private String prefDistance;
    private String key;

    private Query obtainKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);

        initWidgets();

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        userid = mAuth.getCurrentUser().getUid();

        updatePreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefDistance = distanceText.getText().toString().trim();
                Toast.makeText(Pref.this,prefDistance + "",Toast.LENGTH_LONG).show();
                if(prefDistance != null){
                    obtainKey = mDatabaseReference.child(Constants.DRIVER).child(userid).child(Constants.GEOKEY);
                    obtainKey.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            key = dataSnapshot.getValue(String.class);
                            if(key != null){

                                Toast.makeText(Pref.this,"here",Toast.LENGTH_LONG).show();
                                Double dis = Double.parseDouble(prefDistance.trim());
                                Toast.makeText(Pref.this,dis + "",Toast.LENGTH_LONG).show();
                                mDatabaseReference.child(Constants.PREFERENCE).child(key).child(Constants.DISTANCE).setValue(dis);
                            }
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

    }

    private void initWidgets() {
        from = (EditText) findViewById(R.id.fromTime);
        to = (EditText) findViewById(R.id.toTime);
        distanceText = (EditText) findViewById(R.id.distance);
        updatePreference = (Button) findViewById(R.id.updatePreferences);
        closeTV = (TextView) findViewById(R.id.closeTV_rUpdate);
        scrollView = (ScrollView) findViewById(R.id.scrollView_profile);
    }
}

