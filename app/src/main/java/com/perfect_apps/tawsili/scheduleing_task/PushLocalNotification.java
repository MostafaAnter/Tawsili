package com.perfect_apps.tawsili.scheduleing_task;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by mostafa_anter on 11/13/16.
 */

public class PushLocalNotification extends IntentService {

    public PushLocalNotification() {
        super("pushLocalNotification");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Toast.makeText(this, "alarm run now", Toast.LENGTH_SHORT).show();
    }


    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                // Set the alarm here.
                Intent sendIntent = new Intent(context, PushLocalNotification.class);
                context.startService(sendIntent);
            }
        }
    }
}


