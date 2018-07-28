package com.eventyay.organizer.core.event.create;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import timber.log.Timber;

public class LocationPicker {

    private final GoogleApiAvailability googleApiAvailabilityInstance = GoogleApiAvailability.getInstance();
    private static final int PLACE_PICKER_REQUEST = 1;

    public boolean launchPicker(Activity activity) {
        int errorCode = googleApiAvailabilityInstance.isGooglePlayServicesAvailable(activity);

        if (errorCode == ConnectionResult.SUCCESS) {
            //SUCCESS
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                activity.startActivityForResult(builder.build(activity), PLACE_PICKER_REQUEST);
                return true;
            } catch (GooglePlayServicesRepairableException e) {
                Timber.d(e, "GooglePlayServicesRepairable");
            } catch (GooglePlayServicesNotAvailableException e) {
                Timber.d("GooglePlayServices NotAvailable => Updating or Unauthentic");
            }
        }
        return false;
    };

    @SuppressLint("RestrictedApi")
    public Location getPlace(Activity activity, Intent data) {
        Place place = PlacePicker.getPlace(activity, data);
        return new Location(place.getLatLng().latitude, place.getLatLng().longitude, place.getAddress());
    }

    public boolean shouldShowLocationLayout() {
        return false;
    }

}
