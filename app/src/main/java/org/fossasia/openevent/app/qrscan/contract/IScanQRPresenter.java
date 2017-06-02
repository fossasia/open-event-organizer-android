package org.fossasia.openevent.app.qrscan.contract;

import com.google.android.gms.vision.barcode.Barcode;

public interface IScanQRPresenter {

    void attach();

    void detach();

    void cameraPermissionGranted(boolean granted);

    void onBarcodeDetected(Barcode barcode);

    void onScanStarted();

    void onCameraLoaded();

    void onCameraDestroyed();

}
