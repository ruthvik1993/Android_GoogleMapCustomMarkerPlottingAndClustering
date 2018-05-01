package com.ruthvik.mapclustering.model;

import android.os.Parcel;
import android.os.Parcelable;


public class Location  {

    private String name;
    private double longitude;
    private double latitude;

    private String distance;



    public String getName() {
        return name;
    }

    public String getDistance() {
        return distance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(name);
        dest.writeValue(longitude);
        dest.writeValue(latitude);
        dest.writeValue(distance);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Location{" +
                "name='" + name + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", distance=" + distance +
                '}';
    }
}
