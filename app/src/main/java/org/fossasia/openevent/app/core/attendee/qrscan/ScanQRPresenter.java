package org.fossasia.openevent.app.core.attendee.qrscan;

import android.support.annotation.VisibleForTesting;

import com.google.android.gms.vision.barcode.Barcode;

import org.fossasia.openevent.app.common.mvp.presenter.AbstractDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.data.attendee.Attendee;
import org.fossasia.openevent.app.data.attendee.AttendeeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.schedule;

public class ScanQRPresenter extends AbstractDetailPresenter<Long, ScanQRView> {

    private static final String CLEAR_DISTINCT = "clear";

    private final AttendeeRepository attendeeRepository;
    private final List<Attendee> attendees = new ArrayList<>();

    private final PublishSubject<Boolean> detect = PublishSubject.create();
    private final PublishSubject<String> data = PublishSubject.create();

    private boolean paused;

    @Inject
    public ScanQRPresenter(AttendeeRepository attendeeRepository) {
        this.attendeeRepository = attendeeRepository;

        detect.distinctUntilChanged()
            .debounce(150, TimeUnit.MILLISECONDS)
            .compose(schedule())
            .subscribe(receiving -> getView().showBarcodePanel(!receiving));

        data.distinctUntilChanged()
            .filter(barcode -> !paused)
            .filter(barcode -> !barcode.equals(CLEAR_DISTINCT))
            .compose(schedule())
            .subscribe(this::processBarcode);
    }

    private void processBarcode(String barcode) {
        getView().showBarcodeData(barcode);

        Observable.fromIterable(attendees)
            .compose(dispose(getDisposable()))
            .filter(attendee -> attendee.getOrder() != null)
            .filter(attendee -> (attendee.getOrder().getIdentifier() + "-" + attendee.getId()).equals(barcode))
            .compose(schedule())
            .subscribe(attendee -> getView().onScannedAttendee(attendee));
    }

    public void setAttendees(List<Attendee> attendeeList) {
        attendees.clear();
        attendees.addAll(attendeeList);
    }

    @Override
    public void start() {
        loadAttendees();

        getView().showProgress(true);
        onCameraLoaded();
    }

    public void pauseScan() {
        paused = true;
    }

    public void resumeScan() {
        paused = false;
        data.onNext(CLEAR_DISTINCT);
    }

    private void loadAttendees() {
        attendeeRepository.getAttendees(getId(), false)
            .compose(dispose(getDisposable()))
            .toList()
            .subscribe(attendeeList -> {
                attendees.clear();
                attendees.addAll(attendeeList);
            }, Logger::logError);
    }

    public void cameraPermissionGranted(boolean granted) {
        if (granted) {
            getView().startScan();
        } else {
            getView().showProgress(false);
            getView().showPermissionError("User denied permission");
        }
    }

    public void onBarcodeDetected(Barcode barcode) {
        detect.onNext(barcode == null);

        if (barcode == null || attendees.isEmpty())
            return;

        data.onNext(barcode.displayValue);
    }

    public void onScanStarted() {
        getView().showProgress(false);
    }

    public void onCameraLoaded() {
        if (getView().hasCameraPermission()) {
            getView().startScan();
        } else {
            getView().requestCameraPermission();
        }
    }

    public void onCameraDestroyed() {
        getView().stopScan();
    }

    @VisibleForTesting
    public ScanQRView getView() {
        return super.getView();
    }
}
