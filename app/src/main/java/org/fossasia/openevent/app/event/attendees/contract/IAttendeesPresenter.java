package org.fossasia.openevent.app.event.attendees.contract;

import org.fossasia.openevent.app.data.models.Attendee;

import java.util.List;

import io.reactivex.Single;

public interface IAttendeesPresenter {

    void attach(long eventId, IAttendeesView attendeesView);

    void start();

    void detach();

    List<Attendee> getAttendees();

    Single<Attendee> getAttendeeById(long attendeeId);

    void loadAttendees(boolean forceReload);

}
