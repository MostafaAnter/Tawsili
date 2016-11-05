package com.perfect_apps.tawsili.store;

import android.content.Context;
import android.content.SharedPreferences;

import com.perfect_apps.tawsili.models.FavoritePlaceItem;

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
public class FavoritePlacesStore {
    private static final String PREFKEY = "favorites";
    private SharedPreferences favoritePrefs;

    public FavoritePlacesStore(Context context) {
        favoritePrefs = context.getSharedPreferences(PREFKEY, Context.MODE_PRIVATE);
    }

    public List<FavoritePlaceItem> findAll() {

        Map<String, ?> notesMap = favoritePrefs.getAll();

        SortedSet<String> keys = new TreeSet<String>(notesMap.keySet());

        List<FavoritePlaceItem> noteList = new ArrayList<>();
        for (String key : keys) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject((String) notesMap.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String name = jsonObject.optString("name");
            String vicinity = jsonObject.optString("vicinity");
            String lat = jsonObject.optString("lat");
            String lng = jsonObject.optString("lng");
            boolean fav = jsonObject.optBoolean("fav");
            
            FavoritePlaceItem note = new FavoritePlaceItem();
            note.setName(name);
            note.setVicinity(vicinity);
            note.setLat(lat);
            note.setLng(lng);
            noteList.add(note);
        }

        return noteList;
    }

    public boolean isFavoritItem(String value, String key){
        return value.equalsIgnoreCase(favoritePrefs.getString(key, ""));
    }

    public boolean addItem(FavoritePlaceItem note) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", note.getName());
            jsonObject.put("vicinity", note.getVicinity());
            jsonObject.put("lat", note.getLat());
            jsonObject.put("lng", note.getLng());
            jsonObject.put("fav", note.isFav());
        } catch (JSONException e) {
            e.printStackTrace();
        }



        SharedPreferences.Editor editor = favoritePrefs.edit();
        editor.putString(note.getLat() + "," + note.getLng(), jsonObject.toString());
        editor.apply();
        return true;
    }

    public boolean removeItem(FavoritePlaceItem note) {

        if (favoritePrefs.contains(note.getLat() + "," + note.getLng())) {
            SharedPreferences.Editor editor = favoritePrefs.edit();
            editor.remove(note.getLat() + "," + note.getLng());
            editor.apply();
        }

        return true;
    }
}
