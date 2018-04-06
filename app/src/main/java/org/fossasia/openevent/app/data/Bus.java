package org.fossasia.openevent.app.data;

import org.fossasia.openevent.app.data.event.Event;

import io.reactivex.Observable;

public interface Bus {

    void pushSelectedEvent(Event event);

    Observable<Event> getSelectedEvent();

}
