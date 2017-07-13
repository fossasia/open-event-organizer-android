package org.fossasia.openevent.app.data;

import org.fossasia.openevent.app.data.contract.IBus;
import org.fossasia.openevent.app.data.models.Event;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class Bus implements IBus {
    private static PublishSubject<Event> eventIdPublisher = PublishSubject.create();

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
