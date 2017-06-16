package org.fossasia.openevent.app.common.di.module;

import android.content.Context;
import android.util.SparseArray;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Notification;
import io.reactivex.subjects.PublishSubject;

@Module(includes = AndroidModule.class)
public class BarcodeModule {

    @Provides
    @Singleton
    @Named("barcodeEmitter")
    PublishSubject<Notification<Barcode>> providesBarcodeEmitter() {
        return PublishSubject.create();
    }

    @Provides
    Detector.Processor<Barcode> providesProcessor(@Named("barcodeEmitter") PublishSubject<Notification<Barcode>> emitter) {
        return new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // No action to be taken
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> barcodeSparseArray = detections.getDetectedItems();
                if (barcodeSparseArray.size() == 0)
                    emitter.onNext(Notification.createOnError(new Throwable()));
                else
                    emitter.onNext(Notification.createOnNext(barcodeSparseArray.valueAt(0)));
            }
        };
    }

    @Provides
    BarcodeDetector providesBarCodeDetector(Context context, Detector.Processor<Barcode> processor) {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context)
            .setBarcodeFormats(Barcode.QR_CODE)
            .build();

        barcodeDetector.setProcessor(processor);

        return barcodeDetector;
    }

    @Provides
    CameraSource providesCameraSource(Context context, BarcodeDetector barcodeDetector) {
        return new CameraSource
            .Builder(context, barcodeDetector)
            .setRequestedPreviewSize(640, 480)
            .setRequestedFps(15.0f)
            .setAutoFocusEnabled(true)
            .build();
    }

}
