package com.alison.arwu.autovolume;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Alison on 2016-12-19.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (OnPickTimeListener) context;
    }
    public interface OnPickTimeListener{
        public void returnTime(Calendar value);
    }
    OnPickTimeListener mCallback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //Create and return a new instance of TimePickerDialog
        TimePickerDialog tpd = new TimePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                this, hour, minute, DateFormat.is24HourFormat(getActivity()));

        //You can set a simple text title for TimePickerDialog
        //tpd.setTitle("Title Of Time Picker Dialog");

        /*.........Set a custom title for picker........*/
        TextView tvTitle = new TextView(getActivity());
        tvTitle.setText("When do you want to change the volume?");
        tvTitle.setBackgroundColor(Color.parseColor("#76D7C4"));
        tvTitle.setPadding(20, 12, 20, 12);
        tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        tpd.setCustomTitle(tvTitle);
        /*.........End custom title section........*/

        return tpd;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        //Do something with the user chosen time
        //Get reference of host activity (XML Layout File) TextView widget
        TextView tv = (TextView) getActivity().findViewById(R.id.displayText);
        //Set a message for user

        //Get the AM or PM for current time
        String amORpm = "AM";
        if(hourOfDay >11)
            amORpm = "PM";

        //Make the 24 hour time format to 12 hour time format
        int currentHour;
        if(hourOfDay>11)
            currentHour = hourOfDay - 12;
        else
            currentHour = hourOfDay;

        tv.setText("Your chosen time is...\n\n");
        //Display the user changed time on TextView
        tv.setText(tv.getText()+ String.valueOf(currentHour)
                + ":" + String.valueOf(minute) + " " + amORpm + "\n");

        if(mCallback!=null)
        {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, minute);

            tv.setText(tv.getText()+ cal.getTime().toString());

            mCallback.returnTime(cal);
        }
    }
}
