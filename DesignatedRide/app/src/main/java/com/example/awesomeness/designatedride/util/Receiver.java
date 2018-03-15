package com.example.awesomeness.designatedride.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        String intentName = intent.getAction();
        if(intentName.equals("ACK")) {
            Intent switchIntent = new Intent(context, SynAck.class);
            context.startActivity(switchIntent);
        }

        else if(intentName.equals("SYN")){
                String fileName = new SimpleDateFormat("yyyy_dd_MM_HH_mm_ss", Locale.US).format(new Date()) + ".txt";

                HandleFileReadWrite writer = new HandleFileReadWrite();

                writer.open(context, "appointments_metadata.txt", HandleFileReadWrite.fileOperator.OPEN_APPEND);
                writer.writeLine(fileName);
                writer.close();

                writer.open(context, fileName, HandleFileReadWrite.fileOperator.OPEN_WRITE);
                writer.writeLine(intent.getStringExtra("name"));
                writer.writeLine(intent.getStringExtra("address"));
                writer.writeLine(intent.getStringExtra("time"));
                writer.writeLine(intent.getStringExtra("date"));
                writer.writeLine(intent.getStringExtra("status"));
                writer.writeLine(intent.getStringExtra("notes"));
                writer.close();

        }
    }
}
