package org.fossasia.openevent.app.event.attendees.contract;

import org.fossasia.openevent.app.data.models.Attendee;

import java.util.List;

public interface IAttendeesView {

    void showProgressBar(boolean show);

    void onRefreshComplete();

    void showScanButton(boolean show);

    void showAttendees(List<Attendee> attendees);

    void showEmptyView(boolean show);

    void updateAttendee(int position, Attendee attendee);

    void showErrorMessage(String error);

    void showToggleDialog(Attendee attendee);

}
