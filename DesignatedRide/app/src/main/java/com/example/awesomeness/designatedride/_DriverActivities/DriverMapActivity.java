package com.example.awesomeness.designatedride._DriverActivities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.awesomeness.designatedride.R;
import com.example.awesomeness.designatedride.Util.Constants;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final String TAG = "DriverMapActivity";
    private static final long DES_TIME = 10000; //milliseconds
    private static final long EXP_TIME = 5000;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final float DEFAULT_ZOOM = 15f;

    private Boolean mapLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mapFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationManager locationManager;
    private LocationCallback mLocationCallback;

    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private String userid;

    private boolean isOn = false;
    private boolean ttl = false;
    private boolean exchange = true;

    //Widgets
    Button setPickupBtn;
    Button yesButton;
    Button noButton;
    TextView txt;
    View confirm_dialog_view;

    private String key = "";
    private String riderKey = "";
    private String rating = "";
    private String available = "Currently Available (ON)";
    private String unavailable = "Currently Unavailable (OFF)";

    //Hash Table
    private Map writeInfo;
    private Map exchangeInfo;

    //GeoFire
    private GeoFire mAvailableGeoFire;
    private GeoFire mGeoFire;
    private DatabaseReference mAvailableGeoLocationRef;
    private DatabaseReference mGeoLocationRef;
    private DatabaseReference mChildAvailable;
    private DatabaseReference mChildLocation;

    //Alert Box
    AlertDialog.Builder confirmation;
    AlertDialog dialogBox;

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_map);
        initWidgets();

        //ToDo: make the alert for all activities.  Otherwise it will crash if activated on different Activity.
        confirmation = new AlertDialog.Builder(DriverMapActivity.this);

        mapFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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

        readDatabase();

        getLocationPermissions();

        createDialogBox();

        // TODO: change button, Need toggle button to determine if Driver can give out rides.
        setPickupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOn) {
                    isOn = false;
                    setPickupBtn.setText(unavailable);
                    deleteLocation();
                    stopLocationUpdates();
                }
                else{
                    isOn = true;
                    setPickupBtn.setText(available);
                    getDeviceLocation();
                }
            }
        });

    }



    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if(isOn && exchange) {
            mAvailableGeoFire.setLocation(key, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {

                }
            });
        }
        else if(!exchange){
           mGeoFire.setLocation(key, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
               @Override
               public void onComplete(String key, DatabaseError error) {

               }
            });
        }

        //ToDo: Fix Camera
        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        //mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
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


    // TODO: Add code to prevent race condition.
    private void getDeviceLocation() {
        try {
            if (mapLocationPermissionsGranted) {
                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(DES_TIME);
                mLocationRequest.setFastestInterval(EXP_TIME);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                startLocationUpdates();

                Task location = mapFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "onComplete: found location.");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);

                            if(isOn && exchange){
                                mAvailableGeoFire.setLocation(key, new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude()), new GeoFire.CompletionListener() {
                                    @Override
                                    public void onComplete(String key, DatabaseError error) {

                                    }
                                });

                                exchangeInfo = new HashMap();
                                writeInfo = new HashMap();

                                exchangeInfo.put(Constants.IS_AVAILABLE,"true");
                                exchangeInfo.put(Constants.USER_RATING, rating);
                                exchangeInfo.put(Constants.CONFIRMATION,"false");
                                writeInfo.put(Constants.LOCATION + "/" + key + "/", exchangeInfo);

                                mDatabaseReference.updateChildren(writeInfo, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if(databaseError != null){
                                            Log.wtf(TAG, databaseError.getMessage());
                                        }
                                    }
                                });

                                mDatabaseReference.child(Constants.LOCATION).child(key).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                        if(dataSnapshot.getKey().equals(Constants.IS_AVAILABLE)) {
                                            dialogBox.show();
                                            timer = new Timer();
                                            timer.schedule(new TimerTask(){
                                                @Override
                                                public void run(){
                                                    dialogBox.dismiss();
                                                    if(ttl){
                                                    }
                                                    else
                                                    {
                                                        deleteLocation();
                                                        stopLocationUpdates();
                                                        //setPickupBtn.setText(unavailable);
                                                        ttl = false;
                                                    }
                                                    timer.cancel();
                                                }
                                            },10000);
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

                            }

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(DriverMapActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
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
    {setPickupBtn = (Button) findViewById(R.id.setPickupBtn_ridermap);
        setPickupBtn.setText(unavailable);
        confirm_dialog_view = getLayoutInflater().inflate(R.layout
                .confirmation_dialog, null);
        yesButton = (Button)confirm_dialog_view.findViewById(R.id.yesButton);
        noButton = (Button)confirm_dialog_view.findViewById(R.id.noButton);
        txt = (TextView)confirm_dialog_view.findViewById(R.id.textAlert);
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

    private void createDialogBox(){
        String message = "Rider Available" + "\n" + "Give Ride?";
        txt.setText(message);
        confirmation.setView(confirm_dialog_view);
        dialogBox = confirmation.create();
        dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ttl = true;
                dialogBox.dismiss();
                mDatabaseReference.child(Constants.AVAILABLE_GEOLOCATION).child(key).removeValue();
                mDatabaseReference.child(Constants.LOCATION).child(key).child(Constants.CONFIRMATION).setValue("true");
                setPickupBtn.setText(unavailable);
                setPickupBtn.setVisibility(View.GONE);
                exchange = false;
                isOn = true;
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ttl = true;
                isOn = false;
                setPickupBtn.setText(available);
                dialogBox.dismiss();
            }
        });
    }

    private void readDatabase(){
        mDatabaseReference.child(Constants.DRIVER).child(userid).child(Constants.GEOKEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                key = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference.child(Constants.DRIVER).child(userid).child(Constants.USER_RATING).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rating = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void callBack(){
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                Location location = locationResult.getLastLocation();
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                if(isOn && exchange) {
                    mAvailableGeoFire.setLocation(key, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {

                        }
                    });
                }
                else if(!exchange){
                    mGeoFire.setLocation(key, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {

                        }
                    });
                }

                //ToDo: Fix camera
                //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
                //mMap.animateCamera(cameraUpdate);

                //ToDo: Remove? It was removed on RiderMapActivity.
                //locationManager.removeUpdates(this);

            }
        };
    }

    private void deleteLocation(){
        mDatabaseReference.child(Constants.AVAILABLE_GEOLOCATION).child(key).removeValue();
        mDatabaseReference.child(Constants.LOCATION).child(key).removeValue();
    }
}




