package org.fossasia.openevent.app.core.attendee;

import android.content.Context;
import android.content.Intent;

import org.fossasia.openevent.app.core.attendee.qrscan.ScanQRActivity;
import org.fossasia.openevent.app.core.auth.AuthActivity;
import org.fossasia.openevent.app.core.event.about.AboutEventActivity;
import org.fossasia.openevent.app.core.event.chart.ChartActivity;
import org.fossasia.openevent.app.core.event.create.CreateEventActivity;
import org.fossasia.openevent.app.core.main.MainActivity;
import org.fossasia.openevent.app.core.organizer.detail.OrganizerDetailActivity;

import java.util.Arrays;
import java.util.List;

public class ScanningDecider {

    public void openScanQRActivity(Context context, long eventId) {
        Intent intent = new Intent(context, ScanQRActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MainActivity.EVENT_KEY, eventId);
        context.startActivity(intent);
    }

    public static List<Object[]> getActivitiesDataArray() {
        return Arrays.asList(new Object[][]{
            {AuthActivity.class, null, null},
            {ScanQRActivity.class, MainActivity.EVENT_KEY, 1L},
            {MainActivity.class, null, null},
            {ChartActivity.class, null, null},
            {AboutEventActivity.class, AboutEventActivity.EVENT_ID, 1L},
            {OrganizerDetailActivity.class, null, null},
            {CreateEventActivity.class, null, null}
        });
    }
}
