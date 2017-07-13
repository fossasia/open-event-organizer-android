package org.fossasia.openevent.app.data.contract;

import org.fossasia.openevent.app.data.models.Event;

import io.reactivex.Observable;

public interface IBus {

    void pushSelectedEvent(Event event);

    Observable<Event> getSelectedEvent();

}
