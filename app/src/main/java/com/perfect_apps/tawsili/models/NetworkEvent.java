package com.perfect_apps.tawsili.models;

/**
 * Created by mostafa_anter on 11/4/16.
 */

public class NetworkEvent {
    private String message;

    public NetworkEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
