package com.example.awesomeness.designatedride._RiderActivities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.awesomeness.designatedride.R;
import com.example.awesomeness.designatedride.util.AppointmentInformation;
import com.example.awesomeness.designatedride.util.Constants;
import com.example.awesomeness.designatedride.util.HandleFileReadWrite;
import com.example.awesomeness.designatedride.util.MonthInterpreter;

import org.w3c.dom.Text;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class RiderAddAppointmentActivity extends AppCompatActivity {

    private final static String metaFile = "appointments_metadata.txt";
    private final static String FILENAME_POSTFIX = ".txt";
    private static String time;
    private static String date;
    private static TextView appointmentTimeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_add_appointment);

        final EditText hospitalName = (EditText) findViewById(R.id.hospitalName);
        final EditText hospitalAddress = (EditText) findViewById(R.id.destinationAddress);
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.advancedBookingStatusRadioGroup);
        radioGroup.check(R.id.yesRadioButton);
        final EditText appointmentNotes = (EditText) findViewById(R.id.appointmentNotes);

        time = "";
        date = "";


        Button confirmButton = (Button) findViewById(R.id.addAppointmentConfirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int fileCount = 0;
                String name = hospitalName.getText().toString();
                String address = hospitalAddress.getText().toString();
                boolean status =  radioGroup.getCheckedRadioButtonId() == R.id.yesRadioButton;
                String notes = appointmentNotes.getText().toString();

                if (name.equals("") || address.equals("") || date.equals("") || time.equals("")) {
                    Toast.makeText(getApplicationContext(),"Field(s) is empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                String fileName = new SimpleDateFormat("yyyy_dd_MM_HH_mm_ss", Locale.US).format(new Date()) + FILENAME_POSTFIX;

                HandleFileReadWrite writer = new HandleFileReadWrite();
                //write to file name to metadata
                writer.open(RiderAddAppointmentActivity.this, metaFile, HandleFileReadWrite.fileOperator.OPEN_APPEND);
                writer.writeLine(fileName);
                writer.close();

                writer.open(RiderAddAppointmentActivity.this, fileName, HandleFileReadWrite.fileOperator.OPEN_WRITE);
                writer.writeLine(name);
                writer.writeLine(address);
                writer.writeLine(time);
                writer.writeLine(date);
                writer.writeLine(status ? "yes" : "no");
                writer.writeLine(notes);
                writer.close();

                finish();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancelAppointmentConfirmButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageButton appointmentTime = (ImageButton) findViewById(R.id.appoitmentTimeButon);
        appointmentTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), Constants.DATE_PICKER_TAG);

                newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), Constants.TIME_PICKER_TAG);


            }
        });

        appointmentTimeView = (TextView) findViewById(R.id.appointmentTimeView);

    }

    //
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), R.style.AlertDialogTheme, this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            time = hourOfDay + ":" + minute;
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            time = "";
            Fragment dateFragment = getFragmentManager().findFragmentByTag(Constants.DATE_PICKER_TAG);
            if (dateFragment != null) {
                getFragmentManager().beginTransaction().remove(dateFragment).commit();
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), R.style.AlertDialogTheme, this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            date = (new MonthInterpreter()).getName(month) + " " + day + ", " + year;

            appointmentTimeView.setText(time + "\n" + date);
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            date = "";
            appointmentTimeView.setText("");
        }
    }
    //

}
