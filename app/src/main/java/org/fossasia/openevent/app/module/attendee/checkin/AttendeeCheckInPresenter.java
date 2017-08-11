package org.fossasia.openevent.app.module.attendee.checkin;

import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.app.common.app.lifecycle.presenter.BaseDetailPresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.db.DatabaseChangeListener;
import org.fossasia.openevent.app.common.data.db.contract.IDatabaseChangeListener;
import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.module.attendee.checkin.contract.IAttendeeCheckInPresenter;
import org.fossasia.openevent.app.module.attendee.checkin.contract.IAttendeeCheckInView;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.erroneousCompletable;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.erroneousResult;

public class AttendeeCheckInPresenter extends BaseDetailPresenter<Long, IAttendeeCheckInView> implements IAttendeeCheckInPresenter {

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
            .distinctUntilChanged(Attendee::isCheckedIn)
            .skip(1)
            .compose(erroneousResult(getView()))
            .subscribe(attendee -> {
                this.attendee = attendee;
                String status = attendee.isCheckedIn() ? "Checked In" : "Checked Out";
                getView().onSuccess(status);
            }, Logger::logError);

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

    @Override
    public void toggleCheckIn() {
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
