package org.fossasia.openevent.app.qrscan.contract;

import android.util.SparseArray;

import com.google.android.gms.vision.barcode.Barcode;

public interface IScanQRPresenter {

    void attach();

    void detach();

    void cameraPermissionGranted(boolean granted);

    void onBarcodeDetected(SparseArray<Barcode> barcodes);

    void onScanStarted();

    void onCameraLoaded();

    void onCameraDestroyed();

}
