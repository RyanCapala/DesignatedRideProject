package com.example.awesomeness.designatedride._DriverActivities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.example.awesomeness.designatedride.util.Constants;
import com.example.awesomeness.designatedride.util.SynAck;
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

    //Widgets
    Button setPickupBtn;

    private String key;
    private String rating;
    private String isAvailable;
    private String isAdvancedBooking;
    private Integer seqAck;
    private String riderKey;
    private String pairKey;
    private String available = "Currently Available (ON)";
    private String unavailable = "Currently Unavailable (OFF)";

    public static boolean listener = false;

    private static boolean activity;

    //Hash Table
    private Map aWriteInfo;
    private Map aExchangeInfo;
    private Map abWriteInfo;
    private Map abExchangeInfo;

    //GeoFire
    private GeoFire mAvailableGeoFire;
    private GeoFire mGeoFire;
    private DatabaseReference mAvailableGeoLocationRef;
    private DatabaseReference mGeoLocationRef;
    private DatabaseReference mChildAvailable;
    private DatabaseReference mChildLocation;
    private ChildEventListener mChildEventListener;
    private ChildEventListener riderEventListener;
    private Query obtainKey;
    private Query obtainRating;
    private Query obtainPacket;
    private Query obtainRiderKey;
    private Query obtainPairKey;
    private Timer timer;

    Marker marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_map);
        getLocationPermissions();

        initWidgets();

        mapFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //mLocationRequest sets the time we acquire their geo location
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(DES_TIME);
        mLocationRequest.setFastestInterval(EXP_TIME);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //This code is run based on the time of mLocationRequest
        callBack();

        mDatabase = FirebaseDatabase.getInstance();

        mDatabaseReference = mDatabase.getReference();
        mChildLocation = mDatabase.getReference();
        mChildAvailable = mDatabase.getReference();

        //Creates write locations depending if the user is current available or un-available(giving a ride)
        mAvailableGeoLocationRef = mChildAvailable.child(Constants.AVAILABLE_GEOLOCATION);
        mGeoLocationRef = mChildLocation.child(Constants.GEO_LOCATION);

        mAvailableGeoFire = new GeoFire(mAvailableGeoLocationRef);
        mGeoFire = new GeoFire(mGeoLocationRef);

        //Gets the user id number
        mAuth = FirebaseAuth.getInstance();
        userid = mAuth.getCurrentUser().getUid();

        //function reads database to get user's rating and geo location key
        getKeyNodes();
    }



    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

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

    private void getDeviceLocation() {
        try {
            if (mapLocationPermissionsGranted) {

                Task location = mapFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "onComplete: found location.");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);

                            if(isAvailable != null && seqAck != null) {
                                if (isAvailable.equals("true") && seqAck == 8) {

                                    mDatabaseReference.child(Constants.PACKET).child(key).child(Constants.SEQ_ACK).setValue(0);

                                    //If the driver is available write their geolocation to this node
                                    mAvailableGeoFire.setLocation(key, new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude()), new GeoFire.CompletionListener() {
                                        @Override
                                        public void onComplete(String key, DatabaseError error) {

                                        }
                                    });

                                    //Create a listener on the database.  If the Packet node of the user we created changes for any reason
                                    //Determine which node did and respond to it accordingly.
                                    if(!listener){
                                        listener = true;
                                            mChildEventListener = mDatabaseReference.child(Constants.PACKET).child(key).addChildEventListener(new ChildEventListener() {
                                                @Override
                                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                }

                                                @Override
                                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                                    //If they are no longer available, delete their geolocation.
                                                    if (dataSnapshot.getKey().equals(Constants.IS_AVAILABLE)) {
                                                        isAvailable = dataSnapshot.getValue(String.class);
                                                        mDatabaseReference.child(Constants.AVAILABLE_GEOLOCATION).child(key).removeValue();
                                                    }

                                                    //SeqAck operates very similar to an internet packet
                                                    //Rather than create multiple nodes to store boolean values in the database to help determine
                                                    //where or what each packet of information is doing.  SeqAck stores a number that describes
                                                    //where and what it is doing.
                                                    else if (dataSnapshot.getKey().equals(Constants.SEQ_ACK)) {
                                                        seqAck = dataSnapshot.getValue(Integer.class);
                                                        //0 means the packet is not being held by anyone.
                                                        //1 means that a Rider has sent a request to this driver.
                                                        if (seqAck == 1) {
                                                            if(activtyOn()){
                                                                Intent intent = new Intent("ACK");
                                                                sendBroadcast(intent);
                                                            }
                                                            else{
                                                                mDatabaseReference.child(Constants.PACKET).child(key).child(Constants.SEQ_ACK).setValue(5);
                                                            }

                                                            Toast.makeText(DriverMapActivity.this, "Being paired with Rider", Toast.LENGTH_LONG).show();
                                                        }
                                                        //2 means the Driver has seen their request and has sent an acknowledgement
                                                        //3 means that the Rider has seen their acknowledgement and has sent an acknowledgement
                                                        else if (seqAck == 3) {
                                                            Toast.makeText(DriverMapActivity.this, "Connected with Rider", Toast.LENGTH_LONG).show();
                                                            setPickupBtn.setVisibility(View.GONE);
                                                            obtainPairKey = mDatabaseReference.child(Constants.PACKET).child(key).child(Constants.PAIR_KEY);
                                                            obtainPairKey.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    pairKey = dataSnapshot.getValue(String.class);
                                                                    obtainRiderKey  = mDatabaseReference.child(Constants.PAIR).child(pairKey).child(Constants.RIDER_KEY);
                                                                    obtainRiderKey.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                            riderKey = dataSnapshot.getValue(String.class);
                                                                            mGeoFire.getLocation(riderKey, new com.firebase.geofire.LocationCallback() {
                                                                                @Override
                                                                                public void onLocationResult(String key, GeoLocation location) {
                                                                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location_blue)));
                                                                                    marker.setTag(riderKey);

                                                                                }

                                                                                @Override
                                                                                public void onCancelled(DatabaseError databaseError) {

                                                                                }
                                                                            });

                                                                            riderEventListener = mDatabaseReference.child(Constants.GEO_LOCATION).child(riderKey).addChildEventListener(new ChildEventListener() {
                                                                                @Override
                                                                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                                                                }

                                                                                @Override
                                                                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                                                                    mGeoFire.getLocation(riderKey, new com.firebase.geofire.LocationCallback() {
                                                                                        @Override
                                                                                        public void onLocationResult(String key, GeoLocation location) {
                                                                                            marker.remove();
                                                                                            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location_blue)));
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
                                                        //4 means that the TTL(time to live) has died.  This is because the driver is inactive.
                                                        else if (seqAck == 4) {
                                                            deleteLocation();
                                                            stopLocationUpdates();
                                                            resetValues();

                                                            setPickupBtn.setText(unavailable);
                                                            Toast.makeText(DriverMapActivity.this, "From inactivity Geolocation turned off", Toast.LENGTH_LONG).show();
                                                        }

                                                        else if(seqAck == 5){
                                                            mDatabaseReference.child(Constants.PACKET).child(key).child(Constants.SEQ_ACK).setValue(0);
                                                            Toast.makeText(DriverMapActivity.this, "Request unavailable",Toast.LENGTH_LONG).show();
                                                        }
                                                        //6 means the packet is being held by someone.
                                                        else if(seqAck == 6){
                                                            Toast.makeText(DriverMapActivity.this,"A rider is viewing you",Toast.LENGTH_LONG).show();
                                                        }
                                                        //7 means an error has happened.
                                                        //8 means the packet is being created.
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


                                } else if (isAdvancedBooking.equals("true") && seqAck == 8) {
                                    //Create a listener on the database.  If the Packet node of the user we created changes for any reason
                                    //Determine which node did and respond to it accordingly.

                                    mGeoFire.setLocation(key, new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude()), new GeoFire.CompletionListener() {
                                        @Override
                                        public void onComplete(String key, DatabaseError error) {

                                        }
                                    });


                                    mChildEventListener = mDatabaseReference.child(Constants.PACKET).child(key).addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                            if (dataSnapshot.getKey().equals(Constants.SEQ_ACK)) {
                                                seqAck = dataSnapshot.getValue(Integer.class);
                                                if (seqAck == 9) {
                                                    setPickupBtn.setVisibility(View.GONE);
                                                    mDatabaseReference.child(Constants.PACKET).child(Constants.PAIR_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            pairKey = dataSnapshot.getValue(String.class);
                                                            mDatabaseReference.child(Constants.PACKET).child(key).child(Constants.SEQ_ACK).setValue(10);
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                }
                                                else if (seqAck == 11) {
                                                    obtainRiderKey = mDatabaseReference.child(Constants.PAIR).child(pairKey).child(Constants.RIDER_KEY);
                                                    obtainRiderKey.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            riderKey = dataSnapshot.getValue(String.class);
                                                            mGeoFire.getLocation(riderKey, new com.firebase.geofire.LocationCallback() {
                                                                @Override
                                                                public void onLocationResult(String key, GeoLocation location) {
                                                                    if(location != null) {
                                                                        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location_blue)));
                                                                        marker.setTag(riderKey);
                                                                    }
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

                                                    riderEventListener = mDatabaseReference.child(Constants.GEO_LOCATION).child(riderKey).addChildEventListener(new ChildEventListener() {
                                                        @Override
                                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                                        }

                                                        @Override
                                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                                            mGeoFire.getLocation(riderKey, new com.firebase.geofire.LocationCallback() {
                                                                @Override
                                                                public void onLocationResult(String key, GeoLocation location) {
                                                                    marker.remove();
                                                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location_blue)));
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
                                                    mDatabaseReference.child(Constants.PACKET).child(key).child(Constants.SEQ_ACK).setValue(3);
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

                                    mDatabaseReference.child(Constants.PACKET).child(key).child(Constants.SEQ_ACK).setValue(9);

                                }

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
        resumed();
        startLocationUpdates();
        obtainKey = mDatabaseReference.child(Constants.DRIVER).child(userid).child(Constants.GEOKEY);
        obtainKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                key = dataSnapshot.getValue(String.class);
                obtainPairKey = mDatabaseReference.child(Constants.PACKET).child(key).child(Constants.PAIR_KEY);
                obtainPairKey.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        pairKey = dataSnapshot.getValue(String.class);
                        if(pairKey != null) {
                            obtainRiderKey = mDatabaseReference.child(Constants.PAIR).child(pairKey).child(Constants.RIDER_KEY);
                            obtainRiderKey.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    riderKey = dataSnapshot.getValue(String.class);
                                    if(riderKey != null) {
                                        mGeoFire.getLocation(riderKey, new com.firebase.geofire.LocationCallback() {
                                            @Override
                                            public void onLocationResult(String key, GeoLocation location) {
                                                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location_blue)));
                                                marker.setTag(riderKey);

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        riderEventListener = mDatabaseReference.child(Constants.GEO_LOCATION).child(riderKey).addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                            }

                                            @Override
                                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                                mGeoFire.getLocation(riderKey, new com.firebase.geofire.LocationCallback() {
                                                    @Override
                                                    public void onLocationResult(String key, GeoLocation location) {
                                                        marker.remove();
                                                        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location_blue)));

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

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
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

    // Prevent battery drain when activity is not in focus
    @Override
    protected void onPause() {
        super.onPause();
        paused();
        stopLocationUpdates();
        obtainKey = mDatabaseReference.child(Constants.DRIVER).child(userid).child(Constants.GEOKEY);
        obtainKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                key = dataSnapshot.getValue(String.class);
                obtainPairKey = mDatabaseReference.child(Constants.PACKET).child(key).child(Constants.PAIR_KEY);
                obtainPairKey.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        pairKey = dataSnapshot.getValue(String.class);
                        if(pairKey != null) {
                            obtainRiderKey = mDatabaseReference.child(Constants.PAIR).child(pairKey).child(Constants.RIDER_KEY);
                            obtainRiderKey.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    riderKey = dataSnapshot.getValue(String.class);
                                    if(riderKey != null) {
                                        mDatabaseReference.child(Constants.GEO_LOCATION).child(riderKey).removeEventListener(riderEventListener);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
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
    protected void onDestroy(){
        super.onDestroy();
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

    private void getKeyNodes(){
        obtainKey = mDatabaseReference.child(Constants.DRIVER).child(userid).child(Constants.GEOKEY);
        obtainKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                key = dataSnapshot.getValue(String.class);
                //we get all the packet of information the moment we also obtain the key.  This is done
                //in case a packet was created outside of the map.
                obtainRating = mDatabaseReference.child(Constants.DRIVER).child(userid).child(Constants.USER_RATING);
                obtainRating.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        rating = dataSnapshot.getValue(String.class);
                        getPacket();
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

    private void callBack(){
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                Location location = locationResult.getLastLocation();
                //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                //Driver is available, thus stores geolocation into section to be available for query
                if(isAvailable != null && seqAck != null) {
                    if (isAvailable.equals("true") && seqAck == 0) {
                        mAvailableGeoFire.setLocation(key, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {

                            }
                        });

                    }

                    //Driver is unavailable, thus stores geolocation into section not for query
                    else if (seqAck == 3) {
                        mGeoFire.setLocation(key, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {

                            }
                        });
                    }
                }

                //ToDo: Fix camera
                //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
                //mMap.animateCamera(cameraUpdate);

            }
        };
    }

    //Deletes the packet and geolocation.
    private void deleteLocation(){
        mDatabaseReference.child(Constants.AVAILABLE_GEOLOCATION).child(key).removeValue();
        mDatabaseReference.child(Constants.PACKET).child(key).removeValue();
    }

    private void checkIsOn(){
        if(isAvailable != null) {
            if (isAvailable.equals("true")) {
                setPickupBtn.setText(available);
            }
            else
            {
                setPickupBtn.setText(unavailable);
            }
        }
        else
            setPickupBtn.setText(unavailable);
    }

    //reads the packet of information from the database. In case packet was dropped or created outside this activity.
    private void getPacket() {
        obtainPacket = mDatabaseReference.child(Constants.PACKET).child(key);
        obtainPacket.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDatabaseReference.child(Constants.PACKET).child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            if (childDataSnapshot.getKey().equals(Constants.IS_AVAILABLE)) {
                                isAvailable = childDataSnapshot.getValue(String.class);
                            } else if (childDataSnapshot.getKey().equals(Constants.IS_ADVANCED_BOOKING)) {
                                isAdvancedBooking = childDataSnapshot.getValue(String.class);
                                if(isAdvancedBooking != null) {
                                    if (isAdvancedBooking.equals("true")) {
                                        setPickupBtn.setVisibility(View.GONE);
                                    }
                                }
                            } else if (childDataSnapshot.getKey().equals(Constants.SEQ_ACK)) {
                                seqAck = childDataSnapshot.getValue(Integer.class);
                            } else if (childDataSnapshot.getKey().equals(Constants.USER_RATING)) {
                                rating = childDataSnapshot.getValue(String.class);
                            } else if (childDataSnapshot.getKey().equals(Constants.PAIR_KEY)){
                                pairKey = childDataSnapshot.getValue(String.class);
                            }
                        }

                        initButton();
                        checkIsOn();
                        //Calls getDeviceLocation a second time.  This is because the first time it runs
                        //is to set up the map (initialize it to their position) but, the second time is to start the
                        //read,write and listen operations to the database.
                        getDeviceLocation();

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



    private void setPacket() {
        aExchangeInfo = new HashMap();
        aWriteInfo = new HashMap();

        //////////////////////////////////////////
        //Creates a brand new node within database
        //Packet
        // $geokey
        //   isAdvancedBooking -> false
        //   isAvailable ->       true
        //   userRating ->        (current rating)
        //   seqAck ->             8
        /////////////////////////////////////////

        aExchangeInfo.put(Constants.IS_AVAILABLE, "true");
        aExchangeInfo.put(Constants.USER_RATING, rating);
        aExchangeInfo.put(Constants.SEQ_ACK, 8);
        aExchangeInfo.put(Constants.IS_ADVANCED_BOOKING, "false");
        aExchangeInfo.put(Constants.PAIR_KEY, "false");

        aWriteInfo.put(Constants.PACKET + "/" + key, aExchangeInfo);

        mDatabaseReference.updateChildren(aWriteInfo, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.wtf(TAG, databaseError.getMessage());
                }
            }
        });

        // Initializes the variables being used to match what we are writing into the database
        isAvailable = "true";
        seqAck = 8;
        isAdvancedBooking = "false";
        pairKey = "false";

        checkIsOn();
        getDeviceLocation();
    }

    private void resetValues(){
        isAvailable = null;
        seqAck = null;
        isAdvancedBooking = null;
        pairKey = null;
    }

    private void initButton(){
        //button if clicked sets the driver to be available for pick ups
        setPickupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAvailable != null) {
                    if (isAvailable.equals("true")) {
                        stopLocationUpdates();
                        resetValues();
                        setPickupBtn.setText(unavailable);
                        deleteLocation();
                        //shuts off the listener to prevent stacked code
                        if(mChildEventListener != null) {
                            mDatabaseReference.child(Constants.PACKET).child(key).removeEventListener(mChildEventListener);
                        }
                    }
                    else if(isAvailable.equals("false")){
                        setPacket();
                    }
                }
                else if (isAvailable == null) {
                    setPickupBtn.setText(available);
                    setPacket();
                }

            }
        });
    }

    public static boolean activtyOn() {
        return activity;
    }

    public static void resumed() {
        activity = true;
    }

    public static void paused() {
        activity= false;
    }
}



