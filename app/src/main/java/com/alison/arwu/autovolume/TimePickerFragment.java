package com.alison.arwu.autovolume;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.PriorityQueue;

/**
 * Created by Alison on 2016-12-19.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    PriorityQueue<String> settings = new PriorityQueue<String>();

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        mCallback = (OnPickTimeListener) context;
//    }
//    public interface OnPickTimeListener {
//        public void returnTime(Calendar value, String setting);
//    }
//    OnPickTimeListener mCallback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String setting = getArguments().getString("volume");
        settings.add(setting);
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //Create and return a new instance of TimePickerDialog
        TimePickerDialog tpd = new TimePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                this, hour, minute, DateFormat.is24HourFormat(getActivity()));

        //set a simple text title for TimePickerDialog
//        tpd.setTitle("When do you want the volume "+setting+"?");

        //set a custom title
        TextView tvTitle = new TextView(getActivity());
        tvTitle.setText("When do you want the volume "+setting+"?");
        tvTitle.setBackgroundColor(Color.parseColor("#76D7C4"));
        tvTitle.setPadding(20, 12, 20, 12);
        tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        tpd.setCustomTitle(tvTitle);

        return tpd;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        String currentSetting = settings.remove();
        //Do something with the user chosen time
        //Get reference of host activity (XML Layout File) TextView widget
        TextView tv = null;
        if(currentSetting.equals("down")) {
            tv = (TextView) getActivity().findViewById(R.id.volumeDownText);
        } else if(currentSetting.equals("up")) {
            tv = (TextView) getActivity().findViewById(R.id.volumeUpText);
        }

        //Set a message for user

        //Get the AM or PM for current time
        String amORpm = "AM";
        if(hourOfDay >11)
            amORpm = "PM";

        //Make the 24 hour time format to 12 hour time format
        int currentHour;
        if(hourOfDay>11) {
            currentHour = hourOfDay - 12;
        } else {
            currentHour = hourOfDay;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        if(tv != null) {
            tv.setText("Your chosen time is...\n\n");
            //Display the user changed time on TextView
            tv.setText(tv.getText() + String.valueOf(currentHour)
                    + ":" + String.valueOf(minute) + " " + amORpm + "\n");
            tv.setText(tv.getText()+ cal.getTime().toString());
        }
//        if(mCallback!=null) {
//            mCallback.returnTime(cal, setting);
//        }
        setAlarm(cal, currentSetting);
    }

    //sets an alarm that triggers at daily intervals
    public void setAlarm(Calendar value, String setting){
        Intent intentAlarm = new Intent(getActivity(), AlarmReceiver.class);
        intentAlarm.setAction("com.alison.arwu.autovolume");
        intentAlarm.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intentAlarm.putExtra("volume", setting);
        int rc = setting.equals("down")? MainActivity.downRC : MainActivity.upRC;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), rc, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
//ERROR: when setting both down and up, the latter action will happen (overlap?)
        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC, value.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(getActivity(), "Alarm scheduled with setting "+setting, Toast.LENGTH_LONG).show();
    }
}
