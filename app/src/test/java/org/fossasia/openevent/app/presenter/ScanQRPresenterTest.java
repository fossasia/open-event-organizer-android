package org.fossasia.openevent.app.presenter;

import com.google.android.gms.vision.barcode.Barcode;

import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.data.models.Order;
import org.fossasia.openevent.app.data.repository.contract.IAttendeeRepository;
import org.fossasia.openevent.app.qrscan.ScanQRPresenter;
import org.fossasia.openevent.app.qrscan.contract.IScanQRView;
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
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ScanQRPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    IScanQRView scanQRView;

    @Mock
    IAttendeeRepository attendeeRepository;

    private int eventId = 32;
    private ScanQRPresenter scanQRPresenter;

    private List<Attendee> attendees = Arrays.asList(
        new Attendee(12),
        new Attendee(34),
        new Attendee(56),
        new Attendee(91),
        new Attendee(29),
        new Attendee(90),
        new Attendee(123)
    );

    private List<Order> orders = Arrays.asList(
        new Order("test1"),
        null,
        new Order("test3"),
        new Order("test4"),
        new Order("test5"),
        null,
        new Order("test6")
    );

    private Barcode barcode1 = new Barcode();
    private Barcode barcode2 = new Barcode();

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        scanQRPresenter = new ScanQRPresenter(attendeeRepository);
        scanQRPresenter.attach(eventId, scanQRView);

        barcode1.displayValue = "Test Barcode 1";
        barcode2.displayValue = "Test Barcode 2";

        for(int i = 0; i < attendees.size(); i++)
            attendees.get(i).setOrder(orders.get(i));
    }

    @Before
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadAttendeesAutomatically() {
        when(attendeeRepository.getAttendees(eventId, false))
            .thenReturn(Observable.fromIterable(attendees));

        scanQRPresenter.start();

        verify(attendeeRepository).getAttendees(eventId, false);
    }

    @Test
    public void shouldLoadCameraAutomatically() {
        when(attendeeRepository.getAttendees(eventId, false))
            .thenReturn(Observable.fromIterable(attendees));

        scanQRPresenter.start();

        verify(scanQRView).loadCamera();
    }

    @Test
    public void shouldDetachViewOnStop() {
        when(attendeeRepository.getAttendees(eventId, false))
            .thenReturn(Observable.fromIterable(attendees));

        scanQRPresenter.start();

        assertNotNull(scanQRPresenter.getView());

        scanQRPresenter.detach();

        assertNull(scanQRPresenter.getView());
    }

    @Test
    public void shouldNotAccessViewAfterDetach() {
        scanQRPresenter.detach();

        scanQRPresenter.start();
        scanQRPresenter.onCameraLoaded();
        scanQRPresenter.cameraPermissionGranted(false);
        scanQRPresenter.cameraPermissionGranted(true);
        scanQRPresenter.onScanStarted();
        scanQRPresenter.onBarcodeDetected(barcode2);
        scanQRPresenter.onCameraDestroyed();

        verifyZeroInteractions(scanQRView);
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
        when(attendeeRepository.getAttendees(eventId, false))
            .thenReturn(Observable.fromIterable(attendees));
        when(scanQRView.hasCameraPermission()).thenReturn(true);

        scanQRPresenter.start();

        InOrder inOrder = inOrder(scanQRView);
        inOrder.verify(scanQRView).loadCamera();
        scanQRPresenter.onCameraLoaded();
        inOrder.verify(scanQRView).startScan();
    }

    @Test
    public void shouldShowProgressInBetweenImplicitPermissionGrant() {
        when(attendeeRepository.getAttendees(eventId, false))
            .thenReturn(Observable.fromIterable(attendees));
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
        when(attendeeRepository.getAttendees(eventId, false))
            .thenReturn(Observable.fromIterable(attendees));
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
        when(attendeeRepository.getAttendees(eventId, false))
            .thenReturn(Observable.fromIterable(attendees));
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
        when(attendeeRepository.getAttendees(eventId, false))
            .thenReturn(Observable.fromIterable(attendees));
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
        when(attendeeRepository.getAttendees(eventId, false))
            .thenReturn(Observable.fromIterable(attendees));
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
        scanQRPresenter.setAttendees(attendees);
        sendBarcodeBurst(barcode1);

        verify(scanQRView, atMost(1)).showBarcodeData(barcode1.displayValue);
    }

    @Test
    public void shouldSendOnlyDistinctBarcode() {
        scanQRPresenter.setAttendees(attendees);

        // Add bursts of barcodes to test only distinct gets transmitted
        sendBarcodeBurst(barcode1);
        sendBarcodeBurst(barcode2);
        sendBarcodeBurst(barcode2);
        sendBarcodeBurst(barcode1);
        sendBarcodeBurst(barcode1);
        sendBarcodeBurst(barcode2);
        sendBarcodeBurst(barcode1);
        sendBarcodeBurst(barcode1);

        InOrder inOrder = inOrder(scanQRView);

        inOrder.verify(scanQRView).showBarcodeData(barcode1.displayValue);
        inOrder.verify(scanQRView).showBarcodeData(barcode2.displayValue);
        inOrder.verify(scanQRView).showBarcodeData(barcode1.displayValue);
        inOrder.verify(scanQRView).showBarcodeData(barcode2.displayValue);
        inOrder.verify(scanQRView).showBarcodeData(barcode1.displayValue);
        inOrder.verifyNoMoreInteractions();
    }

    private void sendNullInterleaved() {
        sendBarcodeBurst(barcode1);
        sendBarcodeBurst(null);
        sendBarcodeBurst(barcode1);
        sendBarcodeBurst(null);
        sendBarcodeBurst(barcode2);
        sendBarcodeBurst(barcode2);
        sendBarcodeBurst(null);
        sendBarcodeBurst(barcode1);
        sendBarcodeBurst(null);
    }

    @Test
    public void shouldNotSendNullBarcode() {
        scanQRPresenter.setAttendees(attendees);
        sendNullInterleaved();

        InOrder inOrder = inOrder(scanQRView);

        inOrder.verify(scanQRView).showBarcodeData(barcode1.displayValue);
        inOrder.verify(scanQRView).showBarcodeData(barcode2.displayValue);
        inOrder.verify(scanQRView).showBarcodeData(barcode1.displayValue);
        inOrder.verify(scanQRView, never()).showBarcodeData(anyString());
    }

    @Test
    public void shouldNotSendAnyBarcodeIfAttendeesAreNull() {
        sendNullInterleaved();

        verify(scanQRView, never()).onScannedAttendee(any(Attendee.class));
    }

    @Test
    public void shouldNotSendAttendeeOnWrongBarcodeDetection() {
        scanQRPresenter.setAttendees(attendees);
        sendNullInterleaved();

        verify(scanQRView, never()).onScannedAttendee(any(Attendee.class));
    }

    @Test
    public void shouldSendAttendeeOnCorrectBarcodeDetection() {
        // Somehow the setting in setUp is not working, a workaround till fix is found
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());

        scanQRPresenter.setAttendees(attendees);

        barcode1.displayValue = "test4-91";
        scanQRPresenter.onBarcodeDetected(barcode1);

        verify(scanQRView).onScannedAttendee(attendees.get(3));
    }

}
