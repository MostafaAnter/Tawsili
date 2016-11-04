package com.perfect_apps.tawsili.models;

/**
 * Created by mostafa_anter on 11/3/16.
 */

public class DriverDurationAndDistance {
    private String distanceText;
    private String distanceValue;
    private String durationText;
    private String durationValue;

    public DriverDurationAndDistance(String distanceText, String distanceValue, String durationText, String durationValue) {
        this.distanceText = distanceText;
        this.distanceValue = distanceValue;
        this.durationText = durationText;
        this.durationValue = durationValue;
    }

    public String getDistanceText() {
        return distanceText;
    }

    public void setDistanceText(String distanceText) {
        this.distanceText = distanceText;
    }

    public String getDistanceValue() {
        return distanceValue;
    }

    public void setDistanceValue(String distanceValue) {
        this.distanceValue = distanceValue;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public String getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(String durationValue) {
        this.durationValue = durationValue;
    }
}
