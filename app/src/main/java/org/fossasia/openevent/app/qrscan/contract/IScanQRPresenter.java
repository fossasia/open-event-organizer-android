package org.fossasia.openevent.app.qrscan.contract;

import com.google.android.gms.vision.barcode.Barcode;

import org.fossasia.openevent.app.common.contract.presenter.IDetailPresenter;

public interface IScanQRPresenter extends IDetailPresenter<Long, IScanQRView> {

    void pauseScan();

    void resumeScan();

    void cameraPermissionGranted(boolean granted);

    void onBarcodeDetected(Barcode barcode);

    void onScanStarted();

    void onCameraLoaded();

    void onCameraDestroyed();

}
