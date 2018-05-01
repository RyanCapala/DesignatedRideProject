package com.example.awesomeness.designatedride._RiderActivities;

import android.app.Fragment;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.awesomeness.designatedride.R;
import com.example.awesomeness.designatedride.util.Constants;
import com.example.awesomeness.designatedride.util.HandleFileReadWrite;
import com.example.awesomeness.designatedride.util.SwitchActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.awesomeness.designatedride.util.Constants.NO_APPOINTMENT_MESSAGE;

public class RiderViewAppointmentActivityWrapper extends AppCompatActivity {

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_view_appointment_wrapper);


        // For Back Arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //
        //

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.appointmentsfloatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent nextActivity = new Intent(getApplicationContext(), RiderAddAppointmentActivity.class);
                startActivity(nextActivity);
                finish();*/
                SwitchActivity.gotoActivity(RiderViewAppointmentActivityWrapper.this, RiderAddAppointmentActivity.class, true);
            }
        });


        //
        Toast.makeText(getApplicationContext(), "Hold on appointment to delete!", Toast.LENGTH_SHORT).show();
        //

        HandleFileReadWrite reader = new HandleFileReadWrite();
        reader.open(this, Constants.METAFILE_NAME, HandleFileReadWrite.fileOperator.OPEN_READ);

        int fileCount = 0;
        String appointments[];

        final List<String> sList = new ArrayList<>();

        if (reader.isExist()) {
            String line = reader.readLine();
            while (line != null) {
                sList.add(line);
                line = reader.readLine();
                fileCount++;
            }
        }
        reader.close();

        String[] fileList = buildString(sList);
        ArrayList<String> arrayFileList = new ArrayList<>(Arrays.asList(fileList));
        adapter = new RiderViewAppointmentItemDetail(this, arrayFileList);

        final ListView apmtListView = (ListView) findViewById(R.id.appointmentsListView);

        apmtListView.setAdapter(adapter);


        apmtListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String fileName = String.valueOf(adapterView.getItemAtPosition(i));
                if (fileName.equals(NO_APPOINTMENT_MESSAGE)) {
                    Intent nextActivity = new Intent(getApplicationContext(), RiderAddAppointmentActivity.class);
                    startActivity(nextActivity);
                    finish();
                } else {
                    Intent nextActivity = new Intent(getApplicationContext(), RiderViewAppointmentActivity.class);
                    nextActivity.putExtra(Constants.FILENAME_MESSAGE, fileName);
                    startActivity(nextActivity);
                }
            }
        });

        apmtListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String fileName = String.valueOf(adapterView.getItemAtPosition(i));
                if (!fileName.equals(NO_APPOINTMENT_MESSAGE)) {
                    for (int k=0; k<sList.size(); k++) {
                        if (sList.get(k).equals(fileName)) {
                            sList.remove(k);

                            File fileToDelete = new File(getApplicationContext().getFilesDir(), fileName);
                            if (fileToDelete.exists()) {
                                fileToDelete.delete();
                            }
                            break;
                        }
                    }
                    HandleFileReadWrite writer = new HandleFileReadWrite();
                    writer.open(RiderViewAppointmentActivityWrapper.this, Constants.METAFILE_NAME, HandleFileReadWrite.fileOperator.OPEN_WRITE);
                    for (String line : sList) {
                        //Log.i("RIDER WRAPPER", line);
                        writer.writeLine(line);
                    }
                    writer.close();

                    adapter.remove(fileName);
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(getApplicationContext(), RiderActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String[] buildString(List<String> sList) {
        String list[] = new String[sList.size()];
        for (int i=0; i<sList.size(); i++) {
            list[i] = sList.get(i);
        }

        return list;
    }
}
