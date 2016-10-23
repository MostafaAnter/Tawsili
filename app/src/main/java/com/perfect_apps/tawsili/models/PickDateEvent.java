package com.perfect_apps.tawsili.models;

/**
 * Created by mostafa_anter on 10/23/16.
 */

public class PickDateEvent {
    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public PickDateEvent(int year, int month, int day) {

        this.year = year;
        this.month = month;
        this.day = day;
    }

    private  int year;
    private int month;
    private int day;
}
