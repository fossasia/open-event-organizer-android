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

public class GooglePlacePicker {

    private final GoogleApiAvailability googleApiAvailabilityInstance = GoogleApiAvailability.getInstance();
    private static final int PLACE_PICKER_REQUEST = 1;
    private Place place;
    private boolean showLocation = false;

    public void onSelectingPlace(Activity activity) {
        int errorCode = googleApiAvailabilityInstance.isGooglePlayServicesAvailable(activity);

        if (errorCode == ConnectionResult.SUCCESS) {
            //SUCCESS
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                activity.startActivityForResult(builder.build(activity), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                Timber.d(e, "GooglePlayServicesRepairable");
            } catch (GooglePlayServicesNotAvailableException e) {
                Timber.d("GooglePlayServices NotAvailable => Updating or Unauthentic");
            }
        } else if (googleApiAvailabilityInstance.isUserResolvableError(errorCode)) {
            //SERVICE_MISSING, SERVICE_VERSION_UPDATE_REQUIRED, SERVICE_DISABLED
            googleApiAvailabilityInstance.getErrorDialog(activity, errorCode, PLACE_PICKER_REQUEST, (dialog) -> {
                showLocation = true;
            });
        } else {
            //SERVICE_UPDATING, SERVICE_INVALID - can't use place picker - must enter manually
            showLocation = true;
        }
    };

    @SuppressLint("RestrictedApi")
    public void loadPlaces(Activity activity, Intent data) {
        place = PlacePicker.getPlace(activity, data);
    }

    public boolean shouldShowLocationLayout() {
        return showLocation;
    }

    public double getLatitude() {
        return place.getLatLng().latitude;
    }

    public double getLongitude() {
        return place.getLatLng().longitude;
    }

    public CharSequence getAddress() {
        return place.getAddress();
    }
}
