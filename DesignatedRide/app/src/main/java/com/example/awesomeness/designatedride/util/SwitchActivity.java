package com.example.awesomeness.designatedride.util;
import android.app.Activity;
import android.content.Intent;

public class SwitchActivity {

    //function to go to another activity
    //1st parameter:    current activity: <Class Name>.this
    //2nd paramater:    activity to switch to: <Class Name>.class
    //3rd parameter:    true = finish() old activity
    public static void gotoActivity(Activity context, Class to, boolean finish) {
        context.startActivity(new Intent(context, to));
        if (finish) {
            context.finish();
        }
    }

}
