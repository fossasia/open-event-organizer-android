package com.eventyay.organizer.core.attendee.qrscan;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.eventyay.organizer.R;
import com.eventyay.organizer.core.attendee.ScanQRView;
import com.eventyay.organizer.core.attendee.checkin.AttendeeCheckInFragment;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.ui.ViewUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static com.eventyay.organizer.ui.ViewUtils.showView;

@SuppressWarnings("PMD.TooManyMethods")
public class ScanQRActivity extends DaggerAppCompatActivity implements ScanQRView, HasActivityInjector {

    public static final int PERM_REQ_CODE = 123;
    private static final int REQUEST_CODE_QR_SCAN = 101;

    @BindView(R.id.barcodePanel)
    TextView barcodePanel;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.startScanningAgain)
    Button startScanningAgain;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    private ScanQRViewModel scanQRViewModel;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        scanQRViewModel = ViewModelProviders.of(this, viewModelFactory).get(ScanQRViewModel.class);

        setContentView(R.layout.activity_scan_qr);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ButterKnife.bind(this);

        onCameraLoaded();
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

        scanQRViewModel.getProgress().observe(this, this::showProgress);
        scanQRViewModel.getError().observe(this, this::showPermissionError);
        scanQRViewModel.getMessage().observe(this, this::showMessage);
        scanQRViewModel.getTint().observe(this, this::setTint);
        scanQRViewModel.getShowBarcodePanelLiveData().observe(this, this::showBarcodePanel);
        scanQRViewModel.getOnScannedAttendeeLiveData().observe(this, this::onScannedAttendee);
        scanQRViewModel.loadAttendees();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        compositeDisposable.dispose();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PERM_REQ_CODE)
            return;

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            cameraPermissionGranted(true);
        } else {
            cameraPermissionGranted(false);
        }
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
        showToggleDialog(attendee.getId());
    }

    @Override
    public void showBarcodePanel(boolean show) {
        showView(barcodePanel, show);
    }

    @Override
    public void showMessage(int stringRes) {
        barcodePanel.setText(getString(stringRes));
    }

    @Override
    public void setTint(boolean matched) {
        ViewUtils.setTint(barcodePanel,
            ContextCompat.getColor(this, matched ? R.color.green_a400 : R.color.red_500)
        );
    }

    @Override
    public void showProgress(boolean show) {
        showView(progressBar, show);
    }

    public void onCameraLoaded() {
        if (hasCameraPermission()) {
            startScan();
        } else {
            requestCameraPermission();
        }
    }

    public void cameraPermissionGranted(boolean granted) {
        if (granted) {
            startScan();
        } else {
            showProgress(false);
            showPermissionError("User denied permission");
        }
    }

    @Override
    public void startScan() {
        Intent i = new Intent(ScanQRActivity.this, QrCodeActivity.class);
        startActivityForResult(i, REQUEST_CODE_QR_SCAN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (intent == null)
                return;

            scanQRViewModel.processBarcode(intent.getStringExtra
                ("com.blikoon.qrcodescanner.got_qr_scan_relult"));

        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    public void showToggleDialog(long attendeeId) {
        AttendeeCheckInFragment bottomSheetDialogFragment = AttendeeCheckInFragment.newInstance(attendeeId);
        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
        bottomSheetDialogFragment.setOnCancelListener(() -> {
            ViewUtils.setTint(barcodePanel, ContextCompat.getColor(this, R.color.light_blue_a400));
            showBarcodePanel(false);
            startScan();
        });
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}
