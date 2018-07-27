package com.eventyay.organizer.core.attendee.checkin;

import android.support.annotation.VisibleForTesting;

import com.eventyay.organizer.common.mvp.presenter.AbstractDetailPresenter;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.db.DbFlowDatabaseChangeListener;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.attendee.AttendeeRepository;

import javax.inject.Inject;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;
import static com.eventyay.organizer.common.rx.ViewTransformers.disposeCompletable;
import static com.eventyay.organizer.common.rx.ViewTransformers.erroneousCompletable;
import static com.eventyay.organizer.common.rx.ViewTransformers.erroneousResult;

public class AttendeeCheckInPresenter extends AbstractDetailPresenter<Long, AttendeeCheckInView> {

    private final AttendeeRepository attendeeRepository;
    private final DatabaseChangeListener<Attendee> databaseChangeListener;

    private Attendee attendee;

    @Inject
    public AttendeeCheckInPresenter(AttendeeRepository attendeeRepository, DatabaseChangeListener<Attendee> databaseChangeListener) {
        this.attendeeRepository = attendeeRepository;
        this.databaseChangeListener = databaseChangeListener;
    }

    @Override
    public void start() {
        databaseChangeListener.startListening();

        databaseChangeListener.getNotifier()
            .compose(dispose(getDisposable()))
            .map(DbFlowDatabaseChangeListener.ModelChange::getModel)
            .filter(filterAttendee -> filterAttendee.getId() == attendee.getId())
            .flatMap(filterAttendee -> attendeeRepository.getAttendee(attendee.getId(), false))
            .compose(erroneousResult(getView()))
            .subscribe(attendee -> this.attendee = attendee, Logger::logError);

        attendeeRepository.getAttendee(getId(), false)
            .compose(dispose(getDisposable()))
            .compose(erroneousResult(getView()))
            .subscribe(attendee -> this.attendee = attendee, Logger::logError);
    }

    @Override
    public void detach() {
        super.detach();
        databaseChangeListener.stopListening();
    }

    public void toggleCheckIn() {
        attendee.setChecking(true);
        attendee.isCheckedIn = !attendee.isCheckedIn;

        attendeeRepository.scheduleToggle(attendee)
            .compose(disposeCompletable(getDisposable()))
            .compose(erroneousCompletable(getView()))
            .subscribe(() -> {
                // Nothing to do
            }, Logger::logSuccess);
    }

    @VisibleForTesting
    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }

    @VisibleForTesting
    public AttendeeCheckInView getView() {
        return super.getView();
    }
}
