package org.fossasia.openevent.app.contract.model;

import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.User;

import java.util.List;

import io.reactivex.Observable;

public interface EventModel {

    Observable<User> getOrganiser(boolean reload);

    Observable<Event> getEvent(long eventId, boolean reload);

    Observable<List<Event>> getEvents(boolean reload);

}
