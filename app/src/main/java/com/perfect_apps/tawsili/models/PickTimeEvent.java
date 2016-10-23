package com.perfect_apps.tawsili.models;

/**
 * Created by mostafa_anter on 10/23/16.
 */

public class PickTimeEvent {
    private int hourOfDay;
    private int minute;

    public int getHourOfDay() {
        return hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public PickTimeEvent(int hourOfDay, int minute) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }
}
