package com.perfect_apps.tawsili.scheduleing_task;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.perfect_apps.tawsili.BuildConfig;
import com.perfect_apps.tawsili.app.AppController;
import com.perfect_apps.tawsili.models.SchedualObject;
import com.perfect_apps.tawsili.parser.JsonParser;
import com.perfect_apps.tawsili.store.TawsiliPrefStore;
import com.perfect_apps.tawsili.utils.Constants;
import com.perfect_apps.tawsili.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by mostafa_anter on 11/15/16.
 */

public class GetUserSchedule extends IntentService{
    private Handler mHandler = new Handler();
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public GetUserSchedule() {
        super("getUserSchedule");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // get user schedule
        executeFuncInAnotherThread();
        // add schedule alarm
        // add schedule to schedule pref



    }

    private void executeFuncInAnotherThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                        getUserSchedule();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void getUserSchedule(){
        String url = BuildConfig.API_BASE_URL + "getuserschedule.php?id="
                + new TawsiliPrefStore(GetUserSchedule.this).getPreferenceValue(Constants.userId);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("getUserSchedule", response.toString());
                // filter schedule to delete
                checkScheduleListToDeleteFromServerAndAddCreateAlarm(JsonParser
                        .parseScheduleList(response));

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("checkOrder", "Error: " + error.getMessage());
                executeFuncInAnotherThread();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void checkScheduleListToDeleteFromServerAndAddCreateAlarm(List<SchedualObject> mList){
        if (mList.size() > 0){
            for (SchedualObject schedualObject : mList) {
                if (schedualObject.getSchedual_type().equalsIgnoreCase("once")){
                    SimpleDateFormat simpleDateFormat =
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {

                        Date date1 = simpleDateFormat.parse(Utils.returnTime());
                        Date date2 = simpleDateFormat.parse(schedualObject.getOrder_start_time());

                        if (!isExpired(date1, date2, schedualObject.getSchedual_id())){
                            // add to pref
                            // create alarm
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public boolean isExpired(Date startDate, Date endDate, String scheduleID) {

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        if (elapsedDays < 0 ||
                elapsedHours < 0 ||
                elapsedMinutes < 0 ||
                elapsedSeconds < 0) {
            // delete schedule
            deleteSchedule(scheduleID);
            return true;
        }else {
            return false;
        }


    }

    private void deleteSchedule(final String id){
        String url = BuildConfig.API_BASE_URL + "deleteschedule.php?id="
                + id;
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("getUserSchedule", response.toString());

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("checkOrder", "Error: " + error.getMessage());
                deleteSchedule(id);
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }
}
