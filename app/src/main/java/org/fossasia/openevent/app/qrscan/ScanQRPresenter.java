package org.fossasia.openevent.app.qrscan;

import android.support.annotation.VisibleForTesting;

import com.google.android.gms.vision.barcode.Barcode;

import org.fossasia.openevent.app.common.BaseDetailPresenter;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.qrscan.contract.IScanQRPresenter;
import org.fossasia.openevent.app.qrscan.contract.IScanQRView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.schedule;

public class ScanQRPresenter extends BaseDetailPresenter<Long, IScanQRView> implements IScanQRPresenter {

    private static final String CLEAR_DISTINCT = "clear";

    private IAttendeeRepository attendeeRepository;
    private List<Attendee> attendees = new ArrayList<>();

    private PublishSubject<Boolean> detect = PublishSubject.create();
    private PublishSubject<String> data = PublishSubject.create();

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

    public void setAttendees(List<Attendee> attendees) {
        this.attendees = attendees;
    }

    @Override
    public void attach(Long eventId, IScanQRView scanQRView) {
        super.attach(eventId, scanQRView);
    }

    @Override
    public void start() {
        if(getView() == null)
            return;

        loadAttendees();

        getView().showProgress(true);
        getView().loadCamera();
    }

    @Override
    public void detach() {
        super.detach();
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
            });
    }

    @Override
    public void cameraPermissionGranted(boolean granted) {
        if(getView() == null)
            return;

        if(granted) {
            getView().startScan();
        } else {
            getView().showProgress(false);
            getView().showPermissionError("User denied permission");
        }
    }

    @Override
    public void onBarcodeDetected(Barcode barcode) {
        if(getView() == null)
            return;

        detect.onNext(barcode == null);

        if (barcode == null || attendees.isEmpty())
            return;

        data.onNext(barcode.displayValue);
    }

    @Override
    public void onScanStarted() {
        if(getView() == null)
            return;

        getView().showProgress(false);
    }

    @Override
    public void onCameraLoaded() {
        if(getView() == null)
            return;

        if(getView().hasCameraPermission()) {
            getView().startScan();
        } else {
            getView().requestCameraPermission();
        }
    }

    @Override
    public void onCameraDestroyed() {
        if(getView() == null)
            return;

        getView().stopScan();
    }

    @VisibleForTesting
    public IScanQRView getView() {
        return super.getView();
    }
}
