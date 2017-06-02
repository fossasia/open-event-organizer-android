package org.fossasia.openevent.app.qrscan;

import android.Manifest;
import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Display;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.EventDataRepository;
import org.fossasia.openevent.app.data.UtilModel;
import org.fossasia.openevent.app.data.contract.IEventDataRepository;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.event.attendees.AttendeesFragment;
import org.fossasia.openevent.app.events.EventListActivity;
import org.fossasia.openevent.app.qrscan.contract.IScanQRPresenter;
import org.fossasia.openevent.app.qrscan.contract.IScanQRView;
import org.fossasia.openevent.app.utils.Constants;
import org.fossasia.openevent.app.utils.ViewUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
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

    @BindView(R.id.frame_corner_00)
    FrameLayout frameCorner00;

    @BindView(R.id.frame_corner_01)
    FrameLayout frameCorner01;

    @BindView(R.id.frame_corner_10)
    FrameLayout frameCorner10;

    @BindView(R.id.frame_corner_11)
    FrameLayout frameCorner11;

    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;

    private IScanQRPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        IUtilModel utilModel = new UtilModel(getApplicationContext());
        IEventDataRepository eventDataRepository = new EventDataRepository(utilModel);

        ButterKnife.bind(this);

        setFrames();

        long eventId = getIntent().getLongExtra(EventListActivity.EVENT_KEY, -1);

        if (eventId == -1) {
            Timber.d("No Event ID provided. Exiting ...");
            finish();
            return;
        }

        presenter = new ScanQRPresenter(eventId, this, eventDataRepository);
        presenter.attach();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (presenter != null) presenter.detach();

        if (surfaceView != null) surfaceView.removeCallbacks(() -> Timber.d("Removed"));
        if (barcodeDetector != null) barcodeDetector.release();
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

    /**
     * A method sets frame corners proportional to a screen dimensions.
     * Reference screen width is taken as 480 pixels and found suitable dimensions for
     * frame corners.
     */
    private void setFrames() {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = size.x < size.y ? size.x : size.y;
        int frameCornerWidth = ViewUtils.convertDpToPixel(50 * width / 480, this);
        int marginFromCenter = ViewUtils.convertDpToPixel(160 * width / 480, this);

        //setting layout params to frame corners
        CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(frameCornerWidth, frameCornerWidth);
        lp.gravity = Gravity.CENTER;
        lp.setMargins(0, 0, marginFromCenter, marginFromCenter);
        frameCorner00.setLayoutParams(lp);
        lp = new CoordinatorLayout.LayoutParams(frameCornerWidth, frameCornerWidth);
        lp.gravity = Gravity.CENTER;
        lp.setMargins(marginFromCenter, 0, 0, marginFromCenter);
        frameCorner01.setLayoutParams(lp);
        lp = new CoordinatorLayout.LayoutParams(frameCornerWidth, frameCornerWidth);
        lp.gravity = Gravity.CENTER;
        lp.setMargins(0, marginFromCenter, marginFromCenter, 0);
        frameCorner10.setLayoutParams(lp);
        lp = new CoordinatorLayout.LayoutParams(frameCornerWidth, frameCornerWidth);
        lp.gravity = Gravity.CENTER;
        lp.setMargins(marginFromCenter, marginFromCenter, 0, 0);
        frameCorner11.setLayoutParams(lp);

        //setting background

        Drawable[] drawables = {ResourcesCompat.getDrawable(getResources(), R.drawable.bordered_rectangle, null)};
        int px = ViewUtils.convertDpToPixel(2, this);
        LayerDrawable ld = new LayerDrawable(drawables);
        ld.setLayerInset(0, 0, 0, -px, -px);
        frameCorner00.setBackground(ld.mutate());

        ld = new LayerDrawable(drawables);
        ld.setLayerInset(0, -px, 0, 0, -px);
        frameCorner01.setBackground(ld.mutate());

        ld = new LayerDrawable(drawables);
        ld.setLayerInset(0, 0, -px, -px, 0);
        frameCorner10.setBackground(ld.mutate());

        ld = new LayerDrawable(drawables);
        ld.setLayerInset(0, -px, -px, 0, 0);
        frameCorner11.setBackground(ld.mutate());
    }

    private void buildBarcodeDetector() {
        barcodeDetector = new BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.QR_CODE)
            .build();

        //for when it detects the barcode activity
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // No action to be taken
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> barcodeSparseArray = detections.getDetectedItems();
                presenter.onBarcodeDetected(barcodeSparseArray.size() == 0 ? null : barcodeSparseArray.valueAt(0));
            }
        });
    }

    private void buildCameraSource() {
        cameraSource = new CameraSource
            .Builder(this, barcodeDetector)
            .setRequestedPreviewSize(640, 480)
            .setRequestedFps(15.0f)
            .setAutoFocusEnabled(true)
            .build();
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
        resultIntent.putExtra(Constants.SCANNED_ATTENDEE, attendee);
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
