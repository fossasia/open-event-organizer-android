package org.fossasia.openevent.app.module.attendee.list.contract;

import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Emptiable;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Erroneous;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Progressive;
import org.fossasia.openevent.app.common.app.lifecycle.contract.view.Refreshable;
import org.fossasia.openevent.app.common.data.models.Attendee;

public interface IAttendeesView extends Progressive, Refreshable, Erroneous, Emptiable<Attendee> {

    void showScanButton(boolean show);

    void updateAttendee(Attendee attendee);

    void showToggleDialog(long attendeeId);

}
