package com.example.awesomeness.designatedride.util;


public class AppointmentInformation
{
    private String hospitalName;
    private String hospitalAddress;
    private String time;
    private boolean advancedBookingStatus;
    private String notes;

    public AppointmentInformation()
    {
        setHospitalName("");
        setHospitalAddress("");
        setTime("");
        setStatus(false);
        setNotes("");
    }

    public AppointmentInformation(String name, String address, String tim, boolean status, String note)
    {
        setHospitalName(name);
        setHospitalAddress(address);
        setTime(tim);
        setStatus(status);
        setNotes(note);
    }

    public void setHospitalName(String name) {hospitalName = name;}
    public void setHospitalAddress(String address) {hospitalAddress = address;}
    public void setTime(String t) {time = t;}
    public void setStatus(boolean status) {advancedBookingStatus = status;}
    public void setNotes(String n) {notes = n;}

    @Override
    public String toString()
    {
        return hospitalName + "\n"
                + hospitalAddress + "\n"
                + time + "\n"
                + ((advancedBookingStatus) ? "yes" : "no") + "\n"
                + notes + "\n";
    }
}
