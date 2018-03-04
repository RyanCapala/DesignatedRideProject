package com.example.awesomeness.designatedride.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UserDataHelper{
    private static final String TAG = "UserDataHelper";
    private static final String userDataFileName = "userInfo";
    private static AccountInfoContainer container;

    public enum  userDataItem{
        EMAIL, PASSWORD, USERTYPE
    }


    public static class AccountInfoContainer{
        public String email;
        public String password;
        public String userType;

        public boolean containsInvalidData(){
            boolean valid = email != null;
            valid &= password != null;
            //valid &= userType != null && (userType == Constants.RIDER | userType == Constants.DRIVER);
            return !valid;
        }
    }

    private UserDataHelper() {
        container = new AccountInfoContainer();
    }

    // Auto-login user
    // Should be "secure enough" for our purposes, at least to make testing less tedious
    // Probably better to use some sort of token or something similar.
    public static void saveUserInfo(Context context, String email, String pass, String userType){
        SharedPreferences sharedPreferences = context.getSharedPreferences(userDataFileName, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("pass", pass);
        editor.putString("userType", userType);
        editor.apply();

        Log.d(TAG, "saveUserInfo: Saved user info as sharedPreference");
    }

    public static String loadUserItem(Context context, userDataItem itemName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(userDataFileName, Context.MODE_PRIVATE);
        String item = null;
        switch (itemName){
            case EMAIL: item = sharedPreferences.getString("email", null); break;
            case PASSWORD: item = sharedPreferences.getString("pass", null); break;
            case USERTYPE: item = sharedPreferences.getString("userType", null); break;
        }
        return item;
    }

    public static AccountInfoContainer loadLocalUser(Context context){
        AccountInfoContainer container = new AccountInfoContainer();
        SharedPreferences sharedPreferences = context.getSharedPreferences(userDataFileName, Context.MODE_PRIVATE);
        container.email = sharedPreferences.getString("email", null);
        container.password = sharedPreferences.getString("pass", null);
        container.userType = sharedPreferences.getString("userType",null);
        return container;
    }

    //Deletes username/password stored locally
    public static void deleteLocalUser(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(userDataFileName,Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
        Log.d(TAG, "deleteLocalUser: DELETED USER DATA");
    }
}