package com.laserscorpion.impromptu;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {


    Date realDate;
    dateListener listener;

    public void setListener(dateListener callback) {
        listener = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }


    public synchronized void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        realDate = new Date(year, month, day);
        listener.setDateSeconds(realDate.getTime());
    }

    public interface dateListener {
        void setDateSeconds(long seconds);
    }

}
