package org.fossasia.openevent.app.data.contract;

import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.User;

import java.util.List;

import io.reactivex.Observable;

public interface IEventDataRepository {

    Observable<User> getOrganiser(boolean reload);

    Observable<Event> getEvent(long eventId, boolean reload);

    Observable<List<Attendee>> getAttendees(long eventId, boolean reload);

    Observable<List<Event>> getEvents(boolean reload);

    Observable<Attendee> toggleAttendeeCheckStatus(long eventId, long attendeeId);

}
