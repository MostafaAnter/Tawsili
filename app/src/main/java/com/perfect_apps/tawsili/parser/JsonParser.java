package com.perfect_apps.tawsili.parser;

import android.util.Log;

import com.perfect_apps.tawsili.models.DriverDurationAndDistance;
import com.perfect_apps.tawsili.models.DriverModel;
import com.perfect_apps.tawsili.models.FavoritePlaceItem;
import com.perfect_apps.tawsili.utils.TawsiliPublicFunc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by mostafa_anter on 11/4/16.
 */

public class JsonParser {


    public static List<DriverDurationAndDistance> parseDistanceAndDuration(String content) {

        try {
            JSONObject jsonRootObject = new JSONObject(content);//done
            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonRowsArray = jsonRootObject.optJSONArray("rows");
            List<DriverDurationAndDistance> driverDurationAndDistanceList = new ArrayList<>();

            for (int i = 0; i < jsonRowsArray.length(); i++) {

                JSONObject obj = jsonRowsArray.getJSONObject(i);
                //Get the instance of JSONArray that contains JSONObjects
                JSONArray jsonElementsArray = obj.optJSONArray("elements");
                for (int j = 0; j < jsonElementsArray.length(); j++) {
                    JSONObject obj1 = jsonElementsArray.getJSONObject(j);
                    JSONObject obj11 = obj1.optJSONObject("distance");
                    JSONObject obj12 = obj1.optJSONObject("duration");
                    DriverDurationAndDistance driverDurationAndDistance =
                            new DriverDurationAndDistance(obj11.optString("text"),
                                    obj11.optString("value"), obj12.optString("text"),
                                    obj12.optString("value"));

                    driverDurationAndDistanceList.add(driverDurationAndDistance);

                }

            }

            return driverDurationAndDistanceList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static DriverModel parseDriversList(String userLat, String userLng, String feed){
        JSONArray driversArray = null;
        try {
            driversArray = new JSONArray(feed);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TreeSet<DriverModel> driverModelTreeSet = new TreeSet<>();
        if (driversArray != null) {
            for (int i = 0; i < driversArray.length(); i++) {
                JSONObject driverObject = null;
                try {
                    driverObject = driversArray.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (driverObject != null) {
                    String language_id = driverObject.optString("language_id");
                    String language = driverObject.optString("language");
                    String driver_id = driverObject.optString("driver_id");
                    String ssno = driverObject.optString("ssno");
                    String name = driverObject.optString("name");
                    String mobile = driverObject.optString("mobile");
                    String email = driverObject.optString("email");
                    String birth_date = driverObject.optString("birth_date");
                    String nationality = driverObject.optString("nationality");
                    String hire_date = driverObject.optString("hire_date");
                    String driving_license_exp = driverObject.optString("driving_license_exp");
                    String status = driverObject.optString("status");
                    String enable = driverObject.optString("enable");
                    String img_name = driverObject.optString("img_name");
                    String current_location_lat = driverObject.optString("current_location_lat");
                    String current_location_lng = driverObject.optString("current_location_lng");
                    String un = driverObject.optString("un");
                    String car_id = driverObject.optString("car_id");
                    String car_from = driverObject.optString("car_from");
                    String car_by_un = driverObject.optString("car_by_un");
                    String status_by = driverObject.optString("status_by");
                    String car_type = driverObject.optString("car_type");
                    String model = driverObject.optString("model");
                    String category = driverObject.optString("category");
                    String category2 = driverObject.optString("category2");
                    String payment_machine = driverObject.optString("payment_machine");
                    String capacity = driverObject.optString("capacity");
                    String join_date = driverObject.optString("join_date");
                    String udid = driverObject.optString("udid");
                    String license_plate = driverObject.optString("license_plate");
                    String rate = driverObject.optString("rate");
                    double distanceFromCarToClient;
                    if (!current_location_lat.trim().isEmpty() && !current_location_lng.trim().isEmpty()) {
                        distanceFromCarToClient = TawsiliPublicFunc.calculateDistance(Double.valueOf(userLat),
                                Double.valueOf(userLng), Double.valueOf(current_location_lat),
                                Double.valueOf(current_location_lng));
                    } else {
                        distanceFromCarToClient = 0;
                    }
                    DriverModel mDriverModel = new DriverModel(language_id, language,
                            driver_id, ssno, name, mobile, email, birth_date, nationality,
                            hire_date, driving_license_exp, status, enable, img_name, current_location_lat,
                            current_location_lng, un, car_id, car_from, car_by_un ,status_by, car_type,
                            model,category,category2, payment_machine, capacity, join_date, udid,
                            license_plate,rate, distanceFromCarToClient);
                    if (distanceFromCarToClient != 0)
                    driverModelTreeSet.add(mDriverModel);

                }

            }
            Log.d("treeSet", driverModelTreeSet.toString());
            if (driverModelTreeSet.size() > 0) {
                return driverModelTreeSet.first();
            }
        }

        return null;
    }

    public static List<FavoritePlaceItem> parseFavoritePlaces(String response){
        JSONObject rootObject = null;
        try {
            rootObject = new JSONObject(response);
            JSONArray jsonArray = rootObject.optJSONArray("results");
            List<FavoritePlaceItem> favoritePlaceItems = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject placeObject = jsonArray.optJSONObject(i);
                JSONObject geometryObject = placeObject.optJSONObject("geometry");
                JSONObject locationObject = geometryObject.optJSONObject("location");
                String lat = locationObject.optString("lat");
                String lng = locationObject.optString("lng");
                String name = placeObject.optString("name");
                String vicinity = placeObject.optString("vicinity");

                favoritePlaceItems.add(new FavoritePlaceItem(name, vicinity, lat, lng, false));

            }
            return favoritePlaceItems;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}