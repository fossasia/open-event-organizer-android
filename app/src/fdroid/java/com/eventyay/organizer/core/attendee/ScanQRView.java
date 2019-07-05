package com.eventyay.organizer.core.attendee;

import androidx.annotation.NonNull;

import com.eventyay.organizer.data.attendee.Attendee;

public interface ScanQRView {

    boolean hasCameraPermission();

    void requestCameraPermission();

    void showPermissionError(String error);

    void onScannedAttendee(Attendee attendee);

    void showBarcodePanel(boolean show);

    void showMessage(@NonNull int stringRes);

    void setTint(boolean matched);

    void showProgress(boolean show);

    void startScan();
}
