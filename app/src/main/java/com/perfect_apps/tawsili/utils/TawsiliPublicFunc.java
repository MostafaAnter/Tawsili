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
import com.perfect_apps.tawsili.models.FavoritePlaceItem;
import com.perfect_apps.tawsili.store.FavoritePlacesStore;
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

    public static String createMatrixUri(String userLat, String userLng, String driverLat, String driverLng){

        // currentLocation coordinates
        double lastLatitude = Double.valueOf(userLat);
        double lastLongitude = Double.valueOf(userLng);
        double targetLatitude = Double.valueOf(driverLat);
        double targetLongitude = Double.valueOf(driverLng);
        return "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" +
                lastLatitude + "," + lastLongitude + "&destinations=" + targetLatitude + "," +
                targetLongitude + "&mode=driving&language=en-EN&key="
                + "AIzaSyAiT5rjXpzq0N7-ibpjq-QW5_pDfFPElRw";

    }

    public static void addItem(Context mContext, FavoritePlaceItem favoritePlaceItem) {
        //add item to favorite
        new FavoritePlacesStore(mContext).addItem(favoritePlaceItem);
    }

    public static void removeItem(Context mContext, FavoritePlaceItem favoritePlaceItem) {
        //add item to favorite
        new FavoritePlacesStore(mContext).removeItem(favoritePlaceItem);
    }

    public static int calculateFairEstimate(String category, int disInKilo, boolean isNow){
        float startCost = 0;
        float runningCost = 0;
        float minimumCost = 0;
        switch (category){
            case "1":
                if (isNow){
                    startCost = Constants.startCostOfEconomyNow;
                    runningCost = Constants.runningCostOfEconomy;
                    minimumCost = Constants.minimumCostOfEconomyNow;
                }else {
                    startCost = Constants.startCostOfEconomyLater;
                    runningCost = Constants.runningCostOfEconomy;
                    minimumCost = Constants.minimumCostOfEconomyLater;
                }
                break;
            case "2":
                if (isNow){
                    startCost = Constants.startCostOfBusinessNow;
                    runningCost = Constants.runningCostOfBusiness;
                    minimumCost = Constants.minimumCostOfBusinessNow;
                }else {
                    startCost = Constants.startCostOfBusinessLater;
                    runningCost = Constants.runningCostOfBusiness;
                    minimumCost = Constants.minimumCostOfBusinessLater;
                }
                break;
            case "3":
                if (isNow){
                    startCost = Constants.startCostOfVIPNow;
                    runningCost = Constants.runningCostOfVIP;
                    minimumCost = Constants.minimumCostOfVIPNow;
                }else {
                    startCost = Constants.startCostOfVIPLater;
                    runningCost = Constants.runningCostOfVIP;
                    minimumCost = Constants.minimumCostOfVIPLater;
                }
                break;
            case "4":
                if (isNow){
                    startCost = Constants.startCostOfFamilitRegularNow;
                    runningCost = Constants.runningCostOfFamilitRegular;
                    minimumCost = Constants.minimumCostOfFamilitRegularNow;
                }else {
                    startCost = Constants.startCostOfFamilitRegularLater;
                    runningCost = Constants.runningCostOfFamilitRegular;
                    minimumCost = Constants.minimumCostOfFamilitRegularLater;
                }
                break;
            case "5":
                if (isNow){
                    startCost = Constants.startCostOfFamilitSpecialNow;
                    runningCost = Constants.runningCostOfFamilitSpecial;
                    minimumCost = Constants.minimumCostOfFamilitSpecialNow;
                }else {
                    startCost = Constants.startCostOfFamilitSpecialLater;
                    runningCost = Constants.runningCostOfFamilitSpecial;
                    minimumCost = Constants.minimumCostOfFamilitSpecialLater;
                }
                break;
        }

        float estimateFare = startCost + (disInKilo * runningCost);

        return estimateFare > minimumCost? Math.round(estimateFare) : Math.round(minimumCost);
    }


}
