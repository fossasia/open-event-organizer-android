package org.fossasia.openevent.app.qrscan.contract;

import org.fossasia.openevent.app.data.models.Attendee;

import io.reactivex.annotations.NonNull;

public interface IScanQRView {

    boolean hasCameraPermission();

    void requestCameraPermission();

    void showPermissionError(String error);

    void onScannedAttendee(Attendee attendee);

    void showBarcodePanel(boolean show);

    void showBarcodeData(@NonNull String data);

    void showProgressBar(boolean show);

    void loadCamera();

    void startScan();

    void stopScan();

}
