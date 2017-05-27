package org.fossasia.openevent.app.qrscan;

import android.util.SparseArray;

import com.google.android.gms.vision.barcode.Barcode;

import org.fossasia.openevent.app.data.contract.IEventDataRepository;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.qrscan.contract.IScanQRPresenter;
import org.fossasia.openevent.app.qrscan.contract.IScanQRView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class ScanQRPresenter implements IScanQRPresenter {

    private long eventId;

    private IScanQRView scanQRView;
    private IEventDataRepository eventDataRepository;
    private List<Attendee> attendees = new ArrayList<>();

    private PublishSubject<Boolean> detect = PublishSubject.create();
    private PublishSubject<String> data = PublishSubject.create();

    public ScanQRPresenter(long eventId, IScanQRView scanQRView, IEventDataRepository eventDataRepository) {
        this.eventId = eventId;
        this.scanQRView = scanQRView;
        this.eventDataRepository = eventDataRepository;

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
            .filter(attendee -> (attendee.getOrder().getIdentifier() + "-" + attendee.getId()).equals(barcode))
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(attendee -> scanQRView.onScannedAttendee(attendee));
    }

    @Override
    public void attach() {
        loadAttendees();
        scanQRView.loadCamera();
        scanQRView.showProgressBar(true);
    }

    @Override
    public void detach() {
        scanQRView = null;
    }

    private void loadAttendees() {
        eventDataRepository.getAttendees(eventId, false)
            .subscribe(attendeeList -> {
                attendees.clear();
                attendees.addAll(attendeeList);
            });
    }

    @Override
    public void cameraPermissionGranted(boolean granted) {
        if(granted) {
            scanQRView.startScan();
        } else {
            scanQRView.showPermissionError("User denied permission");
        }
    }

    @Override
    public void onBarcodeDetected(SparseArray<Barcode> barcodes) {
        detect.onNext(barcodes.size() == 0);

        if (barcodes.size() == 0 || attendees == null)
            return;

        data.onNext(barcodes.valueAt(0).displayValue);
    }

    @Override
    public void onScanStarted() {
        scanQRView.showProgressBar(false);
    }

    @Override
    public void onCameraLoaded() {
        if(scanQRView.hasCameraPermission()) {
            scanQRView.startScan();
        } else {
            scanQRView.requestCameraPermission();
        }
    }

    @Override
    public void onCameraDestroyed() {
        scanQRView.stopScan();
    }
}
