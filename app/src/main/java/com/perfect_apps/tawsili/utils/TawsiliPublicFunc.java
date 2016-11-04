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
}
