package com.eventyay.organizer.data;

import com.eventyay.organizer.data.event.Event;

import io.reactivex.Observable;

public interface Bus {

    void pushSelectedEvent(Event event);

    Observable<Event> getSelectedEvent();
}
