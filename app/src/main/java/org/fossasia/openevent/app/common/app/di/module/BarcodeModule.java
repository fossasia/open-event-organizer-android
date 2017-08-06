package org.fossasia.openevent.app.common.app.di.module;

import android.content.Context;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.fossasia.openevent.app.OrgaProvider;
import org.fossasia.openevent.app.module.attendee.qrscan.BarcodeGraphic;
import org.fossasia.openevent.app.module.attendee.qrscan.BarcodeTrackerFactory;
import org.fossasia.openevent.app.module.attendee.qrscan.widget.GraphicOverlay;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Notification;
import io.reactivex.subjects.PublishSubject;

@Module(includes = AndroidModule.class)
public class BarcodeModule {

    private final GraphicOverlay<BarcodeGraphic> graphicOverlay;

    public BarcodeModule(GraphicOverlay<BarcodeGraphic> graphicOverlay) {
        this.graphicOverlay = graphicOverlay;
    }

    @Provides
    @Singleton
    @Named("barcodeEmitter")
    PublishSubject<Notification<Barcode>> providesBarcodeEmitter() {
        return PublishSubject.create();
    }

    @Provides
    BarcodeDetector providesBarCodeDetector(@Named("barcodeEmitter") PublishSubject<Notification<Barcode>> barcodeEmitter) {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(OrgaProvider.context)
            .setBarcodeFormats(Barcode.QR_CODE)
            .build();

        barcodeDetector.setProcessor(
            new MultiProcessor.Builder<>(new BarcodeTrackerFactory(graphicOverlay, barcodeEmitter)).build());

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
