package com.laserscorpion.impromptu;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    int hours;
    int minutes;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public long getSeconds() {
        long milliseconds = (hours * 60 * 60 * 1000) + (minutes * 60 * 1000);
        /*Log.d("timepicker ", "hour " + hours);
        Log.d("timepicker ", "minutes " + minutes);
        Log.d("timepicker ", "ms " + milliseconds);*/
        return milliseconds;

    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user

        hours = hourOfDay;
        minutes = minute;
    }
}
