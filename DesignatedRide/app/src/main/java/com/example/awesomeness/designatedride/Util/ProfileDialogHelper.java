package com.example.awesomeness.designatedride.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.awesomeness.designatedride.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by awesome on 2/22/18.
 * <p>
 * This class provides dialog functions for Rider and Driver profile so
 * we dont have to write the same functions on both profiles.
 */

public class ProfileDialogHelper {

    private AlertDialog.Builder dialogBuilder, _dialogBuilder, pwdDialogBuilder;
    private AlertDialog dialog, confirmationDialog, pwdDialog;
    private Button updateProfileBtn, logoutBtn, yesButton, noButton;
    private TextView cancelTV;
    private Context ctx;
    private View profilePopupView, confirmationView, pwdDialogView;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Class profileActivityClass;
    private Class loginActivityClass;

    private EditText resetDialog_pwdET;
    private EditText resetDialog_vpwdET;
    private TextView resetDialog_closeTV;
    private Button resetDialog_updtPwdBtn;


    public ProfileDialogHelper(Context ctx, View pwdDialogView, FirebaseAuth mAuth, FirebaseUser mUser) {
        this.ctx = ctx;
        this.pwdDialogView = pwdDialogView;
        this.mAuth = mAuth;
        this.mUser = mUser;
    }


    public ProfileDialogHelper(Context ctx, View profilePopupView, View confirmationView, FirebaseAuth mAuth, FirebaseUser mUser, Class profileActivityClass, Class loginActivityClass) {
        this.ctx = ctx;
        this.profilePopupView = profilePopupView;
        this.confirmationView = confirmationView;
        this.mAuth = mAuth;
        this.mUser = mUser;
        this.profileActivityClass = profileActivityClass;
        this.loginActivityClass = loginActivityClass;
    }

    //----------------------------------------------------------------------------------------------
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

    //----------------------------------------------------------------------------------------------
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
                confirmationDialog.dismiss();
                confirmationDialog = null;
                signOutUser();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
                confirmationDialog = null;
                confirmationDialog.dismiss();
            }
        });

    }

    //----------------------------------------------------------------------------------------------
    public void createResetPwdDialog() {
        pwdDialogBuilder = new AlertDialog.Builder(ctx);
        resetDialog_pwdET = (EditText) pwdDialogView.findViewById(R.id.passworddET_pwdDialog);
        resetDialog_vpwdET = (EditText) pwdDialogView.findViewById(R.id.verifyPwdET_pwdDialog);
        resetDialog_updtPwdBtn = (Button) pwdDialogView.findViewById(R.id.updatePwdBtn_pwdDialog);
        resetDialog_closeTV = (TextView) pwdDialogView.findViewById(R.id.closeTV_pwdDialog);

        pwdDialogBuilder.setView(pwdDialogView);
        pwdDialog = pwdDialogBuilder.create();
        pwdDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pwdDialog.show();

        resetDialog_updtPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pwd = resetDialog_pwdET.getText().toString().trim();
                String vpwd = resetDialog_vpwdET.getText().toString().trim();
                updateUserPassword(pwd, vpwd, resetDialog_pwdET, resetDialog_vpwdET);
            }
        });

        resetDialog_closeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwdDialog.dismiss();
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    private void updateUserPassword(String pwd, String vpwd, EditText pwdET, EditText vpwdET) {
        Checker checker = new Checker();
        if (!pwd.isEmpty() || !vpwd.isEmpty()) {
            if (checker.pwdFieldChecking(pwd, vpwd, pwdET, vpwdET)) {
                if (checker.pwdmatch(pwd, vpwd, pwdET, vpwdET)) {
                    if (checker.checkPwd(pwd) && checker.checkPwd(vpwd)) {
                        mUser.updatePassword(pwd);
                        clearPwdField(pwdET, vpwdET);
                        Snackbar.make(pwdDialogView, "Updated", Snackbar.LENGTH_LONG).show();
                    } else {
                        checker.pwdMessage(true, true, pwdET, vpwdET);
                    }
                }
            }
        } else {
            Snackbar.make(pwdDialogView, "Empty Fields", Snackbar.LENGTH_LONG).show();
        }
    }

    //----------------------------------------------------------------------------------------------
    private void signOutUser() {
        if (mUser != null && mAuth != null) {
            mAuth.signOut();
            gotoLoginActivity(loginActivityClass);

        }
    }

    //----------------------------------------------------------------------------------------------
    private void goToProfileActivity(Class activityClass) {
        Intent intent = new Intent(ctx, activityClass);
        intent.putExtra(Constants.INTENT_KEY, mUser.getUid());
        ctx.startActivity(intent);
        ((Activity) ctx).finish();
    }

    //----------------------------------------------------------------------------------------------
    private void gotoLoginActivity(Class activityClass) {
        Intent intent = new Intent(ctx, activityClass);
        ctx.startActivity(intent);
        ((Activity) ctx).finish();
    }

    //----------------------------------------------------------------------------------------------
    private void clearPwdField(EditText et1, EditText et2) {
        et1.setText("");
        et2.setText("");
    }


}
