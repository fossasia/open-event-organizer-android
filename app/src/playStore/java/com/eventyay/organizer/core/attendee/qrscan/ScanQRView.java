package com.eventyay.organizer.core.attendee.qrscan;

import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.data.attendee.Attendee;

import io.reactivex.annotations.NonNull;

public interface ScanQRView extends Progressive {

    boolean hasCameraPermission();

    void requestCameraPermission();

    void showPermissionError(String error);

    void onScannedAttendee(Attendee attendee);

    void showBarcodePanel(boolean show);

    void showMessage(@NonNull int stringRes);

    void setTint(boolean matched);

    void startScan();

    void stopScan();

}
