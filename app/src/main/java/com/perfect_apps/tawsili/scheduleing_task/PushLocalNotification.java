package com.perfect_apps.tawsili.scheduleing_task;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.activities.ScheduleTesultActivity;

/**
 * Created by mostafa_anter on 11/13/16.
 */

public class PushLocalNotification extends IntentService {

    private NotificationManager mNotificationManager;
    private String mScheduleID;
    NotificationCompat.Builder builder;

    public PushLocalNotification() {
        super("pushLocalNotification");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // The reminder message the user set.
        mScheduleID = intent.getStringExtra("ID");
        NotificationManager nm = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        String action = intent.getAction();
        // This section handles the 3 possible actions:
        // ping, snooze, and dismiss.
        if(action.equals("ACTION_PING")){
            issueNotification(intent, mScheduleID);
        } else if (action.equals("createOrder")) {
            nm.cancel(Integer.valueOf(mScheduleID));
            // TODO: 11/16/16 create order 

        } else if (action.equals("cancel")) {
            nm.cancel(Integer.valueOf(mScheduleID));
        }
    }


    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("Tawsili_schedule")) {
                // Set the alarm here.
                Intent sendIntent = new Intent(context, PushLocalNotification.class);
                sendIntent.setAction("ACTION_PING");
                sendIntent.putExtra("ID", intent.getStringExtra("ID"));
                context.startService(sendIntent);
            }
        }
    }

    private void issueNotification(Intent intent, String mScheduleID) {
        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        // Sets up the Snooze and Dismiss action buttons that will appear in the
        // expanded view of the notification.
        Intent dismissIntent = new Intent(this, PushLocalNotification.class);
        dismissIntent.setAction("cancel");
        PendingIntent piDismiss = PendingIntent.getService(this, 0, dismissIntent, 0);

        Intent snoozeIntent = new Intent(this, PushLocalNotification.class);
        snoozeIntent.setAction("createOrder");
        PendingIntent piSnooze = PendingIntent.getService(this, 0, snoozeIntent, 0);

        // Constructs the Builder object.
        builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.car_marker)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.you_have_new_trip))
                        .setDefaults(Notification.DEFAULT_ALL) // requires VIBRATE permission
                /*
                 * Sets the big view "big text" style and supplies the
                 * text (the user's reminder message) that will be displayed
                 * in the detail area of the expanded notification.
                 * These calls are ignored by the support library for
                 * pre-4.1 devices.
                 */
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(getString(R.string.you_have_new_trip)))
                        .addAction (R.drawable.close,
                                getString(R.string.cancel), piDismiss)
                        .addAction (R.drawable.com_facebook_button_like_icon_selected,
                                getString(R.string.let_s_go), piSnooze);

        /*
         * Clicking the notification itself displays ResultActivity, which provides
         * UI for snoozing or dismissing the notification.
         * This is available through either the normal view or big view.
         */
        Intent resultIntent = new Intent(this, ScheduleTesultActivity.class);
        resultIntent.putExtra("ID", mScheduleID);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        builder.setContentIntent(resultPendingIntent);
        startTimer(1000);
    }

    private void issueNotification(NotificationCompat.Builder builder) {
        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // Including the notification ID allows you to update the notification later on.
        mNotificationManager.notify(Integer.valueOf(mScheduleID), builder.build());
    }

    // Starts the timer according to the number of seconds the user specified.
    private void startTimer(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        issueNotification(builder);
    }
}


