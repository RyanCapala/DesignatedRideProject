package com.example.awesomeness.designatedride.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.awesomeness.designatedride.R;
import com.example.awesomeness.designatedride.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by awesome on 2/22/18.
 * <p>
 * This class provides dialog functions for Rider and Driver profile so
 * we dont have to write the same functions on both profiles.
 */

public class ProfileDialogHelper {

    private AlertDialog.Builder dialogBuilder, _dialogBuilder, pwdDialogBuilder, alertBuilder;
    private AlertDialog dialog, confirmationDialog, pwdDialog, alertDialog;
    private Button updateProfileBtn, logoutBtn, yesButton, noButton;
    private TextView cancelTV;
    private Context ctx;
    private View profilePopupView, confirmationView, dialogView;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Class profileActivityClass;
    private Class loginActivityClass;

    private EditText resetDialog_pwdET;
    private EditText resetDialog_vpwdET;
    private TextView resetDialog_closeTV;
    private Button resetDialog_updtPwdBtn;

    private ImageButton yes_img_btn, no_img_btn;



    public ProfileDialogHelper(Context ctx, View dialogView, FirebaseAuth mAuth, FirebaseUser mUser) {
        this.ctx = ctx;
        this.dialogView = dialogView;
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
                dialog.dismiss();
                goToProfileActivity(profileActivityClass);

            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //showConfirmationDialog();
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
    public void createConfirmationPrompt() {
        alertBuilder = new AlertDialog.Builder(ctx);
        alertBuilder.setView(dialogView);
        alertDialog = alertBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));;
        alertDialog.show();

        yes_img_btn = (ImageButton) dialogView.findViewById(R.id.yes_img_btn);
        no_img_btn = (ImageButton) dialogView.findViewById(R.id.no_img_btn);
        yes_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //confirmationDialog.dismiss();
                //confirmationDialog = null;
                alertDialog.dismiss();
                alertDialog = null;
                UserDataHelper.deleteLocalUser(ctx);
                if (mUser != null && mAuth != null) {
                    mAuth.signOut();
                    ((Activity) ctx).finish();
                    Intent intent = new Intent(ctx, LoginActivity.class);
                    ctx.startActivity(intent);


                }
            }
        });

        no_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                alertDialog = null;
            }
        });

    }


    //----------------------------------------------------------------------------------------------
//    private void showConfirmationDialog() {
//
//        _dialogBuilder = new AlertDialog.Builder(ctx);
//        //yesButton = (Button) confirmationView.findViewById(R.id.yesButton);
//        //noButton = (Button) confirmationView.findViewById(R.id.noButton);
//        yesButton = (Button) dialogView.findViewById(R.id.yesButton);
//        noButton = (Button) dialogView.findViewById(R.id.noButton);
//
//        //_dialogBuilder.setView(confirmationView);
//        _dialogBuilder.setView(dialogView);
//        confirmationDialog = _dialogBuilder.create();
//        confirmationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        confirmationDialog.show();
//
//        yesButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                confirmationDialog.dismiss();
//                confirmationDialog = null;
//                UserDataHelper.deleteLocalUser(ctx);
//                signOutUser();
//            }
//        });
//
//        noButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                confirmationDialog.dismiss();
//                confirmationDialog = null;
//            }
//        });
//
//    }

    //----------------------------------------------------------------------------------------------
    public void createResetPwdDialog() {
        pwdDialogBuilder = new AlertDialog.Builder(ctx);
        resetDialog_pwdET = (EditText) dialogView.findViewById(R.id.passworddET_pwdDialog);
        resetDialog_vpwdET = (EditText) dialogView.findViewById(R.id.verifyPwdET_pwdDialog);
        resetDialog_updtPwdBtn = (Button) dialogView.findViewById(R.id.updatePwdBtn_pwdDialog);
        resetDialog_closeTV = (TextView) dialogView.findViewById(R.id.closeTV_pwdDialog);

        pwdDialogBuilder.setView(dialogView);
        pwdDialog = pwdDialogBuilder.create();
        pwdDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pwdDialog.show();

        resetDialog_updtPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pwd = resetDialog_pwdET.getText().toString().trim();
                String vpwd = resetDialog_vpwdET.getText().toString().trim();
                updateUserPassword(pwd, vpwd, resetDialog_pwdET, resetDialog_vpwdET);
                hideKeyboard();
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
                        Snackbar.make(dialogView, "Updated", Snackbar.LENGTH_LONG).show();
                    } else {
                        checker.pwdMessage(true, true, pwdET, vpwdET);
                    }
                }
            }
        } else {
            Snackbar.make(dialogView, "Empty Fields", Snackbar.LENGTH_LONG).show();
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

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)ctx.getSystemService(Context
                .INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(dialogView.getWindowToken(), 0);
    }


}
