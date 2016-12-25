package com.alison.arwu.autovolume;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements TimePickerFragment.OnPickTimeListener{

    Button volDownBut, volUpBut, cancelBut;
    final int downRC=0, upRC=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //could just add android:onClick in xml
        volDownBut = (Button) findViewById(R.id.volumeDownBut);
        volUpBut = (Button) findViewById(R.id.volumeUpBut);
        cancelBut = (Button) findViewById(R.id.cancelAllBut);

        volDownBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickTime(view, "down");
            }
        });
        volUpBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickTime(view, "up");
            }
        });
        cancelBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlarm(view);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //opens time picker fragment
    private void pickTime(View v, String setting){
        DialogFragment newFragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putString("volume", setting);
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "TimePicker");
    }

    @Override
    public void returnTime(Calendar value, String setting){
        setAlarm(value, setting);
    }

    //sets an alarm that triggers at daily intervals
    public void setAlarm(Calendar value, String setting){
        Intent intentAlarm = new Intent(this, AlarmReceiver.class);
        intentAlarm.setAction("com.alison.arwu.autovolume");
        intentAlarm.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intentAlarm.putExtra("volume", setting);
        int rc = setting.equals("down")? downRC : upRC;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, rc, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC, value.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(this, "Alarm scheduled with setting "+setting, Toast.LENGTH_LONG).show();
    }

    public void cancelAlarm(View view) {
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        i.setAction("com.alison.arwu.autovolume");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, downRC, i, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pendingIntent);
        pendingIntent = PendingIntent.getBroadcast(this, upRC, i, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pendingIntent);
        Toast.makeText(this, "Alarm canceled", Toast.LENGTH_SHORT).show();
    }
}
