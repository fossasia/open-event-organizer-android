package org.fossasia.openevent.app.core.attendee.checkin;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.common.mvp.presenter.AbstractDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.db.DbFlowDatabaseChangeListener;
import org.fossasia.openevent.app.data.attendee.Attendee;
import org.fossasia.openevent.app.data.attendee.AttendeeRepository;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.erroneousCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.erroneousResult;

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
        attendee.checking.set(true);
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
