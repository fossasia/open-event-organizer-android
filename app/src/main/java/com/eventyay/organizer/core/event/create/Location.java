package com.eventyay.organizer.core.event.create;

public class Location {

    private double latitude;
    private double longitude;
    private CharSequence address;

    public Location(double latitude, double longitude, CharSequence address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public CharSequence getAddress() {
        return address;
    }

}
