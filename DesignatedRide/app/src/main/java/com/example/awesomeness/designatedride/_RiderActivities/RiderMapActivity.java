package com.example.awesomeness.designatedride._RiderActivities;

import android.Manifest;
import android.util.Log;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.awesomeness.designatedride.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RiderMapActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final String TAG = "RiderMapActivity";
    private static final long MIN_TIME = 500;
    private static final float MIN_DISTANCE = 500;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final float DEFAULT_ZOOM = 15f;

    // Maps
    private Boolean mapLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mapFusedLocationProviderClient;
    private Marker marker;

    //Widgets
    Button setPickupBtn;

    //FireBase
    private DatabaseReference mDatabaseReference;
    private DataSnapshot mDataSnapshot;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private String userid;

    //Database children
    private String _GeoLocation = "GeoLocation";
    private String _AvaliableGeoLocation = "AvaliableGeoLocation";
    private String _Driver = "Driver";
    private String _Rider = "Rider";
    private String _geoKey = "geoKey";
    private String _userRating = "userRating";
    private String _isAvaliable = "isAvaliable";
    private String key = "";
    private String rating = "";
    private String driverKey = "";

    //GeoFire
    private GeoQuery mGeoQuery;
    private GeoFire mAvaliableGeoFire;
    private GeoFire mGeoFire;
    private DatabaseReference mAvaliableGeoLocationRef;
    private DatabaseReference mGeoLocationRef;
    private DatabaseReference mChildAvaliable;
    private DatabaseReference mChildLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_map);
        initWidgets();

        mapFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mDatabase = FirebaseDatabase.getInstance();

        mDatabaseReference = mDatabase.getReference();
        mChildLocation = mDatabase.getReference();
        mChildAvaliable = mDatabase.getReference();

        mAvaliableGeoLocationRef = mChildAvaliable.child(_AvaliableGeoLocation);
        mGeoLocationRef = mChildLocation.child(_GeoLocation);

        mAvaliableGeoFire = new GeoFire(mAvaliableGeoLocationRef);
        mGeoFire = new GeoFire(mGeoLocationRef);

        mAuth = FirebaseAuth.getInstance();
        userid = mAuth.getCurrentUser().getUid();
        mDatabaseReference.child(_Rider).child(userid).child(_geoKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                key = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        getLocationPermissions();

        setPickupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RiderMapActivity.this, "Requested a ride!", Toast.LENGTH_SHORT).show();
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        driverKey = (String)marker.getTag();
                        mDatabaseReference.child(_AvaliableGeoLocation).child(driverKey).child(_isAvaliable).setValue("false");
                        return false;
                    }
                });

                mGeoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                    @Override
                    public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                        rating = dataSnapshot.child(_userRating).getValue(String.class);
                        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude,location.longitude)).title(rating));
                        marker.setTag(dataSnapshot.getKey());
                    }

                    @Override
                    public void onDataExited(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

                    }

                    @Override
                    public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

                    }

                    @Override
                    public void onGeoQueryReady() {

                    }

                    @Override
                    public void onGeoQueryError(DatabaseError error) {

                    }
                });
            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        mGeoFire.setLocation(key, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.animateCamera(cameraUpdate);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        padGoogleMap();
        // Add a marker in Sydney and move the camera (Default thing from google maps)
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (mapLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mMap.setMyLocationEnabled(true);

            //mMap.getUiSettings().setMyLocationButtonEnabled(false); // Hides the "locate me" button
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mapLocationPermissionsGranted = false;
                            return;
                        }
                    }

                    mapLocationPermissionsGranted = true;
                    initializeMap();
                }
            }
        }
    }

    private void initializeMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getDeviceLocation() {

        try {
            if (mapLocationPermissionsGranted) {
                final Task location = mapFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {

                            Location currentLocation = (Location) task.getResult();
                            Log.d(TAG, "onComplete: found location. ");
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);

                            mGeoFire.setLocation(key, new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude()), new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {

                                }
                            });
                            mGeoQuery = mAvaliableGeoFire.queryAtLocation(new GeoLocation(currentLocation.getLatitude(),currentLocation.getLongitude()),0.5);
                        }
                        else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(RiderMapActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }

    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat:" + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void getLocationPermissions() {
        String[] permissions = {FINE_LOCATION, COURSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mapLocationPermissionsGranted = true;
                initializeMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    private void initWidgets()
    {
        setPickupBtn = (Button) findViewById(R.id.setPickupBtn_ridermap);
    }

    // Pad map appropriately to not obscure google logo/copyright info
    // This is a generic function, wont look nice on most devices
    // probably needs some math to calculate padding size (its in pixels)
    private void padGoogleMap(){
        //    //int[] locationOnScreen; // [x, y]
        //    //findViewById(R.id.setPickupBtn_ridermap).getLocationOnScreen(locationOnScreen);

        //    // left, top, right, bottom
        mMap.setPadding(0,0, 0,150);

    }

}