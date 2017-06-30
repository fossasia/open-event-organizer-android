package org.fossasia.openevent.app.event.checkin;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.event.checkin.contract.IAttendeeCheckInPresenter;
import org.fossasia.openevent.app.event.checkin.contract.IAttendeeCheckInView;

import javax.inject.Inject;

public class AttendeeCheckInPresenter implements IAttendeeCheckInPresenter {

    private long attendeeId;
    private IAttendeeCheckInView attendeeCheckInView;
    private IAttendeeRepository attendeeRepository;

    private Attendee attendee;

    @Inject
    public AttendeeCheckInPresenter(IAttendeeRepository attendeeRepository) {
        this.attendeeRepository = attendeeRepository;
    }

    @Override
    public void attach(long attendeeId, IAttendeeCheckInView attendeeCheckInView) {
        this.attendeeId = attendeeId;
        this.attendeeCheckInView = attendeeCheckInView;
    }

    @Override
    public void start() {
        loadAttendee();
    }

    @Override
    public void detach() {
        attendeeCheckInView = null;
    }

    private void loadAttendee() {
        attendeeRepository.getAttendee(attendeeId, false)
            .subscribe(attendee -> {
                this.attendee = attendee;
                if (attendeeCheckInView != null)
                    attendeeCheckInView.showAttendee(attendee);
            }, Throwable::printStackTrace);
    }

    @Override
    public void toggleCheckIn() {
        if (attendeeCheckInView == null)
            return;

        attendeeCheckInView.showProgress(true);

        attendeeRepository.toggleAttendeeCheckStatus(attendee.getEventId(), attendeeId)
            .subscribe(completed -> {
                if (attendeeCheckInView == null)
                    return;

                attendee = completed;

                String status = attendee.isCheckedIn() ? "Checked In" : "Checked Out";

                attendeeCheckInView.showAttendee(attendee);
                attendeeCheckInView.onSuccess(status);
                attendeeCheckInView.showProgress(false);
                //attendeeCheckInView.dismiss();
            }, throwable -> {
                throwable.printStackTrace();
                attendeeCheckInView.onError(throwable.getMessage());
                attendeeCheckInView.showProgress(false);
                //attendeeCheckInView.dismiss();
            });
    }

    @VisibleForTesting
    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }

    @VisibleForTesting
    public IAttendeeCheckInView getView() {
        return attendeeCheckInView;
    }
}
