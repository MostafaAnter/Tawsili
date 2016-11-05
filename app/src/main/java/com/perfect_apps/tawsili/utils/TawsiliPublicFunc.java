package com.perfect_apps.tawsili.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.perfect_apps.tawsili.BuildConfig;
import com.perfect_apps.tawsili.app.AppController;
import com.perfect_apps.tawsili.store.TawsiliPrefStore;

import java.text.DecimalFormat;

/**
 * Created by mostafa_anter on 11/4/16.
 */

public class TawsiliPublicFunc {
    private static final String TAG = "TawsiliPublicFunc";

    // get price list from service and save inside shared preference
    public static void getPriceList(final Context mContext) {
        String url = BuildConfig.API_BASE_URL + "pricelist.php";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                new TawsiliPrefStore(mContext).addPreference(Constants.PRICE_LIST, response);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    public static double calculateDistance(double startLat, double startLng,
                                           double endLat, double endLng) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = startLat;
        double lat2 = endLat;
        double lon1 = startLng;
        double lon2 = endLng;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));


        return Radius * c * 1000;//result in meters
    }
}
