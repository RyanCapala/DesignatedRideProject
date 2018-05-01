package com.example.awesomeness.designatedride.util.Widgets;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.example.awesomeness.designatedride.R;

import java.util.Calendar;

public class DatePickerFragmentHelper extends DialogFragment {
    private Calendar calendar;
    private int month, day, year;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), R.style.datepicker,
                (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
    }
}
