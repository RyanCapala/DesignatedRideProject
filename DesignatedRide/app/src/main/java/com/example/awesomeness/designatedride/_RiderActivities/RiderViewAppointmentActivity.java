package com.example.awesomeness.designatedride._RiderActivities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v4.app.ActivityCompat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.awesomeness.designatedride.R;
import com.example.awesomeness.designatedride.util.Constants;
import com.example.awesomeness.designatedride.util.HandleFileReadWrite;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class RiderViewAppointmentActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "RiderViewAppointmentAct";
    // Maps
    private Boolean mapLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private Marker marker;

    // Widgets
    private Button reqRideButton;
    private ActionBar actionBar;
    private ImageButton editPenIB;
    private Guideline mapInfoSeperator;
    //file
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment);

        //
        Intent intent = getIntent();
        fileName = intent.getStringExtra(Constants.FILENAME_MESSAGE);

        //
        initWidgets();
        initializeMap();

        editPenIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float defaultPct = 0.36f;
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mapInfoSeperator.getLayoutParams();
                if (params.guidePercent < defaultPct || params.guidePercent > 0.5)
                    params.guidePercent = defaultPct;
                else
                    params.guidePercent = 1.0f;
                mapInfoSeperator.setLayoutParams(params);
                Log.d(TAG, "onClick: Set guidepct to:" + params.guidePercent);
            }
        });
        editPenIB.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    Point p = new Point();
                    getWindowManager().getDefaultDisplay().getSize(p);
                    int mScreenHeight = p.y;
                    float changeHeight;
                    if (motionEvent.getRawY() > mScreenHeight)
                        changeHeight = mScreenHeight;
                    else if (motionEvent.getRawY() < 0)
                        changeHeight = 0;
                    else
                        changeHeight = motionEvent.getRawY();

                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mapInfoSeperator.getLayoutParams();
                    params.guidePercent = changeHeight / mScreenHeight;
                    mapInfoSeperator.setLayoutParams(params);
                    Log.d(TAG, "onTouch: Set guidebegin to:" + params.guidePercent);
                }
                return true;
            }
        });
        runExampleAppt();
        setAppt();

        reqRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RiderViewAppointmentActivity.this, "Request ride pressed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // TODO: 2/27/2018 Jump to user's appointment destination
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false); // Hides the "locate me" button
        padGoogleMap();

        // Zoom on sunrise hospital for example
        LatLng sunriseHosp = new LatLng(36.133137, -115.136258);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(sunriseHosp).title("Sunrise Hospital");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location_red));
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sunriseHosp, 15));

        mMap.getUiSettings().setScrollGesturesEnabled(false); // Just disables zoom for example
    }

    private void initializeMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.appointmentViewRider_map);
        mapFragment.getMapAsync(this);
    }

    // Pad map appropriately to not obscure google logo/copyright info
    // This is a generic function, wont look nice on most devices
    // probably needs some math to calculate padding size (its in pixels)
    private void padGoogleMap() {
        //    //int[] locationOnScreen; // [x, y]
        //    //findViewById(R.id.setPickupBtn_ridermap).getLocationOnScreen(locationOnScreen);

        //    // left, top, right, bottom
        mMap.setPadding(0, 0, 0, 0);

    }

    private void runExampleAppt() {
        setHospitalInfo("Sunrise Hospital", "3186 S Maryland Pkwy, Las Vegas, NV 89109");
        setTimeInfo("14:00 PST", "Monday, February 5");
        setAdvancedBookingInfo("Not Set", "Advanced booking available");
        setNotesInfo("Tap the edit button to add notes.");
    }

    private void setAppt() {
        //open file

        String name = "";
        String address = "";
        String time = "";
        String date = "";
        String status = "";
        String notes = "";

        HandleFileReadWrite reader = new HandleFileReadWrite();
        reader.open(this, fileName);
        if (reader.isExist()) {
            name = reader.readLine();
            address = reader.readLine();
            time = reader.readLine();
            date = reader.readLine();
            status = reader.readLine();
            notes = reader.readLine();
            StringBuilder stringBuilder = new StringBuilder();
            while (notes != null) {
                stringBuilder.append(notes);
                stringBuilder.append("\n");
                notes = reader.readLine();
            }
            notes = stringBuilder.toString();
        }

        setHospitalInfo(name, address);
        setTimeInfo(time, date);
        setAdvancedBookingInfo(status, (status.equals("yes")) ? "Advanced booking available" : "Advanced booking not available");
        setNotesInfo(notes);
    }

    private void setHospitalInfo(String name, String address) {
        setTextViewString(R.id.viewAppointmentRiderDestName_tv, name);
        setTextViewString(R.id.viewAppointmentRiderDestAddress_tv, address);
    }

    private void setTimeInfo(String timeTimezone, String alphaDate) {
        setTextViewString(R.id.viewAppointmentRiderDateTime_tv, timeTimezone);
        setTextViewString(R.id.viewAppointmentRiderDateDayMonth_tv, alphaDate);
    }

    private void setAdvancedBookingInfo(String status, String available) {
        setTextViewString(R.id.viewAppointmentRiderAdvanceBookingSetStatus_tv, status);
        setTextViewString(R.id.viewAppointmentRiderAdvanceBookingAvail_tv, available);
    }

    private void setNotesInfo(String info) {
        setTextViewString(R.id.viewAppointmentRiderNotesText_tv, info);
    }

    private void setTextViewString(int id, String str) {
        if (str != null) {
            ((TextView) findViewById(id)).setText(str);
        } else {
            Log.d(TAG, "setTextViewString: received null string");
        }
    }

    private void initWidgets() {
        reqRideButton = findViewById(R.id.viewAppointmentRiderRequestRide_btn);
        actionBar = getActionBar();
        editPenIB = findViewById(R.id.viewAppointmentRiderDraggableResize_btn);
        mapInfoSeperator = findViewById(R.id.appointmentViewRiderMapInfo_guideline);
    }
}
