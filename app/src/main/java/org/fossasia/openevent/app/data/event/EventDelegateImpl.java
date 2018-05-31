package org.fossasia.openevent.app.data.event;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.fossasia.openevent.app.utils.service.DateService;

import java.text.ParseException;

import timber.log.Timber;
@SuppressWarnings("ComparableType")
public class EventDelegateImpl implements EventDelegate {

    private final Event event;

    public EventDelegateImpl(Event event) {
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
