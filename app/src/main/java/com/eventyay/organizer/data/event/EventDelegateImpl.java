package com.eventyay.organizer.data.event;

import androidx.annotation.NonNull;
import com.eventyay.organizer.utils.DateUtils;
import com.eventyay.organizer.utils.service.DateService;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
        if (event.getState() != null)
            return DateUtils.formatDateWithDefault(
                    DateUtils.FORMAT_MONTH_YEAR, event.getStartsAt());
        return "";
    }

    @Override
    @JsonIgnore
    public long getHeaderId() {
        return Math.abs(getHeader().hashCode());
    }
}
