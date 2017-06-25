package org.fossasia.openevent.app.event.checkin.contract;

public interface IAttendeeCheckInPresenter {

    void attach(long attendeeId, IAttendeeCheckInView attendeeCheckInView);

    void start();

    void detach();

    void toggleCheckIn();

}
