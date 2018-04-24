package com.example.awesomeness.designatedride.util.Widgets;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.DatePicker;

import com.example.awesomeness.designatedride.R;
import com.example.awesomeness.designatedride.util.MonthInterpreter;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
    private static final String TAG = "DatePickerFragment";
    private SimpleDateFormat dateFormatterEntry = new SimpleDateFormat("M/dd/yyyy");
    private Calendar calendar;
    int year;
    int month;
    int day;

    private DatePickedListener listener;

    public static interface DatePickedListener {
        void onDatePicked(String date);
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(TAG, "onCreateDialog");

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        listener = (DatePickedListener) getActivity();

        return new DatePickerDialog(getActivity(), R.style.AlertDialogTheme, this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        DateFormatSymbols dfs = new DateFormatSymbols();
        String monthStr = dfs.getMonths()[month];
        String date = String.format(Locale.getDefault(), "%02d", day) + "-" +
                monthStr + "-" + String.format(Locale.getDefault(), "%d", year);
        listener.onDatePicked(date);
    }
}
