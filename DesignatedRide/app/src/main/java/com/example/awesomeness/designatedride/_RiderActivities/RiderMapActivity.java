package com.example.awesomeness.designatedride._RiderActivities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import com.example.awesomeness.designatedride.R;
import com.example.awesomeness.designatedride.util.Constants;
import com.example.awesomeness.designatedride.util.Checker;
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


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    Button acceptButton;
    Button declineButton;
    TextView txt;
    View text_box;
    EditText time;
    EditText destination;
    EditText pickUp;

    //List
    private ArrayList<Marker> availableDrivers;
    private List<Address> pickUpAddress;
    private List<Address> destinationAddress;
    private Address location;
    private double Longitude;
    private double Latitude;

    //Marker
    private Marker driver;
    private Marker driverMarker;
    private Marker locMarker;

    //FireBase
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private String userid;

    private String key;
    private String riderRating;
    private String driverRating;
    private String driverKey;
    private Integer seqAck;
    private Integer filter;
    private Integer temp;
    private String mPushKey;
    private String pairKey;
    private String destinationLocation;
    private String pickUpLocation;
    private String timeItOccurs;


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
    private GeoFire mLocation;
    private DatabaseReference mAvailableGeoLocationRef;
    private DatabaseReference mGeoLocationRef;
    private DatabaseReference mChildAvailable;
    private DatabaseReference mChildLocation;
    private DatabaseReference mLocationRef;
    private DatabaseReference mChildDropOff;
    private ChildEventListener childEventListener;
    private ChildEventListener driverEventListener;
    private Geocoder geoCoder;

    private Query obtainKey;
    private Query obtainRating;
    private Query obtainDriverKey;
    private Query obtainPairKey;
    private Query obtainColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_map);
        initWidgets();

        mapFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(DES_TIME);
        mLocationRequest.setFastestInterval(EXP_TIME);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        confirmation = new AlertDialog.Builder(RiderMapActivity.this);

        callBack();

        mDatabase = FirebaseDatabase.getInstance();

        mDatabaseReference = mDatabase.getReference();
        mChildLocation = mDatabase.getReference();
        mChildAvailable = mDatabase.getReference();
        mChildDropOff = mDatabase.getReference();

        mAvailableGeoLocationRef = mChildAvailable.child(Constants.AVAILABLE_GEOLOCATION);
        mGeoLocationRef = mChildLocation.child(Constants.GEO_LOCATION);
        mLocationRef = mChildDropOff.child(Constants.LOCATION);

        mAvailableGeoFire = new GeoFire(mAvailableGeoLocationRef);
        mGeoFire = new GeoFire(mGeoLocationRef);
        mLocation = new GeoFire(mLocationRef);

        mAuth = FirebaseAuth.getInstance();
        userid = mAuth.getCurrentUser().getUid();

        availableDrivers = new ArrayList<>();
        
        setPickupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RiderMapActivity.this, "Requested a ride!", Toast.LENGTH_SHORT).show();
                geoCoder = new Geocoder(RiderMapActivity.this);
                try {
                    if (checkPickUp(pickUp, destination, time)) {
                        pickUpAddress = geoCoder.getFromLocationName(pickUpLocation, 1);
                        destinationAddress = geoCoder.getFromLocationName(destinationLocation, 1);
                        if (checkAddress(destinationAddress,destinationLocation) && checkTime(timeItOccurs)) {
                            removeFields();
                            location = destinationAddress.get(0);
                            Longitude = location.getLongitude();
                            Latitude = location.getLatitude();
                            mPushKey = FirebaseDatabase.getInstance().getReference(Constants.TEXT_BOX + "/" + Constants.PAIR_KEY + "/").push().getKey();
                            mGeoFire.setLocation(mPushKey, new GeoLocation(Latitude, Longitude), new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {

                                }
                            });

                            obtainKey = mDatabaseReference.child(Constants.RIDER).child(userid).child(Constants.GEOKEY);
                            obtainKey.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    key = dataSnapshot.getValue(String.class);
                                    mDatabaseReference.child(Constants.PAIR).child(mPushKey).child(Constants.RIDER_KEY).setValue(key);
                                    mDatabaseReference.child(Constants.PAIR).child(Constants.RIDER_KEY).child(Constants.PAIR_KEY).setValue(mPushKey);
                                    obtainRating = mDatabaseReference.child(Constants.RIDER).child(key).child(Constants.USER_RATING);
                                    obtainRating.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            riderRating = dataSnapshot.getValue(String.class);
                                            mDatabaseReference.child(Constants.TEXT_BOX).child(mPushKey).child(Constants.USER_RATING).setValue(riderRating);
                                            mGeoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                                                @Override
                                                public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                                                    driverKey = dataSnapshot.getKey();
                                                    Longitude = location.longitude;
                                                    Latitude = location.latitude;

                                                    obtainColor = mDatabaseReference.child(Constants.PACKET).child(driverKey).child(Constants.SEQ_ACK);
                                                    obtainColor.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            filter = dataSnapshot.getValue(Integer.class);
                                                            if(filter == 0){
                                                                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Latitude, Longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car_white)));
                                                            }
                                                            else
                                                                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Latitude, Longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car)));
                                                            marker.setTag(driverKey);
                                                            availableDrivers.add(marker);
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

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

                                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                                @Override
                                                public boolean onMarkerClick(Marker marker) {

                                                    driverMarker = marker;

                                                    mDatabaseReference.child(Constants.PACKET).child(driverKey).child(Constants.SEQ_ACK).setValue(6);
                                                    mDatabaseReference.child(Constants.PACKET).child(driverKey).child(Constants.IS_AVAILABLE).setValue("false");
                                                    if (seqAck != null) {
                                                        temp = seqAck;
                                                        seqAck = 6;
                                                    }

                                                    driverKey = (String) marker.getTag();
                                                    driverMarker.setVisible(false);

                                                    obtainRating = mDatabaseReference.child(Constants.PACKET).child(driverKey).child(Constants.USER_RATING);
                                                    obtainRating.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            driverRating = dataSnapshot.getValue(String.class);
                                                            String message = driverRating + "\n" + "Choose this Driver?";
                                                            txt.setText(message);
                                                            confirmation.setView(text_box);
                                                            dialogBox = confirmation.create();
                                                            dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                            dialogBox.show();
                                                            acceptButton.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    //ToDO: set transactions
                                                                    //ToDO: handle packet kills
                                                                    childEventListener = mDatabaseReference.child(Constants.PACKET).child(driverKey).addChildEventListener(new ChildEventListener() {
                                                                        @Override
                                                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                                        }

                                                                        @Override
                                                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                                                            if (dataSnapshot.getKey().equals(Constants.SEQ_ACK)) {
                                                                                mProgressDialog.dismiss();
                                                                                seqAck = dataSnapshot.getValue(Integer.class);
                                                                                if (seqAck == 0 || seqAck == 4 || seqAck == 5 || seqAck == 7) {
                                                                                    Toast.makeText(RiderMapActivity.this, "Driver is current unavailable", Toast.LENGTH_LONG).show();
                                                                                    mDatabaseReference.child(Constants.PACKET).child(driverKey).removeEventListener(childEventListener);
                                                                                    mDatabaseReference.child(Constants.TEXT_BOX).child(mPushKey).removeValue();
                                                                                    driverMarker.remove();
                                                                                } else if (seqAck == 2) {
                                                                                    mDatabaseReference.child(Constants.PACKET).child(Constants.PAIR_KEY).setValue(mPushKey);
                                                                                    mDatabaseReference.child(Constants.TEXT_BOX).child(mPushKey).removeValue();
                                                                                    mDatabaseReference.child(Constants.PACKET).child(driverKey).child(Constants.SEQ_ACK).setValue(3);
                                                                                } else if (seqAck == 3) {
                                                                                    removeFields();
                                                                                    for (int j = 0; j < availableDrivers.size(); j++) {
                                                                                        driver = availableDrivers.get(j);
                                                                                        driver.remove();
                                                                                    }
                                                                                    Toast.makeText(RiderMapActivity.this, "Connected with Driver", Toast.LENGTH_LONG).show();
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
                                                                                            mDatabaseReference.child(Constants.PACKET).child(driverKey).removeEventListener(childEventListener);
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

                                                                    mDatabaseReference.child(Constants.PAIR).child(driverKey).child(Constants.PAIR_KEY).setValue(mPushKey);
                                                                    mDatabaseReference.child(Constants.PACKET).child(driverKey).child(Constants.SEQ_ACK).setValue(1);

                                                                    mProgressDialog.setMessage("Confirming Driver Availability...");
                                                                    mProgressDialog.show();
                                                                    dialogBox.dismiss();
                                                                    if (text_box.getParent() != null) {
                                                                        ((ViewGroup) text_box.getParent()).removeView(text_box);
                                                                    }
                                                                }
                                                            });

                                                            declineButton.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    if (text_box.getParent() != null) {
                                                                        ((ViewGroup) text_box.getParent()).removeView(text_box);
                                                                    }
                                                                    driverMarker.setVisible(true);
                                                                    dialogBox.dismiss();
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    if (seqAck != null) {
                                                        if (seqAck == 6) {
                                                            seqAck = temp;
                                                        }
                                                    }

                                                    return false;
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
                        else if(checkAddress(destinationAddress,destinationLocation) && checkAddress(pickUpAddress,pickUpLocation) && checkTime(timeItOccurs)){
                            //ToDO: send information to appointment manager as it can't be handled by the map. Via Broadcast & Receiver?
                        }
                    }
                    } catch(IOException e){
                    Toast.makeText(RiderMapActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

                    } catch(IllegalArgumentException e){
                        Toast.makeText(RiderMapActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                    // end temp location

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start location updates. Will ask for permissions if necessary
        startLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapLocationPermissionsGranted) {
            startLocationUpdates();
        } else {
            Toast.makeText(this, "Location permissions not granted!", Toast.LENGTH_SHORT).show();
        }
        
        //ToDO: correct to handle text kills
        obtainKey = mDatabaseReference.child(Constants.RIDER).child(userid).child(Constants.GEOKEY);
        obtainKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                key = dataSnapshot.getValue(String.class);
                obtainPairKey = mDatabaseReference.child(Constants.PAIR).child(key).child(Constants.PAIR_KEY);
                obtainPairKey.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        pairKey = dataSnapshot.getValue(String.class);
                        if (pairKey != null) {
                            obtainDriverKey = mDatabaseReference.child(Constants.PAIR).child(pairKey).child(Constants.DRIVER_KEY);
                            obtainDriverKey.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    driverKey = dataSnapshot.getValue(String.class);
                                    if (driverKey != null) {
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

                                        mLocation.getLocation(pairKey, new com.firebase.geofire.LocationCallback() {
                                            @Override
                                            public void onLocationResult(String key, GeoLocation location) {
                                                if (location != null) {
                                                    locMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car)));
                                                    locMarker.setTag(pairKey);
                                                }
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
                                                        driverMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location_blue)));
                                                        driverMarker.setTag(driverKey);

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
        getDeviceLocation();
    }

    // Prevent battery drain when activity is not in focus
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        
        //ToDO: correct to handle text kills
        obtainKey = mDatabaseReference.child(Constants.RIDER).child(userid).child(Constants.GEOKEY);
        obtainKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                key = dataSnapshot.getValue(String.class);
                obtainPairKey = mDatabaseReference.child(Constants.PAIR).child(key).child(Constants.PAIR_KEY);
                obtainPairKey.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        pairKey = dataSnapshot.getValue(String.class);
                        if (pairKey != null) {
                            obtainDriverKey = mDatabaseReference.child(Constants.PAIR).child(pairKey).child(Constants.DRIVER_KEY);
                            obtainDriverKey.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    driverKey = dataSnapshot.getValue(String.class);
                                    if (driverKey != null) {
                                        if (driverEventListener != null) {
                                            mDatabaseReference.child(Constants.GEO_LOCATION).child(driverKey).removeEventListener(driverEventListener);
                                        }
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

    private void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates: STOPPED LOCATION UPDATES");
        if (mapFusedLocationProviderClient != null) {
            if (mLocationCallback != null) {
                mapFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            }
        }
    }

    private void startLocationUpdates() {
        if (mapLocationPermissionsGranted) {
            try {
                Log.d(TAG, "startLocationUpdates: STARTED LOCATION UPDATES");
                mapFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            } catch (SecurityException e) {
                Log.d(TAG, "startLocationUpdates: " + e.getMessage());
            }
        } else {
            getLocationPermissions();
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
                            Log.d(TAG, "onRequestPermissionsResult: Permissions not granted!");
                            return;
                        }
                    }

                    mapLocationPermissionsGranted = true;
                    Log.d(TAG, "onRequestPermissionsResult: Permissions granted!");
                    initializeMap();
                    startLocationUpdates();
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

                            
                            //ToDO: change location write location depending on time
                            mGeoFire.setLocation(key, new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude()), new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {

                                }
                            });

                            //TODo: change query type depending on time
                            mGeoQuery = mAvailableGeoFire.queryAtLocation(new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude()), 0.5);

                        } else {
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
                Log.d(TAG, "getLocationPermissions: Have permissions. Starting Updates");
                startLocationUpdates();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initWidgets() {
        setPickupBtn = (Button) findViewById(R.id.setPickupBtn_ridermap);
        text_box = getLayoutInflater().inflate(R.layout
                .text_box, null);
        acceptButton = (Button) text_box.findViewById(R.id.acceptButton);
        declineButton = (Button) text_box.findViewById(R.id.declineButton);
        txt = (TextView) text_box.findViewById(R.id.rider_name);
        destination = findViewById(R.id.destination);
        time = findViewById(R.id.time);
        pickUp = findViewById(R.id.pickUp);
        mProgressDialog = new ProgressDialog(RiderMapActivity.this);


        // Add <- arrow on actionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    // Pad map appropriately to not obscure google logo/copyright info
    // This is a generic function, wont look nice on most devices
    // probably needs some math to calculate padding size (its in pixels)
    private void padGoogleMap() {
        //    //int[] locationOnScreen; // [x, y]
        //    //findViewById(R.id.setPickupBtn_ridermap).getLocationOnScreen(locationOnScreen);

        //    // left, top, right, bottom
        mMap.setPadding(0, 0, 0, 150);

    }

    private void callBack() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
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

    private void removeFields(){
        setPickupBtn.setVisibility(View.GONE);
        pickUp.setVisibility(View.GONE);
        destination.setVisibility(View.GONE);
        time.setVisibility(View.GONE);
    }

    // ToDO: Set to proper locations
    private void returnFields(){
        setPickupBtn.setVisibility(View.VISIBLE);
        pickUp.setVisibility(View.VISIBLE);
        destination.setVisibility(View.VISIBLE);
        time.setVisibility(View.VISIBLE);
    }
    
    //ToDO: make checker to determine if a packet is being held, set to proper locations
    private void checkPacket() {
        
    }

    // ToDO: Possibly move to checker at some point? Depending on how much more code is added
    private boolean checkPickUp(EditText pickUpField, EditText destinationField, EditText timeField){
        boolean flag = true;

        pickUpLocation = pickUpField.getText().toString().trim();
        destinationLocation = destinationField.getText().toString().trim();
        timeItOccurs = timeField.getText().toString().trim();

        if(pickUpLocation.isEmpty()) {
            pickUpField.setError("Pick up location required");
            flag = false;
        }
        if(destinationLocation.isEmpty()) {
            destinationField.setError("Destination required");
            flag = false;
        }
        if(timeItOccurs.isEmpty()) {
            timeField.setError("Time to be picked up required");
            flag = false;
        }

        return true;
    }

    // ToDO: Currently its always true if text entered is 12pm.  This is to by pass things to allow for easier testing of code
    private boolean checkTime(String time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hhmmaa", Locale.ENGLISH);
        SimpleDateFormat otherDateFormat = new SimpleDateFormat("hh:mm:ss",Locale.ENGLISH);
        try {
            Date format = simpleDateFormat.parse(time);
            long currentTime = Calendar.getInstance().getTimeInMillis();
            long timeDiff = format.getTime() - currentTime;
            long hourDiff = timeDiff/3600000;
            Toast.makeText(RiderMapActivity.this,hourDiff +"",Toast.LENGTH_LONG).show();
        }
        catch(ParseException e){Toast.makeText(RiderMapActivity.this,"Time format entered incorrectly",Toast.LENGTH_LONG).show();}
        return(time.toLowerCase().equals("12pm"));
    }

    // ToDo: If it does exist probably need to place a map marker and have the user confirm the address pulled is correct.
    private boolean checkAddress(List<Address> address, String addressLocation){
        if(address == null || address.size() <= 0){
            Toast.makeText(RiderMapActivity.this,addressLocation + " is not a valid address",Toast.LENGTH_LONG).show();
            return false;
        }
        else return true;
    }

    //ToDO: set to proper locations
    private void killText(){
        mDatabaseReference.child(Constants.TEXT_BOX).child(mPushKey).removeValue();
        mDatabaseReference.child(Constants.PAIR).child(mPushKey).removeValue();
        mDatabaseReference.child(Constants.PAIR).child(Constants.RIDER_KEY).child(Constants.PAIR_KEY).child(mPushKey).removeValue();
    }
}


