package org.fossasia.openevent.app.data;

import org.fossasia.openevent.app.data.event.Event;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class RxBus implements Bus {
    private static PublishSubject<Event> eventPublisher = PublishSubject.create();

    @Inject
    RxBus() { }

    @Override
    public void pushSelectedEvent(Event event) {
        eventPublisher.onNext(event);
    }

    @Override
    public Observable<Event> getSelectedEvent() {
        return eventPublisher;
    }
}
