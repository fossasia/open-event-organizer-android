package org.fossasia.openevent.app.data.event;

import android.support.annotation.NonNull;

import io.reactivex.Observable;

public interface EventRepository {

    Observable<Event> getEvent(long eventId, boolean reload);

    @NonNull
    Observable<Event> getEvents(boolean reload);

    Observable<Event> updateEvent(Event event);

    Observable<Event> createEvent(Event event);

    Observable<EventStatistics> getEventStatistics(long id);
}
