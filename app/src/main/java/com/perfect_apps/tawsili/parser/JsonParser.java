package com.perfect_apps.tawsili.parser;

import com.perfect_apps.tawsili.models.DriverDurationAndDistance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

}
