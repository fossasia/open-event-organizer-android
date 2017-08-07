package org.fossasia.openevent.app.module.attendee.qrscan;

import android.support.annotation.VisibleForTesting;

import com.google.android.gms.vision.barcode.Barcode;

import org.fossasia.openevent.app.common.app.lifecycle.presenter.BaseDetailPresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.module.attendee.qrscan.contract.IScanQRPresenter;
import org.fossasia.openevent.app.module.attendee.qrscan.contract.IScanQRView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.schedule;

public class ScanQRPresenter extends BaseDetailPresenter<Long, IScanQRView> implements IScanQRPresenter {

    private static final String CLEAR_DISTINCT = "clear";

    private final IAttendeeRepository attendeeRepository;
    private final List<Attendee> attendees = new ArrayList<>();

    private final PublishSubject<Boolean> detect = PublishSubject.create();
    private final PublishSubject<String> data = PublishSubject.create();

    private boolean paused;

    @Inject
    public ScanQRPresenter(IAttendeeRepository attendeeRepository) {
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
        getView().loadCamera();
    }

    @Override
    public void pauseScan() {
        paused = true;
    }

    @Override
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

    @Override
    public void cameraPermissionGranted(boolean granted) {
        if (granted) {
            getView().startScan();
        } else {
            getView().showProgress(false);
            getView().showPermissionError("User denied permission");
        }
    }

    @Override
    public void onBarcodeDetected(Barcode barcode) {
        detect.onNext(barcode == null);

        if (barcode == null || attendees.isEmpty())
            return;

        data.onNext(barcode.displayValue);
    }

    @Override
    public void onScanStarted() {
        getView().showProgress(false);
    }

    @Override
    public void onCameraLoaded() {
        if (getView().hasCameraPermission()) {
            getView().startScan();
        } else {
            getView().requestCameraPermission();
        }
    }

    @Override
    public void onCameraDestroyed() {
        getView().stopScan();
    }

    @VisibleForTesting
    public IScanQRView getView() {
        return super.getView();
    }
}
