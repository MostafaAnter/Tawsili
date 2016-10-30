package com.perfect_apps.tawsili.models;

/**
 * Created by mostafa_anter on 10/30/16.
 */

public class ReceiveSMSEvent {
    private String message;

    public ReceiveSMSEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
