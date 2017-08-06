package org.fossasia.openevent.app.module.attendee.checkin;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.common.app.lifecycle.presenter.BaseDetailPresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.module.attendee.checkin.contract.IAttendeeCheckInPresenter;
import org.fossasia.openevent.app.module.attendee.checkin.contract.IAttendeeCheckInView;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.progressiveErroneousResult;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.result;

public class AttendeeCheckInPresenter extends BaseDetailPresenter<Long, IAttendeeCheckInView> implements IAttendeeCheckInPresenter {

    private final IAttendeeRepository attendeeRepository;

    private Attendee attendee;

    @Inject
    public AttendeeCheckInPresenter(IAttendeeRepository attendeeRepository) {
        this.attendeeRepository = attendeeRepository;
    }

    @Override
    public void start() {
        if (getView() == null)
            return;

        attendeeRepository.getAttendee(getId(), false)
            .compose(dispose(getDisposable()))
            .compose(result(getView()))
            .subscribe(attendee -> this.attendee = attendee, Logger::logError);
    }

    @Override
    public void toggleCheckIn() {
        if (getView() == null)
            return;

        attendeeRepository.toggleAttendeeCheckStatus(attendee.getEvent().getId(), getId())
            .compose(dispose(getDisposable()))
            .compose(progressiveErroneousResult(getView()))
            .subscribe(completed -> {
                attendee = completed;
                String status = attendee.isCheckedIn() ? "Checked In" : "Checked Out";
                getView().onSuccess(status);
            }, Logger::logError);
    }

    @VisibleForTesting
    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }

    @VisibleForTesting
    public IAttendeeCheckInView getView() {
        return super.getView();
    }
}
