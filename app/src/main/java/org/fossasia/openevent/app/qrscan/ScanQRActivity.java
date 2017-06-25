package org.fossasia.openevent.app.qrscan;

import android.Manifest;
import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.event.attendees.AttendeesFragment;
import org.fossasia.openevent.app.events.EventListActivity;
import org.fossasia.openevent.app.qrscan.contract.IScanQRPresenter;
import org.fossasia.openevent.app.qrscan.contract.IScanQRView;
import org.fossasia.openevent.app.utils.Constants;
import org.fossasia.openevent.app.utils.ViewUtils;

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

import static org.fossasia.openevent.app.utils.ViewUtils.showView;

public class ScanQRActivity extends AppCompatActivity implements IScanQRView {

    public static final int PERM_REQ_CODE = 123;

    @BindView(R.id.svScanView)
    SurfaceView surfaceView;

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
    IScanQRPresenter presenter;

    @Inject
    @Named("barcodeEmitter")
    PublishSubject<Notification<Barcode>> barcodeEmitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        OrgaApplication
            .getAppComponent(this)
            .inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ButterKnife.bind(this);

        long eventId = getIntent().getLongExtra(EventListActivity.EVENT_KEY, -1);

        if (eventId == -1) {
            Timber.d("No Event ID provided. Exiting ...");
            finish();
            return;
        }

        presenter.attach(eventId, this);
        presenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (presenter != null) presenter.detach();

        if (surfaceView != null) surfaceView.removeCallbacks(() -> Timber.d("Removed"));
        if (barcodeDetector != null) barcodeDetector.release();
        if (disposable != null) disposable.dispose();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERM_REQ_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.cameraPermissionGranted(true);
                } else {
                    presenter.cameraPermissionGranted(false);
                }
            }
        }

    }

    // Lifecycle methods end

    private void buildBarcodeDetector() {
        barcodeDetector = barcodeDetectorProvider.get();
    }

    private void buildCameraSource() {
        cameraSource = cameraSourceProvider.get();
    }

    private void waitForSurface() {
        // Get callback from SurfaceView
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Timber.d("surfaceCreated");
                presenter.onCameraLoaded();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                // No Action required
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Timber.d("surfaceDestroyed");
                presenter.onCameraDestroyed();
            }
        });
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
            .subscribe(() -> {
                waitForSurface();

                if(!surfaceView.getHolder().isCreating()) {
                    Timber.d("Surface already created");
                    presenter.onCameraLoaded();
                }
            });
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
        ViewUtils.setTint(barcodePanel, ContextCompat.getColor(this, R.color.green_a400));

        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constants.SCANNED_ATTENDEE, attendee.getId());
        setResult(AttendeesFragment.REQ_CODE, resultIntent);
        finish();
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
    public void showProgressBar(boolean show) {
        ViewUtils.showView(progressBar, show);
    }

    @Override
    public void startScan() {
        Completable.fromAction(() -> {
            try {
                cameraSource.start(surfaceView.getHolder());

                disposable = barcodeEmitter.subscribe(barcodeNotification -> {
                    if (barcodeNotification.isOnError()) {
                        presenter.onBarcodeDetected(null);
                    } else {
                        presenter.onBarcodeDetected(barcodeNotification.getValue());
                    }
                });
            } catch (IOException ioe) {
                Timber.e("Exception while starting camera");
            } catch (SecurityException se) {
                // Should never happen as we call this when we get our permission
                Timber.e("Should never happen. Check %s", getClass().getName());
            }
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(() -> presenter.onScanStarted());
    }

    @Override
    public void stopScan() {
        cameraSource.stop();
    }
}
