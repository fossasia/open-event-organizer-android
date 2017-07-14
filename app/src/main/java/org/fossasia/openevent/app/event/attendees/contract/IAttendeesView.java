package org.fossasia.openevent.app.event.attendees.contract;

import org.fossasia.openevent.app.common.contract.view.Emptiable;
import org.fossasia.openevent.app.common.contract.view.Erroneous;
import org.fossasia.openevent.app.common.contract.view.Progressive;
import org.fossasia.openevent.app.common.contract.view.Refreshable;
import org.fossasia.openevent.app.data.models.Attendee;

public interface IAttendeesView extends Progressive, Refreshable, Erroneous, Emptiable<Attendee> {

    void showScanButton(boolean show);

    void updateAttendee(Attendee attendee);

    void showToggleDialog(long attendeeId);

}
