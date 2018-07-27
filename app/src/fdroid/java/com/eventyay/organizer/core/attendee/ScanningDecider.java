package com.eventyay.organizer.core.attendee;

import android.content.Context;
import android.widget.Toast;

import com.eventyay.organizer.R;

public class ScanningDecider {

    public void openScanQRActivity(Context context, long eventId) {
        Toast.makeText(context, R.string.scanning_disabled_message, Toast.LENGTH_SHORT).show();
    }
}
