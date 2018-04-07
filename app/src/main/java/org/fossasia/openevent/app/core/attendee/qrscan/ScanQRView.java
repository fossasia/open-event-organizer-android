package org.fossasia.openevent.app.core.attendee.qrscan;

import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.data.attendee.Attendee;

import io.reactivex.annotations.NonNull;

public interface ScanQRView extends Progressive {

    boolean hasCameraPermission();

    void requestCameraPermission();

    void showPermissionError(String error);

    void onScannedAttendee(Attendee attendee);

    void showBarcodePanel(boolean show);

    void showBarcodeData(@NonNull String data);

    void loadCamera();

    void startScan();

    void stopScan();

}
