package org.fossasia.openevent.app.module.event.dashboard;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

public class EventDashboardViewModel extends ViewModel {
    @Nullable
    private Long eventId;

    @Nullable
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(final long eventId) {
        this.eventId = eventId;
    }
}
