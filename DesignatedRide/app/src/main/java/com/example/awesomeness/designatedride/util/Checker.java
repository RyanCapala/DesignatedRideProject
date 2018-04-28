package com.example.awesomeness.designatedride.util;

import android.widget.EditText;

import java.util.regex.Pattern;

/**
 * Created by awesome on 2/25/18.
 */

public class Checker {

    //----------------------------------------------------------------------------------------------


    //----------------------------------------------------------------------------------------------
    public boolean fieldChecking(String fname, String lname, String em, String pwd, String vpwd, EditText firstName, EditText lastName, EditText userEmailET, EditText userPwd, EditText verifyPwd){

        boolean flag = true;

        if(fname.isEmpty()) {
            firstName.setError("First name is required");
            flag = false;
        }
        if(lname.isEmpty()) {
            lastName.setError("Last name is required");
            flag = false;
        }
        if(em.isEmpty()) {
            userEmailET.setError("Email address is required"); flag = false;
        }
        if(pwd.isEmpty()) {
            pwdMessage(true,false, userPwd, verifyPwd);
            flag = false;
        }
        if(vpwd.isEmpty()) {
            pwdMessage(false,true, userPwd, verifyPwd);
            flag = false;
        }

        return flag;
    }

    //----------------------------------------------------------------------------------------------
    public boolean fieldCheckingNoPwd(String fname, String lname, String em, EditText firstName, EditText lastName, EditText userEmailET){

        boolean flag = true;

        if(fname.isEmpty()) {
            firstName.setError("First name is required");
            flag = false;
        }
        if(lname.isEmpty()) {
            lastName.setError("Last name is required");
            flag = false;
        }
        if(em.isEmpty()) {
            userEmailET.setError("Email address is required"); flag = false;
        }

        return flag;
    }

    //----------------------------------------------------------------------------------------------
    public boolean pwdFieldChecking(String pwd, String vpwd, EditText userPwd, EditText verifyPwd){
        boolean flag = true;

        if(pwd.isEmpty()) {
            pwdMessage(true,false, userPwd, verifyPwd);
            flag = false;
        }
        if(vpwd.isEmpty()) {
            pwdMessage(false,true, userPwd, verifyPwd);
            flag = false;
        }

        return flag;
    }


    //----------------------------------------------------------------------------------------------
    public void pwdMessage(boolean userpwd, boolean vpwd, EditText userPwd, EditText verifyPwd) {

        if(userpwd) {
            userPwd.setError(Constants.ERROR_MSG);
        }
        if(vpwd) {
            verifyPwd.setError(Constants.ERROR_MSG);
        }

    }

    //----------------------------------------------------------------------------------------------
    public boolean pwdmatch(String pwd, String vpwd, EditText userPwd, EditText verifyPwd){
        if(!pwd.equals(vpwd)) {
            userPwd.setError(Constants.ERR_PWD_NOTMATCH);
            verifyPwd.setError(Constants.ERR_PWD_NOTMATCH);
            return false;
        }
        else
            return true;
    }

    //----------------------------------------------------------------------------------------------
    public boolean checkName(String fname, String lname, EditText firstName, EditText lastName){
        boolean flag = true;
        if(fname.matches("(.*[0-9].*)|(.*[@#$%^&+=.{}(),\"].*)|(.*[\\s].*)")){
            firstName.setError(Constants.ERR_NAME_SPECIALCHAR);
            flag = false;
        }
        if(lname.matches("(.*[0-9].*)|(.*[@#$%^&+={}(),\"].*)|(.*[\\s].*)")) {
            lastName.setError(Constants.ERR_NAME_SPECIALCHAR);
            flag = false;
        }

        return flag;
    }

    //----------------------------------------------------------------------------------------------
    public boolean checkPwd(String pwd){
        return (pwd.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{3,}") && pwd.length() >= 8);
    }

    //----------------------------------------------------------------------------------------------
    public boolean compareString(String s1, String s2) {
        if (s1.equals(s2)) {
            return true;
        } else {
            return false;
        }

    }

    public boolean compareInt(int s1, int s2) {
        if (s1 == s2) {
            return true;
        } else {
            return false;
        }
    }

    //----------------------------------------------------------------------------------------------
    public boolean validateEmail(String email) {
        Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" + "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
        );

        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();

    }



}
