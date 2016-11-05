package com.perfect_apps.tawsili.models;

/**
 * Created by mostafa_anter on 10/23/16.
 */

public class FavoritePlaceItem {

    private String name;
    private String vicinity;
    private String lat;
    private String lng;
    private boolean fav;

    public FavoritePlaceItem(){

    }

    public FavoritePlaceItem(String name, String vicinity, String lat, String lng, boolean fav) {
        this.name = name;
        this.vicinity = vicinity;
        this.lat = lat;
        this.lng = lng;
        this.fav = fav;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }
}
