package com.perfect_apps.tawsili.store;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mostafa_anter on 9/26/16.
 */

public class TawsiliPrefStore {
    private static final String PREFKEY = "TawsiliPreferencesStore";
    private SharedPreferences tawsiliPreferences;

    public TawsiliPrefStore(Context context){
        tawsiliPreferences = context.getSharedPreferences(PREFKEY, Context.MODE_PRIVATE);
    }

    public void clearPreference(){
        SharedPreferences.Editor editor = tawsiliPreferences.edit();
        editor.clear().apply();
    }

    public void addPreference(String key, String value){
        SharedPreferences.Editor editor = tawsiliPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void addPreference(String key, int value){
        SharedPreferences.Editor editor = tawsiliPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void removePreference(String key){
        SharedPreferences.Editor editor = tawsiliPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public String getPreferenceValue(String key){
        return tawsiliPreferences.getString(key, "");
    }

    public int getIntPreferenceValue(String key){
        return tawsiliPreferences.getInt(key, 0);
    }
}
