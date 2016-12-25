package com.alison.arwu.autovolume;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Alison on 2016-12-19.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
//        Toast.makeText(context, action, Toast.LENGTH_LONG).show();
//        Toast.makeText(context, "Alarm received!", Toast.LENGTH_LONG).show();
        // here you can start an activity or service depending on your need
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Bundle extras = intent.getExtras();
        String setting = null;
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        if(extras != null){
            setting = (String) extras.get("volume");
        }
        Toast.makeText(context, setting, Toast.LENGTH_LONG).show();
        if(setting != null) {
            if (setting.equals("down")) {
                volume = 1;
            } else if (setting.equals("up")) {
                volume = 9;
            }
        }

        audioManager.setStreamVolume(AudioManager.STREAM_RING, volume, AudioManager.FLAG_SHOW_UI);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volume, AudioManager.FLAG_SHOW_UI);
//        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 1, AudioManager.FLAG_SHOW_UI);
    }
}
