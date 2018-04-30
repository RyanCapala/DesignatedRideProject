package com.example.awesomeness.designatedride.util;

public class MonthInterpreter {

    private static String monthsShort[] = {"Jan", "Feb", "Mar", "Apr", "May",
                                "Aug", "Sept", "Oct", "Nov", "Dec"};

    private static String monthsLong[] = {"January", "February", "March", "April", "May",
                                            "June", "July", "August", "September", "October",
                                            "November", "December"};

    public MonthInterpreter()
    {

    }

    public static String shortName(int month) {
        return monthsShort[month];
    }

    public static String shortName(String longName) {
        for (int i=0; i<monthsLong.length; i++) {
            if (longName.equals(monthsLong[i])) {
                return monthsShort[i];
            }
        }
        return "Unknown month: '" + longName + "'.";
    }

    public static String getName(int month) {
        switch (month) {
            case 0:
                return "January";
            case 1:
                return "February";
            case 2:
                return "March";
            case 3:
                return "April";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "August";
            case 8:
                return "September";
            case 9:
                return "October";
            case 10:
                return "November";
            case 11:
                return "December";
            default:
                return "UnknownMonth";
        }
    }

}
