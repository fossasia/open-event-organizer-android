package org.fossasia.openevent.app.event.attendees.contract;

import org.fossasia.openevent.app.data.models.Attendee;

import java.util.List;

public interface IAttendeesPresenter {

    void attach(long eventId, IAttendeesView attendeesView);

    void start();

    void detach();

    List<Attendee> getAttendees();

    void loadAttendees(boolean forceReload);

    void toggleAttendeeCheckStatus(Attendee attendee);

}
