package com.perfect_apps.tawsili.store;

import android.content.Context;
import android.content.SharedPreferences;

import com.perfect_apps.tawsili.models.FavoritePlaceItem;
import com.perfect_apps.tawsili.models.SchedualObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by mostafa on 22/03/16.
 */
public class SceduleStore {
    private static final String PREFKEY = "scedule";
    private SharedPreferences schedulePrefs;

    public SceduleStore(Context context) {
        schedulePrefs = context.getSharedPreferences(PREFKEY, Context.MODE_PRIVATE);
    }

    public List<SchedualObject> findAll() {

        Map<String, ?> notesMap = schedulePrefs.getAll();

        SortedSet<String> keys = new TreeSet<String>(notesMap.keySet());

        List<SchedualObject> noteList = new ArrayList<>();
        for (String key : keys) {
            JSONObject scheduleObject = null;
            try {
                scheduleObject = new JSONObject((String) notesMap.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String schedual_id = scheduleObject.optString("schedual_id");
            String client_id = scheduleObject.optString("client_id");
            String schedual_create_time = scheduleObject.optString("schedual_create_time");
            String order_start_time = scheduleObject.optString("order_start_time");
            String from_location_lat = scheduleObject.optString("from_location_lat");
            String from_location_lng = scheduleObject.optString("from_location_lng");
            String to_location_lat = scheduleObject.optString("to_location_lat");
            String to_location_lng = scheduleObject.optString("to_location_lng");
            String from_details = scheduleObject.optString("from_details");
            String to_details = scheduleObject.optString("to_details");
            String schedual_type = scheduleObject.optString("schedual_type");
            String order_category = scheduleObject.optString("order_category");
            String promocode = scheduleObject.optString("promocode");
            String discount = scheduleObject.optString("discount");
            noteList.add(new SchedualObject(schedual_id,
                    client_id, schedual_create_time, order_start_time,
                    from_location_lat, from_location_lng, to_location_lat, to_location_lng,
                    from_details, to_details, schedual_type, order_category, promocode,
                    discount));
        }

        return noteList;
    }

    public SchedualObject findItem(String key) {
        JSONObject scheduleObject = null;
        String jsonString = schedulePrefs.getString(key, "");
        if (!jsonString.equalsIgnoreCase(""))
            try {
                scheduleObject = new JSONObject(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        if (scheduleObject != null) {
            String schedual_id = scheduleObject.optString("schedual_id");
            String client_id = scheduleObject.optString("client_id");
            String schedual_create_time = scheduleObject.optString("schedual_create_time");
            String order_start_time = scheduleObject.optString("order_start_time");
            String from_location_lat = scheduleObject.optString("from_location_lat");
            String from_location_lng = scheduleObject.optString("from_location_lng");
            String to_location_lat = scheduleObject.optString("to_location_lat");
            String to_location_lng = scheduleObject.optString("to_location_lng");
            String from_details = scheduleObject.optString("from_details");
            String to_details = scheduleObject.optString("to_details");
            String schedual_type = scheduleObject.optString("schedual_type");
            String order_category = scheduleObject.optString("order_category");
            String promocode = scheduleObject.optString("promocode");
            String discount = scheduleObject.optString("discount");
            return new SchedualObject(schedual_id,
                    client_id, schedual_create_time, order_start_time,
                    from_location_lat, from_location_lng, to_location_lat, to_location_lng,
                    from_details, to_details, schedual_type, order_category, promocode,
                    discount);
        }

        return null;


    }

    public boolean isScheduleItem(String key) {
        return !schedulePrefs.getString(key, "").isEmpty();
    }

    public boolean addItem(SchedualObject note) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("schedual_id", note.getSchedual_id());
            jsonObject.put("client_id", note.getClient_id());
            jsonObject.put("schedual_create_time", note.getSchedual_create_time());
            jsonObject.put("order_start_time", note.getOrder_start_time());
            jsonObject.put("from_location_lat", note.getFrom_location_lat());
            jsonObject.put("from_location_lng", note.getFrom_location_lng());
            jsonObject.put("to_location_lat", note.getTo_location_lat());
            jsonObject.put("to_location_lng", note.getTo_location_lng());
            jsonObject.put("from_details", note.getFrom_details());
            jsonObject.put("to_details", note.getTo_details());
            jsonObject.put("schedual_type", note.getSchedual_type());
            jsonObject.put("order_category", note.getOrder_category());
            jsonObject.put("promocode", note.getPromocode());
            jsonObject.put("discount", note.getDiscount());
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }


        SharedPreferences.Editor editor = schedulePrefs.edit();
        editor.putString(note.getSchedual_id(), jsonObject.toString());
        editor.apply();
        return true;
    }

    public boolean removeItem(SchedualObject note) {

        if (schedulePrefs.contains(note.getSchedual_id())) {
            SharedPreferences.Editor editor = schedulePrefs.edit();
            editor.remove(note.getSchedual_id());
            editor.apply();
        }

        return true;
    }

    public void clearPreference() {
        SharedPreferences.Editor editor = schedulePrefs.edit();
        editor.clear().apply();
    }

}
