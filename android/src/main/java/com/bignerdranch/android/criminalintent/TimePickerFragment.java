package com.bignerdranch.android.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimePickerFragment extends DialogFragment {
    public static final String EXTRA_TIME = "com.bignerdranch.android.criminalintent.time";

    private Date mDate;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDate = (Date) getArguments().getSerializable(EXTRA_TIME);

        // create a Calendar to get the hour and minute
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_time, null);

        TimePicker timePicker = (TimePicker) view.findViewById(R.id.dialog_time_timePicker);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                // translate year, month, day into a Date object using a calendar
                mDate.setHours(hourOfDay);
                mDate.setMinutes(minute);

                // update argument to preserve selected value on rotation
                getArguments().putSerializable(EXTRA_TIME, mDate);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }

    public static TimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TIME, date);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);

        return fragment;
    }
}
