package com.example.awesomeness.designatedride.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        String intentName = intent.getAction();
        if(intentName.equals("ACK")) {
            Intent switchIntent = new Intent(context, SynAck.class);
            context.startActivity(switchIntent);
        }
    }
}
