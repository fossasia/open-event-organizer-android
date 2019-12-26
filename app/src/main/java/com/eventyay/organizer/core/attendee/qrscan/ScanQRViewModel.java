package com.eventyay.organizer.core.attendee.qrscan;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.data.Preferences;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.attendee.AttendeeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

import static com.eventyay.organizer.common.rx.ViewTransformers.schedule;

public class ScanQRViewModel extends ViewModel {

    private static final String CLEAR_DISTINCT = "clear";

    private final boolean toCheckIn;
    private final boolean toCheckOut;
    private final boolean toValidate;

    private final AttendeeRepository attendeeRepository;
    private final List<Attendee> attendees = new ArrayList<>();

    private final PublishSubject<Boolean> detect = PublishSubject.create();
    private final PublishSubject<String> data = PublishSubject.create();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<Void> dismiss = new SingleEventLiveData<>();
    private final SingleEventLiveData<Integer> message = new SingleEventLiveData<>();
    private final SingleEventLiveData<Boolean> tint = new SingleEventLiveData<>();
    private final SingleEventLiveData<Boolean> showBarcodePanelLiveData = new SingleEventLiveData<>();
    private final SingleEventLiveData<Attendee> onScannedAttendeeLiveData = new SingleEventLiveData<>();

    private boolean paused;

    @Inject
    public ScanQRViewModel(AttendeeRepository attendeeRepository, Preferences preferences) {
        this.attendeeRepository = attendeeRepository;

        detect.distinctUntilChanged()
            .debounce(150, TimeUnit.MILLISECONDS)
            .compose(schedule())
            .subscribe(receiving -> showBarcodePanelLiveData.setValue(!receiving));

        data.distinctUntilChanged()
            .filter(barcode -> !paused)
            .filter(barcode -> !barcode.equals(CLEAR_DISTINCT))
            .compose(schedule())
            .subscribe(this::processBarcode);

        toCheckIn = preferences.getBoolean(Constants.PREF_SCAN_WILL_CHECK_IN, true);
        toCheckOut = preferences.getBoolean(Constants.PREF_SCAN_WILL_CHECK_OUT, false);
        toValidate = preferences.getBoolean(Constants.PREF_SCAN_WILL_VALIDATE, false);
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<Void> getDismiss() {
        return dismiss;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Integer> getMessage() {
        return message;
    }

    public LiveData<Boolean> getTint() {
        return tint;
    }

    public LiveData<Boolean> getShowBarcodePanelLiveData() {
        return showBarcodePanelLiveData;
    }

    public LiveData<Attendee> getOnScannedAttendeeLiveData() {
        return onScannedAttendeeLiveData;
    }

    public PublishSubject<Boolean> getDetect() {
        return detect;
    }

    public PublishSubject<String> getData() {
        return data;
    }

    public List<Attendee> getAttendees() {
        return attendees;
    }

    public void processBarcode(String barcode) {

        Observable.fromIterable(attendees)
            .filter(attendee -> attendee.getOrder() != null)
            .filter(attendee -> (attendee.getOrder().getIdentifier() + "-" + attendee.getId()).equals(barcode))
            .compose(schedule())
            .toList()
            .subscribe(attendees -> {
                if (attendees.size() == 0) {
                    message.setValue(R.string.invalid_ticket);
                    tint.setValue(false);
                } else {
                    checkAttendee(attendees.get(0));
                }
            });
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis") // Inevitable DU Anomaly
    private void checkAttendee(Attendee attendee) {
        onScannedAttendeeLiveData.setValue(attendee);

        if (toValidate) {
            message.setValue(R.string.ticket_is_valid);
            tint.setValue(true);
            return;
        }

        boolean needsToggle = !(toCheckIn && attendee.isCheckedIn ||
            toCheckOut && !attendee.isCheckedIn);

        attendee.setChecking(true);
        showBarcodePanelLiveData.setValue(true);

        if (toCheckIn) {
            message.setValue(
                attendee.isCheckedIn ? R.string.already_checked_in : R.string.now_checked_in);
            tint.setValue(true);
            attendee.isCheckedIn = true;
        } else if (toCheckOut) {
            message.setValue(
                attendee.isCheckedIn ? R.string.now_checked_out : R.string.already_checked_out);
            tint.setValue(true);
            attendee.isCheckedIn = false;
        }

        if (needsToggle)
            compositeDisposable.add(
                attendeeRepository.scheduleToggle(attendee)
                    .subscribe(() -> {
                        // Nothing to do
                    }, Logger::logError));
    }

    public void setAttendees(List<Attendee> attendeeList) {
        attendees.clear();
        attendees.addAll(attendeeList);
    }

    public void pauseScan() {
        paused = true;
    }

    public void resumeScan() {
        paused = false;
        data.onNext(CLEAR_DISTINCT);
    }

    public void loadAttendees() {

        long eventId = ContextManager.getSelectedEvent().getId();

        attendeeRepository.getAttendees(eventId, false)
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .toList()
            .subscribe(attendeeList -> {
                attendees.clear();
                attendees.addAll(attendeeList);
            }, Logger::logError);
    }

    public void onScanStarted() {
        progress.setValue(false);
    }
}
