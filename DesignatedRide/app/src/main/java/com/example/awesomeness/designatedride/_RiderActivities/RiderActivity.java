package com.example.awesomeness.designatedride._RiderActivities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.awesomeness.designatedride.Activities.LoginActivity;
import com.example.awesomeness.designatedride.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class RiderActivity extends AppCompatActivity {
    public static final String TAG = "RiderActivity";

    private DatabaseReference mDbRef;
    private DatabaseReference mDbRef2;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private StorageReference mStorage;
    private Uri mImageUri;
    public static final int GALLERY_CODE = 1;

    private String uid;
    private String email;
    private String profileImg = "profileImage";
    private String cUser = "User";
    private String cProfile = "Profile";
    private String cUserImage = "userImage";

    //
    private CircleImageView profileImage;
    //private ImageView profileImage;
    private ImageButton viewProfileBtn;
    private ImageButton requestrideBtn;
    private ImageButton calendarBtn;
    private ImageButton settingsBtn;

    private ProgressDialog mProgress;

    //----------
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button updateProfileBtn, logoutBtn, yesButton, noButton;
    private TextView cancelTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider);

//        Toolbar userToolbar = (Toolbar) findViewById(R.id.toolbar_user);
//        setSupportActionBar(userToolbar);




        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDbRef = mDatabase.getReference();
        mDbRef2 = mDatabase.getReference();
        mUser = mAuth.getCurrentUser();
        mDbRef.keepSynced(true);
        mStorage = FirebaseStorage.getInstance().getReference().child(profileImg);




        initWidgets();
        getProfileImage();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    uid = mUser.getUid();
                    Log.d(TAG, "onAuthStateChanged: ===>>uid: " + uid);
                    email = mUser.getEmail();

                    mDbRef.child("Rider").child(uid).child("Profile");
                }
            }
        };

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);

            }
        });

        viewProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createPopupDialog();

                /*********************************************************
                DatabaseReference databaseReference = mDbRef2.child(cUser).child(uid).child(cProfile).child(cUserImage);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String imgPath = dataSnapshot.getValue(String.class);
                        if (imgPath.isEmpty()) {
                            Toast.makeText(RiderActivity.this, "Empty Image path.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                *********************************************************/

            }
        });

        requestrideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(RiderMapActivity.class);
            }
        });

        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        getProfileImage();

    }//End of onCreate

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            mProgress.setMessage("Uploading image...");
            mProgress.show();
            mImageUri = data.getData();
            saveProfileImage(mImageUri);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //----------------------------------------------------------------------------------------------
    private void saveProfileImage(Uri imgUri) {

        if (imgUri != null) {
            StorageReference imagePath = mStorage.child(uid);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            final byte[] data = baos.toByteArray();
            UploadTask uploadTask = imagePath.putBytes(data);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgress.dismiss();
                    mImageUri = taskSnapshot.getDownloadUrl();
                    Log.d(TAG, "onSuccess: >>>> URL: " + mImageUri.toString());
                    DatabaseReference databaseReference = mDbRef2.child(cUser).child(uid).child(cProfile);
                    databaseReference.child(cUserImage).setValue(mImageUri.toString());
                    Picasso.with(RiderActivity.this).load(mImageUri).into(profileImage);
                    Toast.makeText(RiderActivity.this, "Upload finish", Toast.LENGTH_LONG).show();

                }
            });


        } else {

        }

    }

    //----------------------------------------------------------------------------------------------
    private void initWidgets() {
        //profileImage = (ImageView) findViewById(R.id.profileImgView_rider);
        profileImage = (CircleImageView) findViewById(R.id.profileImgView_rider);
        viewProfileBtn = (ImageButton) findViewById(R.id.viewProfileImgBtn_rider);
        requestrideBtn = (ImageButton) findViewById(R.id.requestRideImgBtn_rider);
        calendarBtn = (ImageButton) findViewById(R.id.calendarImgBtn_rider);
        settingsBtn = (ImageButton) findViewById(R.id.settingsImgBtn_rider);
        mProgress = new ProgressDialog(this);
    }

    //----------------------------------------------------------------------------------------------
    private void gotoActivity(Class activityClass) {
        startActivity(new Intent(RiderActivity.this, activityClass));
       // finish();
    }

    //----------------------------------------------------------------------------------------------
    private void signOutUser() {
        if (mUser != null && mAuth != null) {
            mAuth.signOut();
            gotoActivity(LoginActivity.class);
        }
    }

    //----------------------------------------------------------------------------------------------
    private void getProfileImage() {
        uid = mUser.getUid();
        mDbRef2.
                child(cUser).
                child(uid).
                child(cProfile).
                child(cUserImage)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String imgUrl = dataSnapshot.getValue(String.class);

                        if (imgUrl != null) {
                            Picasso.with(RiderActivity.this).load(imgUrl).into(profileImage);
                            //Toast.makeText(RiderActivity.this, imgUrl, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RiderActivity.this, "imagePath: " +imgUrl, Toast.LENGTH_LONG).show();
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
        });
    }

    //----------------------------------------------------------------------------------------------
    private void createPopupDialog() {

        dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.driver_profile_dialog_popup, null);

        updateProfileBtn = (Button) view.findViewById(R.id.btn_updateProfile_drvrPopup);
        logoutBtn = (Button) view.findViewById(R.id.btn_logout_drvrPopup);
        cancelTV = (TextView) view.findViewById(R.id.tv_cancelLink_drvrPopup);

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        //-----------
        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //-----------
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                //will delay the next dialog
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showConfirmationDialog();
                    }
                }, 100);

            }
        });

        //-----------
        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }//End of createPopupDialog

    //----------------------------------------------------------------------------------------------
    private void showConfirmationDialog() {

        final AlertDialog _dialog;
        AlertDialog.Builder _dialogBuilder;
        View view = getLayoutInflater().inflate(R.layout.confirmation_dialog, null);
        _dialogBuilder = new AlertDialog.Builder(this);
        yesButton = (Button) view.findViewById(R.id.yesButton);
        noButton = (Button) view.findViewById(R.id.noButton);

        _dialogBuilder.setView(view);
        _dialog = _dialogBuilder.create();
        _dialog.show();

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signOutUser();

            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                _dialog.dismiss();

            }
        });

    }//End of showConfirmationDialog



}
