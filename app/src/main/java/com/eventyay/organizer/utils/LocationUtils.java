package com.eventyay.organizer.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;

import timber.log.Timber;

public class LocationUtils {

    public static List<Address> getAddress(Context context, String location) {

        List<Address> addresses = null;

        if (Geocoder.isPresent()) {

            try {
                Geocoder gc = new Geocoder(context);
                addresses = gc.getFromLocationName(location, 1);
            } catch (IOException excpetion) {
                Timber.e(excpetion);
            }
        }

        return addresses;
    }

    public static double getLatitude(Context context, String location) {

        Address address = getAddress(context, location).get(0);

        return address.getLatitude();
    }

    public static double getLongitude(Context context, String location) {

        Address address = getAddress(context, location).get(0);

        return address.getLongitude();
    }
}
