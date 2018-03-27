package org.fossasia.openevent.app.core.attendee.checkin;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.common.mvp.presenter.BaseDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.data.db.IDatabaseChangeListener;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.repository.IAttendeeRepository;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.erroneousCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.erroneousResult;

public class AttendeeCheckInPresenter extends BaseDetailPresenter<Long, IAttendeeCheckInView> {

    private final IAttendeeRepository attendeeRepository;
    private final IDatabaseChangeListener<Attendee> databaseChangeListener;

    private Attendee attendee;

    @Inject
    public AttendeeCheckInPresenter(IAttendeeRepository attendeeRepository, IDatabaseChangeListener<Attendee> databaseChangeListener) {
        this.attendeeRepository = attendeeRepository;
        this.databaseChangeListener = databaseChangeListener;
    }

    @Override
    public void start() {
        databaseChangeListener.startListening();

        databaseChangeListener.getNotifier()
            .compose(dispose(getDisposable()))
            .map(DatabaseChangeListener.ModelChange::getModel)
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
    public IAttendeeCheckInView getView() {
        return super.getView();
    }
}
