package com.eventyay.organizer.core.attendee.list;

import com.eventyay.organizer.common.mvp.view.Emptiable;
import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Refreshable;
import com.eventyay.organizer.data.attendee.Attendee;

import java.util.List;

public interface AttendeesView extends Progressive, Refreshable, Erroneous, Emptiable<Attendee> {

    void showScanButton(boolean show);

    void updateAttendee(Attendee attendee);

    void showToggleDialog(long attendeeId);

    List<Attendee> getAttendeeList();
}
