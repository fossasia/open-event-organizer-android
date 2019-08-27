package com.eventyay.organizer.core.attendee.qrscan;

import com.eventyay.organizer.core.attendee.qrscan.widget.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import io.reactivex.Notification;
import io.reactivex.subjects.PublishSubject;

/**
 * Generic tracker which is used for tracking or reading a barcode (and can really be used for any
 * type of item). This is used to receive newly detected items, add a graphical representation to an
 * overlay, update the graphics as the item changes, and remove the graphics when the item goes
 * away.
 */
public class BarcodeGraphicTracker extends Tracker<Barcode> {
    private final GraphicOverlay<BarcodeGraphic> graphicOverlay;
    private final BarcodeGraphic graphic;

    private final PublishSubject<Notification<Barcode>> barcodeEmitter;

    BarcodeGraphicTracker(
            GraphicOverlay<BarcodeGraphic> overlay,
            BarcodeGraphic graphic,
            PublishSubject<Notification<Barcode>> barcodeEmitter) {
        this.graphicOverlay = overlay;
        this.graphic = graphic;
        this.barcodeEmitter = barcodeEmitter;
    }

    /** Start tracking the detected item instance within the item overlay. */
    @Override
    public void onNewItem(int id, Barcode item) {
        graphic.setId(id);
        barcodeEmitter.onNext(Notification.createOnNext(item));
    }

    /** Update the position/characteristics of the item within the overlay. */
    @Override
    public void onUpdate(Detector.Detections<Barcode> detectionResults, Barcode item) {
        graphicOverlay.add(graphic);
        graphic.updateItem(item);
        barcodeEmitter.onNext(Notification.createOnNext(item));
    }

    /**
     * Hide the graphic when the corresponding object was not detected. This can happen for
     * intermediate frames temporarily, for example if the object was momentarily blocked from view.
     */
    @Override
    public void onMissing(Detector.Detections<Barcode> detectionResults) {
        graphicOverlay.remove(graphic);
        barcodeEmitter.onNext(Notification.createOnError(new Throwable()));
    }

    /**
     * Called when the item is assumed to be gone for good. Remove the graphic annotation from the
     * overlay.
     */
    @Override
    public void onDone() {
        graphicOverlay.remove(graphic);
    }
}
