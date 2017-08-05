package org.fossasia.openevent.app.module.attendee.qrscan;

import android.Manifest;
import android.Manifest.permission;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.app.di.component.DaggerBarcodeComponent;
import org.fossasia.openevent.app.common.app.di.module.AndroidModule;
import org.fossasia.openevent.app.common.app.di.module.BarcodeModule;
import org.fossasia.openevent.app.common.app.lifecycle.view.BaseActivity;
import org.fossasia.openevent.app.common.data.models.Attendee;
import org.fossasia.openevent.app.common.utils.ui.ViewUtils;
import org.fossasia.openevent.app.module.attendee.checkin.AttendeeCheckInFragment;
import org.fossasia.openevent.app.module.attendee.qrscan.contract.IScanQRPresenter;
import org.fossasia.openevent.app.module.attendee.qrscan.contract.IScanQRView;
import org.fossasia.openevent.app.module.attendee.qrscan.widget.CameraSourcePreview;
import org.fossasia.openevent.app.module.attendee.qrscan.widget.GraphicOverlay;
import org.fossasia.openevent.app.module.main.MainActivity;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Lazy;
import io.reactivex.Completable;
import io.reactivex.Notification;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;

import static org.fossasia.openevent.app.common.utils.ui.ViewUtils.showView;

public class ScanQRActivity extends BaseActivity<IScanQRPresenter> implements IScanQRView {

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
    Lazy<CameraSource> cameraSourceProvider;
    @Inject
    Lazy<BarcodeDetector> barcodeDetectorProvider;

    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;

    private Disposable disposable;

    @Inject
    Lazy<IScanQRPresenter> presenterProvider;

    @Inject
    @Named("barcodeEmitter")
    PublishSubject<Notification<Barcode>> barcodeEmitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_scan_qr);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ButterKnife.bind(this);

        DaggerBarcodeComponent.builder()
            .androidModule(new AndroidModule())
            .barcodeModule(new BarcodeModule(graphicOverlay))
            .build()
            .inject(this);

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
        if (disposable != null) disposable.dispose();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PERM_REQ_CODE)
            return;

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getPresenter().cameraPermissionGranted(true);
        } else {
            getPresenter().cameraPermissionGranted(false);
        }
    }

    // Lifecycle methods end

    @Override
    public Lazy<IScanQRPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    public int getLoaderId() {
        return R.layout.activity_scan_qr;
    }

    private void buildBarcodeDetector() {
        barcodeDetector = barcodeDetectorProvider.get();
    }

    private void buildCameraSource() {
        cameraSource = cameraSourceProvider.get();
    }

    // View Implementation Start

    @Override
    public void loadCamera() {
        Completable.fromAction(() -> {
            buildBarcodeDetector();
            buildCameraSource();
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(() -> getPresenter().onCameraLoaded());
    }

    @Override
    public boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(ScanQRActivity.this, permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void requestCameraPermission() {
        ActivityCompat.requestPermissions(ScanQRActivity.this, new String[]{Manifest.permission.CAMERA}, PERM_REQ_CODE);
    }

    @Override
    public void showPermissionError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onScannedAttendee(Attendee attendee) {
        getPresenter().pauseScan();
        ViewUtils.setTint(barcodePanel, ContextCompat.getColor(this, R.color.green_a400));
        showToggleDialog(attendee.getId());
    }

    @Override
    public void showBarcodePanel(boolean show) {
        showView(barcodePanel, show);
    }

    @Override
    public void showBarcodeData(String data) {
        barcodePanel.setText(data);
    }

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(progressBar, show);
    }

    @Override
    public void startScan() {
        Completable.fromAction(() -> {
            try {
                startCameraSource();

                disposable = barcodeEmitter.subscribe(barcodeNotification -> {
                    if (barcodeNotification.isOnError()) {
                        getPresenter().onBarcodeDetected(null);
                    } else {
                        getPresenter().onBarcodeDetected(barcodeNotification.getValue());
                    }
                });
            } catch (SecurityException se) {
                // Should never happen as we call this when we get our permission
                Timber.e("Should never happen. Check %s", getClass().getName());
            }
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(() -> getPresenter().onScanStarted());
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
                cameraSource = null;
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
}
