package org.fossasia.openevent.app.data;

import org.fossasia.openevent.app.data.contract.IBus;
import org.fossasia.openevent.app.data.models.Event;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class Bus implements IBus {
    private static BehaviorSubject<Event> eventIdPublisher = BehaviorSubject.create();

    @Inject
    public Bus() {}

    @Override
    public void pushSelectedEvent(Event event) {
        eventIdPublisher.onNext(event);
    }

    @Override
    public Observable<Event> getSelectedEvent() {
        return eventIdPublisher;
    }
}
