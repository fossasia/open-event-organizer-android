package org.fossasia.openevent.app.data.repository;

import android.support.annotation.NonNull;

import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.EventStatistics;
import org.fossasia.openevent.app.data.models.User;

import io.reactivex.Observable;

public interface IEventRepository {

    Observable<User> getOrganiser(boolean reload);

    Observable<Event> getEvent(long eventId, boolean reload);

    @NonNull
    Observable<Event> getEvents(boolean reload);

    Observable<Event> updateEvent(Event event);

    Observable<Event> createEvent(Event event);

    Observable<EventStatistics> getEventStatistics(long id);
}
