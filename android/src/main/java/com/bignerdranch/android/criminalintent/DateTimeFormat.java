package com.bignerdranch.android.criminalintent;

import android.content.Context;

import java.util.Date;

public class DateTimeFormat {

    public DateTimeFormat() {
    }

    public static String getDateString(Context context, Date date) {
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        return  dateFormat.format(date);
    }

    public static String getTimeString(Context context, Date date) {
        java.text.DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
        return timeFormat.format(date);
    }

    public static String getDateTimeString(Context context, Date date) {
        return getDateString(context, date) + " " + getTimeString(context, date);
    }

    public static Boolean is24HourFormat(Context context) {
        return android.text.format.DateFormat.is24HourFormat(context);
    }
}
