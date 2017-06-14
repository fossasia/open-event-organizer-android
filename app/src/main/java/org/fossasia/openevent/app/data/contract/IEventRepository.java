package org.fossasia.openevent.app.data.contract;

import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.data.models.User;

import io.reactivex.Observable;

public interface IEventRepository {

    Observable<User> getOrganiser(boolean reload);

    Observable<Event> getEvent(long eventId, boolean reload);

    Observable<Attendee> getAttendees(long eventId, boolean reload);

    Observable<Event> getEvents(boolean reload);

    Observable<Attendee> toggleAttendeeCheckStatus(long eventId, long attendeeId);

}
