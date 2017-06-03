package org.fossasia.openevent.app.qrscan;

import com.google.android.gms.vision.barcode.Barcode;

import org.fossasia.openevent.app.data.contract.IEventRepository;
import org.fossasia.openevent.app.data.models.Attendee;
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

    private long eventId;

    private IScanQRView scanQRView;
    private IEventRepository eventRepository;
    private List<Attendee> attendees = new ArrayList<>();

    private PublishSubject<Boolean> detect = PublishSubject.create();
    private PublishSubject<String> data = PublishSubject.create();

    @Inject
    public ScanQRPresenter(IEventRepository eventRepository) {
        this.eventRepository = eventRepository;

        detect.distinctUntilChanged()
            .debounce(150, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(receiving -> scanQRView.showBarcodePanel(!receiving));

        data.distinctUntilChanged()
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
                if(scanQRView == null)
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

        scanQRView.showProgressBar(true);
        scanQRView.loadCamera();
    }

    @Override
    public void detach() {
        scanQRView = null;
    }

    private void loadAttendees() {
        eventRepository.getAttendees(eventId, false)
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
            scanQRView.showProgressBar(false);
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

        scanQRView.showProgressBar(false);
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
