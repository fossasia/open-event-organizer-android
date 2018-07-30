package com.eventyay.organizer.core.attendee.history;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.attendee.AttendeeRepository;
import com.eventyay.organizer.data.attendee.CheckInDetail;
import com.eventyay.organizer.utils.ErrorUtils;
import com.eventyay.organizer.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class CheckInHistoryViewModel extends ViewModel {

    private final AttendeeRepository attendeeRepository;
    private final MutableLiveData<List<CheckInDetail>> checkInDetailsLiveData = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MutableLiveData<Attendee> attendeeLive = new MutableLiveData<>();
    private final MutableLiveData<Boolean> progress = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private static final String SCAN_IN = "Scan In";
    private static final String SCAN_OUT = "Scan Out";

    @Inject
    public CheckInHistoryViewModel(AttendeeRepository attendeeRepository) {
        this.attendeeRepository = attendeeRepository;
    }

    public void loadAttendee(long id, boolean reload) {
        compositeDisposable.add(attendeeRepository.getAttendee(id, reload)
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> {
                loadCheckInDetails(attendeeLive.getValue());
                progress.setValue(false);
            })
            .subscribe(attendeeLive::setValue, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    public void loadCheckInDetails(Attendee attendee) {
        if (attendee == null) {
            return ;
        }

        ArrayList<CheckInDetail> checkInDetails = new ArrayList<>(200);

        if (Utils.isEmpty(attendee.checkinTimes)) {
            return ;
        }

        if (Utils.isEmpty(attendee.checkoutTimes)) {
            CheckInDetail checkInDetail = new CheckInDetail();

            checkInDetail.setCheckTime(attendee.getCheckinTimes());
            checkInDetail.setScanAction(SCAN_IN);
            checkInDetails.add(checkInDetail);
        } else {
            String[] checkInTimes = attendee.getCheckinTimes().split(",");
            String[] checkOutTimes = attendee.getCheckoutTimes().split(",");

            for (int i=0; i<checkInTimes.length + checkOutTimes.length; i++) {
                CheckInDetail checkInDetail = new CheckInDetail();

                if (i%2 == 0) {
                    checkInDetail.setCheckTime(checkInTimes[i/2]);
                    checkInDetail.setScanAction(SCAN_IN);
                    checkInDetails.add(checkInDetail);
                } else {
                    checkInDetail.setCheckTime(checkOutTimes[(i-1)/2]);
                    checkInDetail.setScanAction(SCAN_OUT);
                    checkInDetails.add(checkInDetail);
                }
            }
        }

        checkInDetailsLiveData.setValue(checkInDetails);
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<Attendee> getAttendee() {
        return attendeeLive;
    }

    public LiveData<List<CheckInDetail>> getCheckInHistory() {
        return checkInDetailsLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
