package com.perfect_apps.tawsili.models;

import android.os.Parcel;
import android.os.Parcelable;



/**
 * Created by mostafa_anter on 11/4/16.
 */

public class DriverModel implements Parcelable, Comparable<DriverModel> {
    private String language_id;
    private String language;
    private String driver_id;
    private String ssno;
    private String name;
    private String mobile;
    private String email;
    private String birth_date;
    private String nationality;
    private String hire_date;
    private String driving_license_exp;
    private String status;
    private String enable;
    private String img_name;
    private String current_location_lat;
    private String current_location_lng;
    private String un;
    private String car_id;
    private String car_from;
    private String car_by_un;
    private String status_by;
    private String car_type;
    private String model;
    private String category;
    private String category2;
    private String payment_machine;
    private String capacity;
    private String join_date;
    private String udid;
    private String license_plate;
    private String rate;

    private double distanceFromCarToClient;

    public DriverModel(String language_id, String language, String driver_id,
                       String ssno, String name, String mobile, String email,
                       String birth_date, String nationality, String hire_date,
                       String driving_license_exp, String status, String enable,
                       String img_name, String current_location_lat,
                       String current_location_lng, String un, String car_id,
                       String car_from, String car_by_un, String status_by,
                       String car_type, String model, String category, String category2,
                       String payment_machine, String capacity, String join_date,
                       String udid, String license_plate, String rate, double distanceFromCarToClient) {
        this.language_id = language_id;
        this.language = language;
        this.driver_id = driver_id;
        this.ssno = ssno;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.birth_date = birth_date;
        this.nationality = nationality;
        this.hire_date = hire_date;
        this.driving_license_exp = driving_license_exp;
        this.status = status;
        this.enable = enable;
        this.img_name = img_name;
        this.current_location_lat = current_location_lat;
        this.current_location_lng = current_location_lng;
        this.un = un;
        this.car_id = car_id;
        this.car_from = car_from;
        this.car_by_un = car_by_un;
        this.status_by = status_by;
        this.car_type = car_type;
        this.model = model;
        this.category = category;
        this.category2 = category2;
        this.payment_machine = payment_machine;
        this.capacity = capacity;
        this.join_date = join_date;
        this.udid = udid;
        this.license_plate = license_plate;
        this.rate = rate;
        this.distanceFromCarToClient = distanceFromCarToClient;
    }

    public String getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getSsno() {
        return ssno;
    }

    public void setSsno(String ssno) {
        this.ssno = ssno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getHire_date() {
        return hire_date;
    }

    public void setHire_date(String hire_date) {
        this.hire_date = hire_date;
    }

    public String getDriving_license_exp() {
        return driving_license_exp;
    }

    public void setDriving_license_exp(String driving_license_exp) {
        this.driving_license_exp = driving_license_exp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getImg_name() {
        return img_name;
    }

    public void setImg_name(String img_name) {
        this.img_name = img_name;
    }

    public String getCurrent_location_lat() {
        return current_location_lat;
    }

    public void setCurrent_location_lat(String current_location_lat) {
        this.current_location_lat = current_location_lat;
    }

    public String getCurrent_location_lng() {
        return current_location_lng;
    }

    public void setCurrent_location_lng(String current_location_lng) {
        this.current_location_lng = current_location_lng;
    }

    public String getUn() {
        return un;
    }

    public void setUn(String un) {
        this.un = un;
    }

    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }

    public String getCar_from() {
        return car_from;
    }

    public void setCar_from(String car_from) {
        this.car_from = car_from;
    }

    public String getCar_by_un() {
        return car_by_un;
    }

    public void setCar_by_un(String car_by_un) {
        this.car_by_un = car_by_un;
    }

    public String getStatus_by() {
        return status_by;
    }

    public void setStatus_by(String status_by) {
        this.status_by = status_by;
    }

    public String getCar_type() {
        return car_type;
    }

    public void setCar_type(String car_type) {
        this.car_type = car_type;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory2() {
        return category2;
    }

    public void setCategory2(String category2) {
        this.category2 = category2;
    }

    public String getPayment_machine() {
        return payment_machine;
    }

    public void setPayment_machine(String payment_machine) {
        this.payment_machine = payment_machine;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getJoin_date() {
        return join_date;
    }

    public void setJoin_date(String join_date) {
        this.join_date = join_date;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getLicense_plate() {
        return license_plate;
    }

    public void setLicense_plate(String license_plate) {
        this.license_plate = license_plate;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public double getDistanceFromCarToClient() {
        return distanceFromCarToClient;
    }

    public void setDistanceFromCarToClient(double distanceFromCarToClient) {
        this.distanceFromCarToClient = distanceFromCarToClient;
    }

    protected DriverModel(Parcel in) {
        language_id = in.readString();
        language = in.readString();
        driver_id = in.readString();
        ssno = in.readString();
        name = in.readString();
        mobile = in.readString();
        email = in.readString();
        birth_date = in.readString();
        nationality = in.readString();
        hire_date = in.readString();
        driving_license_exp = in.readString();
        status = in.readString();
        enable = in.readString();
        img_name = in.readString();
        current_location_lat = in.readString();
        current_location_lng = in.readString();
        un = in.readString();
        car_id = in.readString();
        car_from = in.readString();
        car_by_un = in.readString();
        status_by = in.readString();
        car_type = in.readString();
        model = in.readString();
        category = in.readString();
        category2 = in.readString();
        payment_machine = in.readString();
        capacity = in.readString();
        join_date = in.readString();
        udid = in.readString();
        license_plate = in.readString();
        rate = in.readString();
        distanceFromCarToClient = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(language_id);
        dest.writeString(language);
        dest.writeString(driver_id);
        dest.writeString(ssno);
        dest.writeString(name);
        dest.writeString(mobile);
        dest.writeString(email);
        dest.writeString(birth_date);
        dest.writeString(nationality);
        dest.writeString(hire_date);
        dest.writeString(driving_license_exp);
        dest.writeString(status);
        dest.writeString(enable);
        dest.writeString(img_name);
        dest.writeString(current_location_lat);
        dest.writeString(current_location_lng);
        dest.writeString(un);
        dest.writeString(car_id);
        dest.writeString(car_from);
        dest.writeString(car_by_un);
        dest.writeString(status_by);
        dest.writeString(car_type);
        dest.writeString(model);
        dest.writeString(category);
        dest.writeString(category2);
        dest.writeString(payment_machine);
        dest.writeString(capacity);
        dest.writeString(join_date);
        dest.writeString(udid);
        dest.writeString(license_plate);
        dest.writeString(rate);
        dest.writeDouble(distanceFromCarToClient);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DriverModel> CREATOR = new Parcelable.Creator<DriverModel>() {
        @Override
        public DriverModel createFromParcel(Parcel in) {
            return new DriverModel(in);
        }

        @Override
        public DriverModel[] newArray(int size) {
            return new DriverModel[size];
        }
    };

    @Override
    public int compareTo(DriverModel o) {
        double distanceFromCarToClient1 = this.distanceFromCarToClient;
        double distanceFromCarToClient2 = o.distanceFromCarToClient;
        if (distanceFromCarToClient1 < distanceFromCarToClient2){
            return -1;
        }else if (distanceFromCarToClient1 == distanceFromCarToClient2){
            return 0;
        }else {
            return 1;
        }

    }
}