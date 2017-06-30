package org.fossasia.openevent.app.data.repository.contract;

import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.User;

import io.reactivex.Observable;

public interface IEventRepository {

    Observable<User> getOrganiser(boolean reload);

    Observable<Event> getEvent(long eventId, boolean reload);

    Observable<Event> getEvents(boolean reload);

}
