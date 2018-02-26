package com.example.awesomeness.designatedride._RiderActivities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.awesomeness.designatedride.Util.Constants;
import com.example.awesomeness.designatedride.Util.ProfileDialogHelper;
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

    //
    private CircleImageView profileImage;
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
        mStorage = FirebaseStorage.getInstance().getReference().child(Constants.PROFILE_IMAGE);

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

                    mDbRef.child(Constants.RIDER).child(uid).child(Constants.PROFILE);
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

                View profile_dialog_view = getLayoutInflater().inflate(R.layout
                        .profile_dialog_popup, null);
                View confirm_dialog_view = getLayoutInflater().inflate(R.layout
                        .confirmation_dialog, null);
                ProfileDialogHelper profileDialogHelper = new ProfileDialogHelper(RiderActivity.this, profile_dialog_view, confirm_dialog_view, mAuth, mUser,
                        RiderProfileActivity.class, LoginActivity.class);
                profileDialogHelper.createPopupDialog();


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
                    DatabaseReference databaseReference = mDbRef2.child(Constants.USER).child(uid).child(Constants.PROFILE);
                    databaseReference.child(Constants.USERIMAGE).setValue(mImageUri.toString());
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
        finish();
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
                child(Constants.USER).
                child(uid).
                child(Constants.PROFILE).
                child(Constants.USERIMAGE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String imgUrl = dataSnapshot.getValue(String.class);

                        if (imgUrl != null) {
                            Picasso.with(RiderActivity.this).load(imgUrl).into(profileImage);
                            //Toast.makeText(RiderActivity.this, imgUrl, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RiderActivity.this, "imagePath: " + imgUrl, Toast.LENGTH_LONG).show();
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



}
