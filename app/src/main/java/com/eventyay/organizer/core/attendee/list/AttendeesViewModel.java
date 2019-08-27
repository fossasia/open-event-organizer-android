package com.eventyay.organizer.core.attendee.list;

import android.annotation.SuppressLint;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.Preferences;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.attendee.AttendeeRepository;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.db.DbFlowDatabaseChangeListener;
import com.eventyay.organizer.utils.ErrorUtils;
import com.eventyay.organizer.utils.Utils;
import com.raizlabs.android.dbflow.structure.BaseModel;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class AttendeesViewModel extends ViewModel {

    private final AttendeeRepository attendeeRepository;
    private final DatabaseChangeListener<Attendee> attendeeListener;
    private final Preferences preferences;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final List<Attendee> attendeeList = new ArrayList<>();

    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<List<Attendee>> attendeesLiveData =
            new SingleEventLiveData<>();
    private final SingleEventLiveData<Boolean> showScanButtonLiveData = new SingleEventLiveData<>();
    private final SingleEventLiveData<Attendee> updateAttendeeLiveData =
            new SingleEventLiveData<>();

    private long eventId;

    @Inject
    public AttendeesViewModel(
            AttendeeRepository attendeeRepository,
            DatabaseChangeListener<Attendee> attendeeListener,
            Preferences preferences) {
        this.attendeeRepository = attendeeRepository;
        this.attendeeListener = attendeeListener;
        this.preferences = preferences;

        eventId = ContextManager.getSelectedEvent().getId();
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<List<Attendee>> getAttendeesLiveData() {
        return attendeesLiveData;
    }

    public LiveData<Boolean> getShowScanButtonLiveData() {
        return showScanButtonLiveData;
    }

    public LiveData<Attendee> getUpdateAttendeeLiveData() {
        return updateAttendeeLiveData;
    }

    public List<Attendee> getAttendees() {
        return attendeeList;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void loadAttendees(boolean forceReload) {

        showScanButtonLiveData.setValue(false);

        compositeDisposable.add(
                getAttendeeSource(forceReload)
                        .doOnSubscribe(disposable -> progress.setValue(true))
                        .doFinally(() -> progress.setValue(false))
                        .toSortedList()
                        .subscribe(
                                attendees -> {
                                    attendeeList.clear();
                                    attendeeList.addAll(attendees);
                                    attendeesLiveData.setValue(attendees);
                                    showScanButtonLiveData.setValue(!attendeeList.isEmpty());
                                },
                                throwable ->
                                        error.setValue(
                                                ErrorUtils.getMessage(throwable).toString())));
    }

    public void loadAttendeesPageWise(long pageNumber, boolean forceReload) {

        showScanButtonLiveData.setValue(false);

        compositeDisposable.add(
                getAttendeeSourcePageWise(pageNumber, forceReload)
                        .doOnSubscribe(disposable -> progress.setValue(true))
                        .doFinally(() -> progress.setValue(false))
                        .toSortedList()
                        .subscribe(
                                attendees -> {
                                    attendeeList.addAll(attendees);
                                    attendeesLiveData.setValue(attendees);
                                    showScanButtonLiveData.setValue(!attendeeList.isEmpty());
                                },
                                throwable ->
                                        error.setValue(
                                                ErrorUtils.getMessage(throwable).toString())));
    }

    private Observable<Attendee> getAttendeeSource(boolean forceReload) {
        if (!forceReload && !attendeeList.isEmpty()) return Observable.fromIterable(attendeeList);
        else return attendeeRepository.getAttendees(eventId, forceReload);
    }

    private Observable<Attendee> getAttendeeSourcePageWise(long pageNumber, boolean forceReload) {
        if (!forceReload && !attendeeList.isEmpty()) return Observable.fromIterable(attendeeList);
        else return attendeeRepository.getAttendeesPageWise(eventId, pageNumber, forceReload);
    }

    private void updateLocal(Attendee attendee) {
        Utils.indexOf(attendeeList, attendee, (first, second) -> first.getId() == second.getId())
                .subscribeOn(Schedulers.computation())
                .subscribe(index -> attendeeList.set(index, attendee), Logger::logError);
    }

    public void listenChanges() {
        attendeeListener.startListening();

        attendeeListener
                .getNotifier()
                .filter(
                        attendeeModelChange ->
                                attendeeModelChange.getAction().equals(BaseModel.Action.UPDATE))
                .map(DbFlowDatabaseChangeListener.ModelChange::getModel)
                .flatMap(
                        filterAttendee ->
                                attendeeRepository.getAttendee(filterAttendee.getId(), false))
                .subscribe(
                        attendee -> {
                            updateAttendeeLiveData.setValue(attendee);
                            updateLocal(attendee);
                        },
                        Logger::logError);
    }

    @VisibleForTesting
    public void setAttendeeList(List<Attendee> attendeeList) {
        this.attendeeList.clear();
        this.attendeeList.addAll(attendeeList);
    }

    @SuppressLint("CheckResult")
    public void toggleCheckInState(List<Attendee> attendeeList, int swipedPosition) {
        Attendee attendee = attendeeList.get(swipedPosition);
        attendee.setChecking(true);
        attendee.isCheckedIn = !attendee.isCheckedIn;
        compositeDisposable.add(
                attendeeRepository
                        .scheduleToggle(attendee)
                        .subscribe(
                                () -> {
                                    // Nothing to do
                                },
                                Logger::logError));
    }
}
