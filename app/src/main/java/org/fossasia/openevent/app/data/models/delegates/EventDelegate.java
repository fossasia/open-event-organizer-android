package org.fossasia.openevent.app.data.models.delegates;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.utils.service.DateService;

import java.text.ParseException;

import timber.log.Timber;

public class EventDelegate implements IEventDelegate {

    private final Event event;

    public EventDelegate(Event event) {
        this.event = event;
    }

    @Override
    public int compareTo(@NonNull Event otherEvent) {
        return DateService.compareEventDates(event, otherEvent);
    }

    @Override
    @JsonIgnore
    public String getHeader() {
        try {
            return DateService.getEventStatus(event);
        } catch (ParseException e) {
            Timber.e(e);
        }

        return "INVALID";
    }

    @Override
    @JsonIgnore
    public long getHeaderId() {
        return getHeader().hashCode();
    }

}
