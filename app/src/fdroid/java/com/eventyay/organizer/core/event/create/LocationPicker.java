package com.eventyay.organizer.core.event.create;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

public class LocationPicker {

    private final double DEMO_VALUE = 1;

    public boolean launchPicker(Activity activity) {
        //do nothing
        return false;
    }

    @SuppressLint("RestrictedApi")
    public Location getPlace(Activity activity, Intent data) {
        return new Location(DEMO_VALUE, DEMO_VALUE, null);
    }

    public boolean shouldShowLocationLayout() {
        return true;
    }
}
