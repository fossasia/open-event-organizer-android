package com.eventyay.organizer.core.presenter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.attendee.qrscan.ScanQRPresenter;
import com.eventyay.organizer.core.attendee.qrscan.ScanQRView;
import com.eventyay.organizer.data.Preferences;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.attendee.AttendeeRepository;
import com.eventyay.organizer.data.order.Order;
import com.google.android.gms.vision.barcode.Barcode;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

@RunWith(JUnit4.class)
@SuppressWarnings("PMD.TooManyMethods")
public class ScanQRPresenterTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private ScanQRView scanQRView;
    @Mock private AttendeeRepository attendeeRepository;
    @Mock private Preferences preferences;

    private static final long EVENT_ID = 32;
    private ScanQRPresenter scanQRPresenter;

    private static final List<Attendee> ATTENDEES =
            Arrays.asList(
                    Attendee.builder().id(12).build(),
                    Attendee.builder().id(34).build(),
                    Attendee.builder().id(56).build(),
                    Attendee.builder().id(91).build(),
                    Attendee.builder().id(29).build(),
                    Attendee.builder().id(90).build(),
                    Attendee.builder().id(123).build());

    private static final List<Order> ORDERS =
            Arrays.asList(
                    Order.builder().identifier("test1").build(),
                    null,
                    Order.builder().identifier("test3").build(),
                    Order.builder().identifier("test4").build(),
                    Order.builder().identifier("test5").build(),
                    null,
                    Order.builder().identifier("test6").build());

    private static final Barcode BARCODE_1 = new Barcode();
    private static final Barcode BARCODE_2 = new Barcode();

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                schedulerCallable -> Schedulers.trampoline());

        scanQRPresenter = new ScanQRPresenter(attendeeRepository, preferences);
        scanQRPresenter.attach(EVENT_ID, scanQRView);

        BARCODE_1.displayValue = "test4-91";
        BARCODE_2.displayValue = "Test Barcode 2";

        for (int i = 0; i < ATTENDEES.size(); i++) ATTENDEES.get(i).setOrder(ORDERS.get(i));
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadAttendeesAutomatically() {
        when(attendeeRepository.getAttendees(EVENT_ID, false))
                .thenReturn(Observable.fromIterable(ATTENDEES));

        scanQRPresenter.start();

        verify(attendeeRepository).getAttendees(EVENT_ID, false);
    }

    @Test
    public void shouldLoadCameraAutomatically() {
        when(attendeeRepository.getAttendees(EVENT_ID, false))
                .thenReturn(Observable.fromIterable(ATTENDEES));

        scanQRPresenter.start();
    }

    @Test
    public void shouldDetachViewOnStop() {
        when(attendeeRepository.getAttendees(EVENT_ID, false))
                .thenReturn(Observable.fromIterable(ATTENDEES));

        scanQRPresenter.start();

        assertNotNull(scanQRPresenter.getView());

        scanQRPresenter.detach();

        assertTrue(scanQRPresenter.getDisposable().isDisposed());
    }

    @Test
    public void shouldStartScanOnCameraLoadedIfPermissionPresent() {
        when(scanQRView.hasCameraPermission()).thenReturn(true);

        scanQRPresenter.onCameraLoaded();

        verify(scanQRView).startScan();
    }

    @Test
    public void shouldAskPermissionOnCameraLoadedIfPermissionsAbsent() {
        when(scanQRView.hasCameraPermission()).thenReturn(false);

        scanQRPresenter.onCameraLoaded();

        verify(scanQRView).requestCameraPermission();
    }

    @Test
    public void shouldStartScanningOnPermissionGranted() {
        scanQRPresenter.cameraPermissionGranted(true);

        verify(scanQRView).startScan();
    }

    @Test
    public void shouldShowErrorOnPermissionDenied() {
        scanQRPresenter.cameraPermissionGranted(false);

        verify(scanQRView)
                .showPermissionError(matches("(.*permission.*denied.*)|(.*denied.*permission.*)"));
    }

    @Test
    public void shouldStopScanOnCameraDestroyed() {
        scanQRPresenter.onCameraDestroyed();

        verify(scanQRView).stopScan();
    }

    /** Checks that the flow of commands happen in order */
    @Test
    public void shouldFollowFlowOnImplicitPermissionGrant() {
        when(attendeeRepository.getAttendees(EVENT_ID, false))
                .thenReturn(Observable.fromIterable(ATTENDEES));
        when(scanQRView.hasCameraPermission()).thenReturn(true);

        scanQRPresenter.start();

        InOrder inOrder = inOrder(scanQRView);
        scanQRPresenter.onCameraLoaded();
        inOrder.verify(scanQRView).startScan();
    }

    @Test
    public void shouldShowProgressInBetweenImplicitPermissionGrant() {
        when(attendeeRepository.getAttendees(EVENT_ID, false))
                .thenReturn(Observable.fromIterable(ATTENDEES));
        when(scanQRView.hasCameraPermission()).thenReturn(true);

        scanQRPresenter.start();

        InOrder inOrder = inOrder(scanQRView);
        inOrder.verify(scanQRView).showProgress(true);
        scanQRPresenter.onCameraLoaded();
        scanQRPresenter.onScanStarted();
        inOrder.verify(scanQRView).showProgress(false);
    }

    @Test
    public void shouldFollowFlowOnImplicitPermissionDenyRequestGrant() {
        when(attendeeRepository.getAttendees(EVENT_ID, false))
                .thenReturn(Observable.fromIterable(ATTENDEES));
        when(scanQRView.hasCameraPermission()).thenReturn(false);

        scanQRPresenter.start();

        InOrder inOrder = inOrder(scanQRView);
        scanQRPresenter.onCameraLoaded();
        inOrder.verify(scanQRView).requestCameraPermission();
        scanQRPresenter.cameraPermissionGranted(true);
        inOrder.verify(scanQRView).startScan();
    }

    @Test
    public void shouldShowProgressInBetweenImplicitPermissionDenyRequestGrant() {
        when(attendeeRepository.getAttendees(EVENT_ID, false))
                .thenReturn(Observable.fromIterable(ATTENDEES));
        when(scanQRView.hasCameraPermission()).thenReturn(false);

        scanQRPresenter.start();

        InOrder inOrder = inOrder(scanQRView);
        inOrder.verify(scanQRView).showProgress(true);
        scanQRPresenter.onCameraLoaded();
        scanQRPresenter.cameraPermissionGranted(true);
        scanQRPresenter.onScanStarted();
        inOrder.verify(scanQRView).showProgress(false);
    }

    @Test
    public void shouldFollowFlowOnImplicitPermissionDenyRequestDeny() {
        when(attendeeRepository.getAttendees(EVENT_ID, false))
                .thenReturn(Observable.fromIterable(ATTENDEES));
        when(scanQRView.hasCameraPermission()).thenReturn(false);

        scanQRPresenter.start();

        InOrder inOrder = inOrder(scanQRView);
        scanQRPresenter.onCameraLoaded();
        inOrder.verify(scanQRView).requestCameraPermission();
        scanQRPresenter.cameraPermissionGranted(false);
        inOrder.verify(scanQRView)
                .showPermissionError(matches("(.*permission.*denied.*)|(.*denied.*permission.*)"));
    }

    @Test
    public void shouldShowProgressInBetweenImplicitPermissionDenyRequestDeny() {
        when(attendeeRepository.getAttendees(EVENT_ID, false))
                .thenReturn(Observable.fromIterable(ATTENDEES));
        when(scanQRView.hasCameraPermission()).thenReturn(false);

        scanQRPresenter.start();

        InOrder inOrder = inOrder(scanQRView);
        inOrder.verify(scanQRView).showProgress(true);
        scanQRPresenter.onCameraLoaded();
        scanQRPresenter.cameraPermissionGranted(false);
        inOrder.verify(scanQRView).showProgress(false);
    }

    private void sendBarcodeBurst(Barcode barcode) {
        scanQRPresenter.onBarcodeDetected(barcode);
        scanQRPresenter.onBarcodeDetected(barcode);
        scanQRPresenter.onBarcodeDetected(barcode);
    }

    @Test
    public void shouldSendSameBarcodeOnlyOnce() {
        scanQRPresenter.setAttendees(ATTENDEES);
        sendBarcodeBurst(BARCODE_1);

        verify(scanQRView, atMost(1)).onScannedAttendee(ATTENDEES.get(3));
    }

    @Test
    public void shouldSendOnlyDistinctBarcode() {
        scanQRPresenter.setAttendees(ATTENDEES);

        // Add bursts of barcodes to test only distinct gets transmitted
        sendBarcodeBurst(BARCODE_1);
        sendBarcodeBurst(BARCODE_2);
        sendBarcodeBurst(BARCODE_2);
        sendBarcodeBurst(BARCODE_1);
        sendBarcodeBurst(BARCODE_1);
        sendBarcodeBurst(BARCODE_2);
        sendBarcodeBurst(BARCODE_1);
        sendBarcodeBurst(BARCODE_1);

        InOrder inOrder = inOrder(scanQRView);

        inOrder.verify(scanQRView).onScannedAttendee(ATTENDEES.get(3));
        inOrder.verify(scanQRView).showMessage(R.string.invalid_ticket, false);
        inOrder.verify(scanQRView).onScannedAttendee(ATTENDEES.get(3));
        inOrder.verify(scanQRView).showMessage(R.string.invalid_ticket, false);
        inOrder.verify(scanQRView).onScannedAttendee(ATTENDEES.get(3));

        inOrder.verifyNoMoreInteractions();
    }

    private void sendNullInterleaved() {
        sendBarcodeBurst(BARCODE_1);
        sendBarcodeBurst(null);
        sendBarcodeBurst(BARCODE_1);
        sendBarcodeBurst(null);
        sendBarcodeBurst(BARCODE_2);
        sendBarcodeBurst(BARCODE_2);
        sendBarcodeBurst(null);
        sendBarcodeBurst(BARCODE_1);
        sendBarcodeBurst(null);
    }

    @Test
    public void shouldNotSendNullBarcode() {
        scanQRPresenter.setAttendees(ATTENDEES);
        sendNullInterleaved();

        InOrder inOrder = inOrder(scanQRView);

        inOrder.verify(scanQRView).onScannedAttendee(ATTENDEES.get(3));
        inOrder.verify(scanQRView).showMessage(R.string.invalid_ticket, false);
        inOrder.verify(scanQRView).onScannedAttendee(ATTENDEES.get(3));
        inOrder.verify(scanQRView, never()).showMessage(anyInt(), anyBoolean());
    }

    @Test
    public void shouldNotSendAnyBarcodeIfAttendeesAreNull() {
        sendNullInterleaved();

        verify(scanQRView, never()).onScannedAttendee(any(Attendee.class));
        verify(scanQRView, never()).showMessage(anyInt(), anyBoolean());
    }

    private void sendWrongNullInterleaved() {
        sendBarcodeBurst(BARCODE_2);
        sendBarcodeBurst(null);
        sendBarcodeBurst(BARCODE_2);
        sendBarcodeBurst(null);
        sendBarcodeBurst(BARCODE_2);
    }

    @Test
    public void shouldNotSendAttendeeOnWrongBarcodeDetection() {
        scanQRPresenter.setAttendees(ATTENDEES);
        sendWrongNullInterleaved();

        verify(scanQRView, never()).onScannedAttendee(any(Attendee.class));
    }

    @Test
    public void shouldSendAttendeeOnCorrectBarcodeDetection() {
        // Somehow the setting in setUp is not working, a workaround till fix is found
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());

        scanQRPresenter.setAttendees(ATTENDEES);

        BARCODE_1.displayValue = "test4-91";
        scanQRPresenter.onBarcodeDetected(BARCODE_1);

        verify(scanQRView).onScannedAttendee(ATTENDEES.get(3));
    }
}
