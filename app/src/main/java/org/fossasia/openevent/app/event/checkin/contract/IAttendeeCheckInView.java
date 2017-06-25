package org.fossasia.openevent.app.event.checkin.contract;

import org.fossasia.openevent.app.data.models.Attendee;

public interface IAttendeeCheckInView {

    void showAttendee(Attendee attendee);

    void showProgress(boolean show);

    void onSuccess(String message);

    void onError(String message);

    void dismiss();

}
