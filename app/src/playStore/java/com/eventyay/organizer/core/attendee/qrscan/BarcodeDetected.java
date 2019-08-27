package com.eventyay.organizer.core.attendee.qrscan;

import com.eventyay.organizer.data.attendee.Attendee;
import com.google.android.gms.vision.barcode.Barcode;
import io.reactivex.subjects.PublishSubject;
import java.util.List;

public class BarcodeDetected {

    public void onBarcodeDetected(
            Barcode barcode,
            PublishSubject<Boolean> detect,
            PublishSubject<String> data,
            List<Attendee> attendees) {

        detect.onNext(barcode == null);

        if (barcode == null || attendees.isEmpty()) return;

        data.onNext(barcode.displayValue);
    }
}
