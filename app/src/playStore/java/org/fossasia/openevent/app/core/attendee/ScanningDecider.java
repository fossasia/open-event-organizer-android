package org.fossasia.openevent.app.core.attendee;

import android.content.Context;
import android.content.Intent;

import org.fossasia.openevent.app.core.attendee.qrscan.ScanQRActivity;
import org.fossasia.openevent.app.core.main.MainActivity;

public class ScanningDecider {

    public void openScanQRActivity(Context context, long eventId) {
        Intent intent = new Intent(context, ScanQRActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MainActivity.EVENT_KEY, eventId);
        context.startActivity(intent);
    }
}
