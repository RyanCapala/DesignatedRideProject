package com.example.awesomeness.designatedride.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.awesomeness.designatedride.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by awesome on 2/22/18.
 *
 * This class provides dialog functions for Rider and Driver profile so
 * we dont have to write the same functions on both profiles.
 */

public class ProfileDialogHelper {

    private AlertDialog.Builder dialogBuilder, _dialogBuilder;
    private AlertDialog dialog, confirmationDialog;
    private Button updateProfileBtn, logoutBtn, yesButton, noButton;
    private TextView cancelTV;
    private Context ctx;
    private View profilePopupView, confirmationView;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Class profileActivityClass;
    private Class loginActivityClass;

    private String INTENT_KEY = "userid";

    public ProfileDialogHelper(Context ctx, View profilePopupView, View confirmationView,
                               FirebaseAuth mAuth, FirebaseUser mUser,
                               Class profileActivityClass, Class loginActivityClass) {
        this.ctx = ctx;
        this.profilePopupView = profilePopupView;
        this.confirmationView = confirmationView;
        this.mAuth = mAuth;
        this.mUser = mUser;
        this.profileActivityClass = profileActivityClass;
        this.loginActivityClass = loginActivityClass;
    }

    public void createPopupDialog() {
        dialogBuilder = new AlertDialog.Builder(ctx);

        updateProfileBtn = (Button) profilePopupView.findViewById(R.id.btn_updateProfile_drvrPopup);
        logoutBtn = (Button) profilePopupView.findViewById(R.id.btn_logout_drvrPopup);
        cancelTV = (TextView) profilePopupView.findViewById(R.id.tv_cancelLink_drvrPopup);

        dialogBuilder.setView(profilePopupView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfileActivity(profileActivityClass);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataHelper.deleteLocalUser(ctx);
                dialog.dismiss();
                showConfirmationDialog();
            }
        });

        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void showConfirmationDialog() {

        _dialogBuilder = new AlertDialog.Builder(ctx);
        yesButton = (Button) confirmationView.findViewById(R.id.yesButton);
        noButton = (Button) confirmationView.findViewById(R.id.noButton);

        _dialogBuilder.setView(confirmationView);
        confirmationDialog = _dialogBuilder.create();
        confirmationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        confirmationDialog.show();

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutUser();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
            }
        });

    }

    private void signOutUser() {
        if (mUser != null && mAuth != null) {
            mAuth.signOut();
            gotoLoginActivity(loginActivityClass);

        }
    }

    private void goToProfileActivity(Class activityClass) {
        Intent intent = new Intent(ctx, activityClass);
        intent.putExtra(INTENT_KEY, mUser.getUid());
        ctx.startActivity(intent);
        ((Activity)ctx).finish();
    }

    private void gotoLoginActivity(Class activityClass) {
        Intent intent = new Intent(ctx, activityClass);
        ctx.startActivity(intent);
        ((Activity)ctx).finish();
    }


}
