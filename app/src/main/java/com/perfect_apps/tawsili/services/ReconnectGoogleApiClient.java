package com.perfect_apps.tawsili.services;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.perfect_apps.tawsili.models.NetworkEvent;
import com.perfect_apps.tawsili.utils.NetworkUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by mostafa_anter on 11/4/16.
 */

public class ReconnectGoogleApiClient extends IntentService {
    // default constructor
    public ReconnectGoogleApiClient() {
        super("BaitkBiedakService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("service", "service started");
        EventBus.getDefault().post(new NetworkEvent("message"));
    }


    static public class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String status = NetworkUtil.getConnectivityStatusString(context);

            // this broadcast will receive signal when mobile connect with internet or call it manually from activity
            if (status.equalsIgnoreCase("Wifi enabled") ||
                    status.equalsIgnoreCase("Mobile data enabled")) {
                Intent sendIntent = new Intent(context, ReconnectGoogleApiClient.class);
                context.startService(sendIntent);
            }

        }
    }
}
