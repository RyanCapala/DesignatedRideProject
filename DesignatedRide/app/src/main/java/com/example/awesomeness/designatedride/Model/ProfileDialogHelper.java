package com.example.awesomeness.designatedride.Model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

    public ProfileDialogHelper(Context ctx, View profilePopupView, View confirmationView, FirebaseAuth mAuth, FirebaseUser mUser, Class profileActivityClass, Class loginActivityClass) {
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
        dialog.show();

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(profileActivityClass);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            goToActivity(loginActivityClass);

        }
    }

    private void goToActivity(Class activityClass) {
        Intent intent = new Intent(ctx, activityClass);
        ctx.startActivity(intent);
        ((Activity)ctx).finish();
    }


}