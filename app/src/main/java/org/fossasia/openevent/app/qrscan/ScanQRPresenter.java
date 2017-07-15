package org.fossasia.openevent.app.qrscan;

import com.google.android.gms.vision.barcode.Barcode;

import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.qrscan.contract.IScanQRPresenter;
import org.fossasia.openevent.app.qrscan.contract.IScanQRView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class ScanQRPresenter implements IScanQRPresenter {

    private static final String CLEAR_DISTINCT = "clear";

    private long eventId;

    private IScanQRView scanQRView;
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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(receiving -> scanQRView.showBarcodePanel(!receiving));

        data.distinctUntilChanged()
            .filter(barcode -> !paused)
            .filter(barcode -> !barcode.equals(CLEAR_DISTINCT))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::processBarcode);
    }

    private void processBarcode(String barcode) {
        scanQRView.showBarcodeData(barcode);

        Observable.fromIterable(attendees)
            .filter(attendee -> attendee.getOrder() != null)
            .filter(attendee -> (attendee.getOrder().getIdentifier() + "-" + attendee.getId()).equals(barcode))
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(attendee -> {
                if (scanQRView == null)
                    return;

                scanQRView.onScannedAttendee(attendee);
            });
    }

    public void setAttendees(List<Attendee> attendees) {
        this.attendees = attendees;
    }

    @Override
    public void attach(long eventId, IScanQRView scanQRView) {
        this.eventId = eventId;
        this.scanQRView = scanQRView;
    }

    @Override
    public void start() {
        if(scanQRView == null)
            return;

        loadAttendees();

        scanQRView.showProgress(true);
        scanQRView.loadCamera();
    }

    @Override
    public void detach() {
        scanQRView = null;
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
        attendeeRepository.getAttendees(eventId, false)
            .toList()
            .subscribe(attendeeList -> {
                attendees.clear();
                attendees.addAll(attendeeList);
            });
    }

    @Override
    public void cameraPermissionGranted(boolean granted) {
        if(scanQRView == null)
            return;

        if(granted) {
            scanQRView.startScan();
        } else {
            scanQRView.showProgress(false);
            scanQRView.showPermissionError("User denied permission");
        }
    }

    @Override
    public void onBarcodeDetected(Barcode barcode) {
        if(scanQRView == null)
            return;

        detect.onNext(barcode == null);

        if (barcode == null || attendees.isEmpty())
            return;

        data.onNext(barcode.displayValue);
    }

    @Override
    public void onScanStarted() {
        if(scanQRView == null)
            return;

        scanQRView.showProgress(false);
    }

    @Override
    public void onCameraLoaded() {
        if(scanQRView == null)
            return;

        if(scanQRView.hasCameraPermission()) {
            scanQRView.startScan();
        } else {
            scanQRView.requestCameraPermission();
        }
    }

    @Override
    public void onCameraDestroyed() {
        if(scanQRView == null)
            return;

        scanQRView.stopScan();
    }

    public IScanQRView getView() {
        return scanQRView;
    }
}
