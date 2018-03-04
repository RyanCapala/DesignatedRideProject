package com.example.awesomeness.designatedride._RiderActivities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.awesomeness.designatedride.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
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
    Button reqRideButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment);
        initWidgets();
        initializeMap();
        runExampleAppt();
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
    }
}
