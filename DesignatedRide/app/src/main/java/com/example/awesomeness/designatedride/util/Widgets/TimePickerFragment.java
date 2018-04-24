package com.example.awesomeness.designatedride.util.Widgets;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.example.awesomeness.designatedride.R;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private TimePickedListener listener;

    public static interface TimePickedListener {
        void onTimePicked(String time);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        listener =  (TimePickedListener)getActivity();
        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(),  R.style.AlertDialogTheme, this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Boolean isAM = hourOfDay < 12;
        String ampm = isAM ? " AM" : " PM";
        hourOfDay = isAM ? hourOfDay : hourOfDay - 12;
        String minuteStr = String.format("%02d", minute);
        String timeString = hourOfDay + ":" + minuteStr + ampm;
        listener.onTimePicked(timeString);
    }
}