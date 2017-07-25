package org.fossasia.openevent.app.common.data.contract;

import org.fossasia.openevent.app.common.data.models.Event;

import io.reactivex.Observable;

public interface IBus {

    void pushSelectedEvent(Event event);

    Observable<Event> getSelectedEvent();

}
