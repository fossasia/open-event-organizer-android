package com.eventyay.organizer.core.attendee.checkin;

import android.annotation.SuppressLint;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.VisibleForTesting;

import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.db.DbFlowDatabaseChangeListener;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.attendee.AttendeeRepository;
import com.eventyay.organizer.utils.ErrorUtils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static com.eventyay.organizer.common.rx.ViewTransformers.dispose;

public class AttendeeCheckInViewModel extends ViewModel {

    private final AttendeeRepository attendeeRepository;
    private final DatabaseChangeListener<Attendee> databaseChangeListener;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleEventLiveData<Attendee> attendeeliveData = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();

    @Inject
    public AttendeeCheckInViewModel(AttendeeRepository attendeeRepository, DatabaseChangeListener<Attendee> databaseChangeListener) {
        this.attendeeRepository = attendeeRepository;
        this.databaseChangeListener = databaseChangeListener;
    }

    @SuppressLint("CheckResult")
    public void start(long attendeeId) {
        databaseChangeListener.startListening();

        compositeDisposable.add(
            databaseChangeListener.getNotifier()
                .compose(dispose(compositeDisposable))
                .map(DbFlowDatabaseChangeListener.ModelChange::getModel)
                .filter(filterAttendee -> filterAttendee.getId() == attendeeliveData.getValue().getId())
                .flatMap(filterAttendee -> attendeeRepository.getAttendee(attendeeliveData.getValue().getId(), false))
                .subscribe(this.attendeeliveData::setValue,
                    throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));

        compositeDisposable.add(
            attendeeRepository.getAttendee(attendeeId, false)
                .compose(dispose(compositeDisposable))
                .subscribe(this.attendeeliveData::setValue,
                    throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    public LiveData<Attendee> getAttendee() {
        return attendeeliveData;
    }

    public LiveData<String> getError() {
        return error;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        databaseChangeListener.stopListening();
    }

    public void toggleCheckIn() {
        attendeeliveData.getValue().setChecking(true);
        attendeeliveData.getValue().isCheckedIn = !attendeeliveData.getValue().isCheckedIn;

        compositeDisposable.add(
            attendeeRepository.scheduleToggle(attendeeliveData.getValue())
                .subscribe(() -> {
                    // Nothing to do
                }, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    @VisibleForTesting
    public void setAttendee(Attendee attendee) {
        this.attendeeliveData.setValue(attendee);
    }

}
