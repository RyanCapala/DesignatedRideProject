package com.example.awesomeness.designatedride._RiderActivities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import com.example.awesomeness.designatedride.R;
import com.example.awesomeness.designatedride.util.Constants;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RiderMapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private static final String TAG = "RiderMapActivity";
    private static final long DES_TIME = 10000; //milliseconds
    private static final long EXP_TIME = 5000;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final float DEFAULT_ZOOM = 15f;

    // Maps
    private Boolean mapLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mapFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Marker marker;

    //Widgets
    Button setPickupBtn;
    Button yesButton;
    Button noButton;
    TextView txt;
    View confirm_dialog_view;

    //List
    ArrayList<Marker> availableDrivers;

    //Marker
    Marker driver;
    Marker driverMarker;

    //FireBase
    private DatabaseReference mDatabaseReference;
    private DataSnapshot mDataSnapshot;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private String userid;

    private String key;
    private String rating;
    private String driverKey;
    private Integer seqAck;
    private Integer filter;
    private Integer temp;

    //Alert Box
    AlertDialog.Builder confirmation;
    AlertDialog dialogBox;
    private ProgressDialog mProgressDialog;
    Timer timer;

    //Map
    private Map writeInfo;
    private Map exchangeInfo;

    //GeoFire
    private GeoQuery mGeoQuery;
    private GeoFire mAvailableGeoFire;
    private GeoFire mGeoFire;
    private DatabaseReference mAvailableGeoLocationRef;
    private DatabaseReference mGeoLocationRef;
    private DatabaseReference mChildAvailable;
    private DatabaseReference mChildLocation;
    private ChildEventListener childEventListener;
    private ChildEventListener driverEventListener;

    private Query obtainKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_map);
        initWidgets();

        mapFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        confirmation = new AlertDialog.Builder(RiderMapActivity.this);

        callBack();

        mDatabase = FirebaseDatabase.getInstance();

        mDatabaseReference = mDatabase.getReference();
        mChildLocation = mDatabase.getReference();
        mChildAvailable = mDatabase.getReference();

        mAvailableGeoLocationRef = mChildAvailable.child(Constants.AVAILABLE_GEOLOCATION);
        mGeoLocationRef = mChildLocation.child(Constants.GEO_LOCATION);

        mAvailableGeoFire = new GeoFire(mAvailableGeoLocationRef);
        mGeoFire = new GeoFire(mGeoLocationRef);

        mAuth = FirebaseAuth.getInstance();
        userid = mAuth.getCurrentUser().getUid();

        availableDrivers = new ArrayList<>();


        obtainKey = mDatabaseReference.child(Constants.RIDER).child(userid).child(Constants.GEOKEY);
        obtainKey.addListenerForSingleValueEvent(new ValueEventListener() {
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
                        String message = "User Rating:" + rating + "\n" + "Choose this Driver?";
                        txt.setText(message);
                        confirmation.setView(confirm_dialog_view);
                        dialogBox = confirmation.create();
                        dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogBox.show();


                        yesButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDatabaseReference.child(Constants.PACKET).child(driverKey).child(Constants.SEQ_ACK).setValue(6);
                                mDatabaseReference.child(Constants.PACKET).child(driverKey).child(Constants.IS_AVAILABLE).setValue("false");
                                if(seqAck != null) {
                                    temp = seqAck;
                                    seqAck = 6;
                                }

                                childEventListener = mDatabaseReference.child(Constants.PACKET).child(driverKey).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                        if(dataSnapshot.getKey().equals(Constants.SEQ_ACK)){
                                            mProgressDialog.dismiss();
                                            seqAck = dataSnapshot.getValue(Integer.class);
                                            if(seqAck == 0){
                                                Toast.makeText(RiderMapActivity.this,"Driver is current unavailable",Toast.LENGTH_LONG).show();
                                                mDatabaseReference.child(Constants.PACKET).child(driverKey).removeEventListener(childEventListener);
                                            }
                                            else if(seqAck == 2){
                                                mDatabaseReference.child(Constants.PACKET).child(driverKey).child(Constants.SEQ_ACK).setValue(3);
                                                mDatabaseReference.child(Constants.LOCATION).child(driverKey).child(Constants.RIDER_KEY).setValue(key);
                                            }
                                            else if(seqAck == 3) {
                                                for(int j = 0; j < availableDrivers.size(); j++) {
                                                    driver = availableDrivers.get(j);
                                                    driver.remove();
                                                }
                                                Toast.makeText(RiderMapActivity.this,"Connected with Driver",Toast.LENGTH_LONG).show();
                                                mGeoFire.getLocation(driverKey, new com.firebase.geofire.LocationCallback() {
                                                    @Override
                                                    public void onLocationResult(String key, GeoLocation location) {
                                                        driverMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car)));
                                                        driverMarker.setTag(driverKey);
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                driverEventListener = mDatabaseReference.child(Constants.GEO_LOCATION).child(driverKey).addChildEventListener(new ChildEventListener() {
                                                    @Override
                                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                                    }

                                                    @Override
                                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                                        mGeoFire.getLocation(driverKey, new com.firebase.geofire.LocationCallback() {
                                                            @Override
                                                            public void onLocationResult(String key, GeoLocation location) {
                                                                driverMarker.remove();
                                                                driverMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car)));
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                                    }

                                                    @Override
                                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                                mDatabaseReference.child(Constants.PACKET).child(driverKey).removeEventListener(childEventListener);
                                            }
                                            else if(seqAck == 4){
                                                mProgressDialog.dismiss();
                                                Toast.makeText(RiderMapActivity.this,"Driver is no longer available",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }

                                    @Override

                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                mProgressDialog.setMessage("Confirming Driver Availability...");
                                mProgressDialog.show();
                                dialogBox.dismiss();
                                if(confirm_dialog_view.getParent() != null){
                                    ((ViewGroup)confirm_dialog_view.getParent()).removeView(confirm_dialog_view);
                                }

                                if(seqAck != null) {
                                    if (seqAck == 6) {
                                        seqAck = temp;
                                    }
                                }
                            }
                        });

                        noButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(confirm_dialog_view.getParent() != null){
                                    ((ViewGroup)confirm_dialog_view.getParent()).removeView(confirm_dialog_view);
                                }
                                dialogBox.dismiss();
                            }
                        });

                        return false;
                    }
                });

                mGeoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                    @Override
                    public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                        driverKey = dataSnapshot.getKey();
                        mDatabaseReference.child(Constants.PACKET).child(driverKey).child(Constants.SEQ_ACK).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                filter = dataSnapshot.getValue(Integer.class);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        mDatabaseReference.child(Constants.PACKET).child(driverKey).child(Constants.USER_RATING).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                rating = dataSnapshot.getValue(String.class);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        if(filter != null) {
                            if (filter == 0) {
                                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).title(rating).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car_white)));
                            } else {
                                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).title(rating).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car)));
                            }
                        }
                        else
                            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).title(rating).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car)));
                        marker.setTag(driverKey);
                        availableDrivers.add(marker);
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
    protected void onResume() {
        super.onResume();
        getDeviceLocation();
    }
    // Prevent battery drain when activity is not in focus
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates: STOPPED LOCATION UPDATES");
        mapFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }
    private void startLocationUpdates() {
        try {
            Log.d(TAG, "startLocationUpdates: STARTED LOCATION UPDATES");
            mapFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }catch (SecurityException e){
            Log.d(TAG, "startLocationUpdates: " + e.getMessage());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        mGeoFire.setLocation(key, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });

        //ToDo: Fix camera
        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        //mMap.animateCamera(cameraUpdate);

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

                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(DES_TIME);
                mLocationRequest.setFastestInterval(EXP_TIME);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                mapFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,mLocationCallback,null);

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
                            mGeoQuery = mAvailableGeoFire.queryAtLocation(new GeoLocation(currentLocation.getLatitude(),currentLocation.getLongitude()),0.5);
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
        confirm_dialog_view = getLayoutInflater().inflate(R.layout
                .confirmation_dialog, null);
        yesButton = (Button)confirm_dialog_view.findViewById(R.id.yesButton);
        noButton = (Button)confirm_dialog_view.findViewById(R.id.noButton);
        txt = (TextView)confirm_dialog_view.findViewById(R.id.textAlert);
        mProgressDialog = new ProgressDialog(RiderMapActivity.this);

        // Add <- arrow on actionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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

    private void callBack(){
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                Location location = locationResult.getLastLocation();
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                mGeoFire.setLocation(key, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                    }
                });

                //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
                //mMap.animateCamera(cameraUpdate);

            }
        };
    }
}

