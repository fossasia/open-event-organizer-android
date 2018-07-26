package org.fossasia.openevent.app.core.attendee.qrscan;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

import org.fossasia.openevent.app.core.attendee.qrscan.widget.GraphicOverlay;

import io.reactivex.Notification;
import io.reactivex.subjects.PublishSubject;

/**
 * Factory for creating a tracker and associated graphic to be associated with a new barcode.  The
 * multi-processor uses this factory to create barcode trackers as needed -- one for each barcode.
 */
public class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
    private final GraphicOverlay<BarcodeGraphic> graphicOverlay;
    private final PublishSubject<Notification<Barcode>> barcodeEmitter;

    public BarcodeTrackerFactory(GraphicOverlay<BarcodeGraphic> barcodeGraphicOverlay, PublishSubject<Notification<Barcode>> barcodeEmitter) {
        this.graphicOverlay = barcodeGraphicOverlay;
        this.barcodeEmitter = barcodeEmitter;
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        BarcodeGraphic graphic = new BarcodeGraphic(graphicOverlay);
        return new BarcodeGraphicTracker(graphicOverlay, graphic, barcodeEmitter);
    }

}
