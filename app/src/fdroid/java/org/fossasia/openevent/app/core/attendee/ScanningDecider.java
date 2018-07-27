package org.fossasia.openevent.app.core.attendee;

import android.content.Context;
import android.widget.Toast;

import org.fossasia.openevent.app.R;
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
        Toast.makeText(context, R.string.scanning_disabled_message, Toast.LENGTH_SHORT).show();
    }

    public static List<Object[]> getActivitiesDataArray() {
        return Arrays.asList(new Object[][]{
            {AuthActivity.class, null, null},
            {MainActivity.class, null, null},
            {ChartActivity.class, null, null},
            {AboutEventActivity.class, AboutEventActivity.EVENT_ID, 1L},
            {OrganizerDetailActivity.class, null, null},
            {CreateEventActivity.class, null, null}
        });
    }
}

