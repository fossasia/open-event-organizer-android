package org.fossasia.openevent.app.unit.presenter;

import com.google.android.gms.vision.barcode.Barcode;

import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.data.models.Order;
import org.fossasia.openevent.app.common.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.module.attendee.qrscan.ScanQRPresenter;
import org.fossasia.openevent.app.module.attendee.qrscan.contract.IScanQRView;
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

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
@SuppressWarnings("PMD.TooManyMethods")
public class ScanQRPresenterTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private IScanQRView scanQRView;
    @Mock private IAttendeeRepository attendeeRepository;

    private static final long EVENT_ID = 32;
    private ScanQRPresenter scanQRPresenter;

    private static final List<Attendee> ATTENDEES = Arrays.asList(
        new Attendee(12),
        new Attendee(34),
        new Attendee(56),
        new Attendee(91),
        new Attendee(29),
        new Attendee(90),
        new Attendee(123)
    );

    private static final List<Order> ORDERS = Arrays.asList(
        new Order("test1"),
        null,
        new Order("test3"),
        new Order("test4"),
        new Order("test5"),
        null,
        new Order("test6")
    );

    private static final Barcode BARCODE_1 = new Barcode();
    private static final Barcode BARCODE_2 = new Barcode();

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        scanQRPresenter = new ScanQRPresenter(attendeeRepository);
        scanQRPresenter.attach(EVENT_ID, scanQRView);

        BARCODE_1.displayValue = "Test Barcode 1";
        BARCODE_2.displayValue = "Test Barcode 2";

        for (int i = 0; i < ATTENDEES.size(); i++)
            ATTENDEES.get(i).setOrder(ORDERS.get(i));
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

        verify(scanQRView).loadCamera();
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

        verify(scanQRView).showPermissionError(matches("(.*permission.*denied.*)|(.*denied.*permission.*)"));
    }

    @Test
    public void shouldStopScanOnCameraDestroyed() {
        scanQRPresenter.onCameraDestroyed();

        verify(scanQRView).stopScan();
    }

    /**
     * Checks that the flow of commands happen in order
     */
    @Test
    public void shouldFollowFlowOnImplicitPermissionGrant() {
        when(attendeeRepository.getAttendees(EVENT_ID, false))
            .thenReturn(Observable.fromIterable(ATTENDEES));
        when(scanQRView.hasCameraPermission()).thenReturn(true);

        scanQRPresenter.start();

        InOrder inOrder = inOrder(scanQRView);
        inOrder.verify(scanQRView).loadCamera();
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
        inOrder.verify(scanQRView).loadCamera();
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
        inOrder.verify(scanQRView).loadCamera();
        scanQRPresenter.onCameraLoaded();
        inOrder.verify(scanQRView).requestCameraPermission();
        scanQRPresenter.cameraPermissionGranted(false);
        inOrder.verify(scanQRView).showPermissionError(matches("(.*permission.*denied.*)|(.*denied.*permission.*)"));
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

        verify(scanQRView, atMost(1)).showBarcodeData(BARCODE_1.displayValue);
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

        inOrder.verify(scanQRView).showBarcodeData(BARCODE_1.displayValue);
        inOrder.verify(scanQRView).showBarcodeData(BARCODE_2.displayValue);
        inOrder.verify(scanQRView).showBarcodeData(BARCODE_1.displayValue);
        inOrder.verify(scanQRView).showBarcodeData(BARCODE_2.displayValue);
        inOrder.verify(scanQRView).showBarcodeData(BARCODE_1.displayValue);
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

        inOrder.verify(scanQRView).showBarcodeData(BARCODE_1.displayValue);
        inOrder.verify(scanQRView).showBarcodeData(BARCODE_2.displayValue);
        inOrder.verify(scanQRView).showBarcodeData(BARCODE_1.displayValue);
        inOrder.verify(scanQRView, never()).showBarcodeData(anyString());
    }

    @Test
    public void shouldNotSendAnyBarcodeIfAttendeesAreNull() {
        sendNullInterleaved();

        verify(scanQRView, never()).onScannedAttendee(any(Attendee.class));
    }

    @Test
    public void shouldNotSendAttendeeOnWrongBarcodeDetection() {
        scanQRPresenter.setAttendees(ATTENDEES);
        sendNullInterleaved();

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
