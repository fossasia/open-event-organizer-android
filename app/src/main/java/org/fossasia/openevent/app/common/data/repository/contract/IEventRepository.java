package org.fossasia.openevent.app.common.data.repository.contract;

import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.User;

import io.reactivex.Observable;

public interface IEventRepository {

    Observable<User> getOrganiser(boolean reload);

    Observable<Event> getEvent(long eventId, boolean reload);

    Observable<Event> getEvents(boolean reload);

}
