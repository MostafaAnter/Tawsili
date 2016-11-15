package com.perfect_apps.tawsili.models;

/**
 * Created by mostafa_anter on 11/15/16.
 */

public class SchedualObject {
    private String schedual_id;
    private String client_id;
    private String schedual_create_time;
    private String order_start_time;
    private String from_location_lat;
    private String from_location_lng;
    private String to_location_lat;
    private String to_location_lng;
    private String from_details;
    private String to_details;
    private String schedual_type;
    private String order_category;
    private String promocode;
    private String discount;

    public SchedualObject(String schedual_id, String client_id, String schedual_create_time, String order_start_time, String from_location_lat, String from_location_lng, String to_location_lat, String to_location_lng, String from_details, String to_details, String schedual_type, String order_category, String promocode, String discount) {
        this.schedual_id = schedual_id;
        this.client_id = client_id;
        this.schedual_create_time = schedual_create_time;
        this.order_start_time = order_start_time;
        this.from_location_lat = from_location_lat;
        this.from_location_lng = from_location_lng;
        this.to_location_lat = to_location_lat;
        this.to_location_lng = to_location_lng;
        this.from_details = from_details;
        this.to_details = to_details;
        this.schedual_type = schedual_type;
        this.order_category = order_category;
        this.promocode = promocode;
        this.discount = discount;
    }

    public String getSchedual_id() {
        return schedual_id;
    }

    public void setSchedual_id(String schedual_id) {
        this.schedual_id = schedual_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getSchedual_create_time() {
        return schedual_create_time;
    }

    public void setSchedual_create_time(String schedual_create_time) {
        this.schedual_create_time = schedual_create_time;
    }

    public String getOrder_start_time() {
        return order_start_time;
    }

    public void setOrder_start_time(String order_start_time) {
        this.order_start_time = order_start_time;
    }

    public String getFrom_location_lat() {
        return from_location_lat;
    }

    public void setFrom_location_lat(String from_location_lat) {
        this.from_location_lat = from_location_lat;
    }

    public String getFrom_location_lng() {
        return from_location_lng;
    }

    public void setFrom_location_lng(String from_location_lng) {
        this.from_location_lng = from_location_lng;
    }

    public String getTo_location_lat() {
        return to_location_lat;
    }

    public void setTo_location_lat(String to_location_lat) {
        this.to_location_lat = to_location_lat;
    }

    public String getTo_location_lng() {
        return to_location_lng;
    }

    public void setTo_location_lng(String to_location_lng) {
        this.to_location_lng = to_location_lng;
    }

    public String getFrom_details() {
        return from_details;
    }

    public void setFrom_details(String from_details) {
        this.from_details = from_details;
    }

    public String getTo_details() {
        return to_details;
    }

    public void setTo_details(String to_details) {
        this.to_details = to_details;
    }

    public String getSchedual_type() {
        return schedual_type;
    }

    public void setSchedual_type(String schedual_type) {
        this.schedual_type = schedual_type;
    }

    public String getOrder_category() {
        return order_category;
    }

    public void setOrder_category(String order_category) {
        this.order_category = order_category;
    }

    public String getPromocode() {
        return promocode;
    }

    public void setPromocode(String promocode) {
        this.promocode = promocode;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
