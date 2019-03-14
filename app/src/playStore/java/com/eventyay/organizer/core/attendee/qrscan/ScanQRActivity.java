package com.eventyay.organizer.core.attendee.qrscan;

import android.Manifest;
import android.Manifest.permission;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import com.eventyay.organizer.OrgaProvider;
import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseActivity;
import com.eventyay.organizer.core.attendee.checkin.AttendeeCheckInFragment;
import com.eventyay.organizer.core.attendee.qrscan.widget.CameraSourcePreview;
import com.eventyay.organizer.core.attendee.qrscan.widget.GraphicOverlay;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.ui.ViewUtils;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Lazy;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.reactivex.Completable;
import io.reactivex.Notification;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;

import static com.eventyay.organizer.ui.ViewUtils.showView;

@SuppressWarnings("PMD.TooManyMethods")
public class ScanQRActivity extends BaseActivity<ScanQRPresenter> implements ScanQRView, HasSupportFragmentInjector {

    public static final int PERM_REQ_CODE = 123;

    @BindView(R.id.preview)
    CameraSourcePreview preview;

    @BindView(R.id.graphicOverlay)
    GraphicOverlay<BarcodeGraphic> graphicOverlay;

    @BindView(R.id.barcodePanel)
    TextView barcodePanel;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    Lazy<ScanQRPresenter> presenterProvider;

    PublishSubject<Notification<Barcode>> barcodeEmitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_scan_qr);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ButterKnife.bind(this);

        barcodeEmitter = PublishSubject.create();
        barcodeDetector = createBarcodeDetector();

        requestCameraPermission();

        setCameraSource();

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        long eventId = getIntent().getLongExtra(MainActivity.EVENT_KEY, -1);
        if (eventId == -1) {
            Timber.d("No Event ID provided. Exiting ...");
            finish();
            return;
        }
        getPresenter().attach(eventId, this);
        getPresenter().start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (preview != null) {
            preview.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (getPresenter() != null) getPresenter().detach();
        if (preview != null) preview.release();
        if (barcodeDetector != null) barcodeDetector.release();
        compositeDisposable.dispose();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PERM_REQ_CODE)
            return;

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getPresenter().cameraPermissionGranted(true);
            //setCameraSource();
        } else {
            getPresenter().cameraPermissionGranted(false);
        }
    }

    private void setCameraSource() {

        if (hasCameraPermission()) {
            cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .setRequestedFps(15.0f)
                .setAutoFocusEnabled(true)
                .build();
        }
    }

    // Lifecycle methods end

    @Override
    public Lazy<ScanQRPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    // View Implementation Start

    @Override
    public boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERM_REQ_CODE);
    }

    @Override
    public void showPermissionError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onScannedAttendee(Attendee attendee) {
        getPresenter().pauseScan();
        showToggleDialog(attendee.getId());
    }

    @Override
    public void showBarcodePanel(boolean show) {
        showView(barcodePanel, show);
    }

    @Override
    public void showMessage(int stringRes, boolean matched) {
        barcodePanel.setText(getString(stringRes));
        ViewUtils.setTint(barcodePanel,
            ContextCompat.getColor(this, matched ? R.color.green_a400 : R.color.red_500)
        );
    }

    @Override
    public void showProgress(boolean show) {
        showView(progressBar, show);
    }

    @Override
    public void startScan() {
        setCameraSource();

        compositeDisposable.add(Completable.fromAction(() -> {
            try {
                startCameraSource();

                compositeDisposable.add(barcodeEmitter.subscribe(barcodeNotification -> {
                    if (barcodeNotification.isOnError()) {
                        getPresenter().onBarcodeDetected(null);
                    } else {
                        getPresenter().onBarcodeDetected(barcodeNotification.getValue());
                    }
                }));
            } catch (SecurityException se) {
                // Should never happen as we call this when we get our permission
                Timber.e("Should never happen. Check %s", getClass().getName());
            }
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(() -> getPresenter().onScanStarted()));
    }

    @Override
    public void stopScan() {
        preview.stop();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        if (cameraSource != null) {
            try {
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Timber.e("Unable to start camera source.");
                cameraSource.release();
            }
        }
    }

    public void showToggleDialog(long attendeeId) {
        AttendeeCheckInFragment bottomSheetDialogFragment = AttendeeCheckInFragment.newInstance(attendeeId);
        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
        bottomSheetDialogFragment.setOnCancelListener(() -> {
            ViewUtils.setTint(barcodePanel, ContextCompat.getColor(this, R.color.light_blue_a400));
            getPresenter().resumeScan();
        });
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    public BarcodeDetector createBarcodeDetector() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(OrgaProvider.context)
            .setBarcodeFormats(Barcode.QR_CODE)
            .build();

        barcodeDetector.setProcessor(
            new MultiProcessor.Builder<>(new BarcodeTrackerFactory(graphicOverlay, barcodeEmitter)).build());

        return barcodeDetector;
    }
}
