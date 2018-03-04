package com.example.awesomeness.designatedride._RiderActivities;

import android.app.DialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.example.awesomeness.designatedride.R;

public class AppointmentManagerFragment extends DialogFragment {

    private static final String TAG = "AppointmentManager";


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Log.i(TAG, "ON CREATED DIALOG");

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);

        //
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View appointmentManagerView = inflater.inflate(R.layout.appointment_manager, null);

        //
        builder.setView(appointmentManagerView)
                .setCancelable(true);

        ImageButton addAppointmentButton = (ImageButton) appointmentManagerView.findViewById(R.id.addAppointmentButton);
        addAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Add button clicked");
                //go to new activity
                Intent nextActivity = new Intent(getActivity().getApplicationContext(), RiderAddAppointmentActivity.class);
                startActivity(nextActivity);
            }
        });

        ImageButton viewAppointmentButton = (ImageButton) appointmentManagerView.findViewById(R.id.viewAppointmentButton);
        viewAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "View button clicked");
                Intent nextActivity = new Intent(getActivity().getApplicationContext(), RiderViewAppointmentActivityWrapper.class);
                startActivity(nextActivity);
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }



}
