package org.fossasia.openevent.app.core.attendee.list;

import org.fossasia.openevent.app.common.mvp.view.Emptiable;
import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.data.attendee.Attendee;

public interface AttendeesView extends Progressive, Refreshable, Erroneous, Emptiable<Attendee> {

    void showScanButton(boolean show);

    void updateAttendee(Attendee attendee);

    void showToggleDialog(long attendeeId);

}
