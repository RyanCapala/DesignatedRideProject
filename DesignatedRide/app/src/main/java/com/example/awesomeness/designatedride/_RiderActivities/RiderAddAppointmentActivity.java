package com.example.awesomeness.designatedride._RiderActivities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
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
import com.example.awesomeness.designatedride.util.SwitchActivity;
import com.example.awesomeness.designatedride.util.Widgets.TimePickerFragment;
import com.example.awesomeness.designatedride.util.Widgets.DatePickerFragment;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

public class RiderAddAppointmentActivity extends AppCompatActivity  implements
        DatePickerFragment.DatePickedListener, TimePickerFragment.TimePickedListener {
    private static final String TAG = "RiderAddAppt";
    //private final static String metaFile = "appointments_metadata.txt";

    private String time;
    private String date;


    // Widgets
    private EditText appointmentName;
    private EditText destinationName;
    private EditText destinationAddress;
    private EditText destinationAddrLineTwo;
    private EditText notesET;
    private TextView dateDay;
    private TextView dateMonth;
    private TextView dateYear;
    private TextView dateTime;
    private Button confirmButton;
    private Button cancelButton;

    private String fileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_add_appointment);


        String apptName = "";
        String locationName = "";
        String locationAddress = "";
        String locationAddressTwo = ""; //city, state, zip code
        //String time = "";
        //String date = "";
        String status = "";
        String notes = "";

        Intent getDataIntent = getIntent();
        Bundle dataFromIntent = getDataIntent.getExtras();
        if (dataFromIntent != null && !dataFromIntent.isEmpty()) {
            fileName = dataFromIntent.getString(Constants.FILENAME_MESSAGE);

            HandleFileReadWrite reader = new HandleFileReadWrite();
            reader.open(this, fileName);
            if (reader.isExist()) {
                apptName = reader.readLine();
                locationName = reader.readLine();
                locationAddress = reader.readLine();
                locationAddressTwo = reader.readLine();
                time = reader.readLine();
                date = reader.readLine();
                //status = reader.readLine();
                notes = reader.readLine();
                StringBuilder stringBuilder = new StringBuilder();
                while (notes != null) {
                    stringBuilder.append(notes);
                    stringBuilder.append("\n");
                    notes = reader.readLine();
                }
                notes = stringBuilder.toString();
            }


        }

        initWidgets();

        if (!fileName.equals("")) {
            appointmentName.setText(apptName);
            destinationName.setText(locationName);
            destinationAddress.setText(locationAddress);
            destinationAddrLineTwo.setText(locationAddressTwo);
            String[] dateSplit = date.split("-");
            dateDay.setText(dateSplit[0]);
            dateMonth.setText(dateSplit[1]);
            dateYear.setText(dateSplit[2]);
            dateTime.setText(time);
            notesET.setText(notes);
        }

        //time = "";
        //date = "";

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int fileCount = 0;
                String apptName = appointmentName.getText().toString().trim();
                String name = destinationName.getText().toString().trim();
                String address = destinationAddress.getText().toString().trim();
                String addressTwo = destinationAddrLineTwo.getText().toString().trim();

                String notes = notesET.getText().toString().trim();

                if (apptName.equals("")) {
                    Toast.makeText(getApplicationContext(), "Appointment Title", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (name.equals("")) {
                    Toast.makeText(getApplicationContext(), "DestinationName", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (address.equals("")) {
                    Toast.makeText(getApplicationContext(), "DestinationAddress", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (addressTwo.equals("")) {
                    Toast.makeText(getApplicationContext(), "DestinationAddressTwo", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (date.equals("")) {
                    Toast.makeText(getApplicationContext(), "date", Toast.LENGTH_SHORT).show();
                    return;
                }

                System.out.println("HGUEGHIUHGEIUHGIUAEHUIG: '" + time + "'");
                if (time.equals("")) {
                    Toast.makeText(getApplicationContext(), "time", Toast.LENGTH_SHORT).show();
                    return;
                }

                HandleFileReadWrite writer = new HandleFileReadWrite();
                if (fileName.equals("")) {
                    fileName = new SimpleDateFormat("yyyy_dd_MM_HH_mm_ss", Locale.US).format(new Date()) + Constants.FILENAME_POSTFIX;
                    //write to file name to metadata
                    writer.open(RiderAddAppointmentActivity.this, Constants.METAFILE_NAME, HandleFileReadWrite.fileOperator.OPEN_APPEND);
                    writer.writeLine(fileName);
                    writer.close();
                }

                writer.open(RiderAddAppointmentActivity.this, fileName, HandleFileReadWrite.fileOperator.OPEN_WRITE);
                writer.writeLine(apptName);
                writer.writeLine(name);
                writer.writeLine(address);
                writer.writeLine(addressTwo);
                writer.writeLine(time);
                writer.writeLine(date);
                writer.writeLine(notes);
                writer.close();

                SwitchActivity.gotoActivity(RiderAddAppointmentActivity.this, RiderViewAppointmentActivityWrapper.class, true);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SwitchActivity.gotoActivity(RiderAddAppointmentActivity.this, RiderViewAppointmentActivityWrapper.class, true);
            }
        });
    }

  public void showTimePickerDialog(View v) {
      DialogFragment newFragment = new TimePickerFragment();
      newFragment.show(getFragmentManager(), "timePicker");
  }

  public void showDatePickerDialog(View v) {
      DialogFragment newFragment = new DatePickerFragment();
      newFragment.show(getFragmentManager(), "datePicker");
  }

  @Override
  public void onDatePicked(String date) {
      String[] dateSplit = date.split("-");
      dateDay.setText(dateSplit[0]);
      dateMonth.setText(dateSplit[1]);
      dateYear.setText(dateSplit[2]);
      this.date = date;
  }

  @Override
  public void onTimePicked(String time) {
      dateTime.setText(time);
     // timeView.setText(time);
      this.time = time;
  }

    private void initWidgets() {
        appointmentName = findViewById(R.id.apptEditApptName_et);
        destinationName = findViewById(R.id.apptEditLocationName_et);
        destinationAddress = findViewById(R.id.apptEditLocationLine1_et);
        destinationAddrLineTwo = findViewById(R.id.apptEditLocationLine2_et);
        notesET = findViewById(R.id.apptEditNotes_et);

        dateDay = findViewById(R.id.apptEditDateDay_tv);
        dateMonth = findViewById(R.id.apptEditDateMonth_tv);
        dateYear = findViewById(R.id.apptEditDateYear_tv);
        dateTime = findViewById(R.id.apptEditDateTime_tv);

        confirmButton = findViewById(R.id.apptEditSave_btn);
        cancelButton = findViewById(R.id.apptEditCancel_btn);
    }
}
