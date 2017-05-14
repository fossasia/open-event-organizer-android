package org.fossasia.openevent.app.ui.activity;

import android.Manifest;
import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.models.Attendee;
import org.fossasia.openevent.app.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanQRActivity extends AppCompatActivity {
    public static final int PERM_REQ_CODE = 123;
    public static final String TAG = "ScanQRActivity";
    @BindView(R.id.svScanView)
    SurfaceView surfaceView;
    CameraSource cameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        ButterKnife.bind(this);

        BarcodeDetector barcodeDetector =
            new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource
            .Builder(this, barcodeDetector)
            .setRequestedPreviewSize(640, 480)
            .setAutoFocusEnabled(true)
            .build();
        //for when it detects the barcode activity
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // No action to be taken
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    String barcode = barcodes.valueAt(0).displayValue;
                    Log.d(TAG, "receiveDetections: not matched" + barcode);
                    ArrayList<Attendee> attendeeDetailses = AttendeeListActivity.attendeeArrayList;
                    int index = -1;
                    for (Attendee thisAttendee : attendeeDetailses) {
                        index++;
                        Log.d(TAG, "receiveDetections: " + thisAttendee.getOrder().getIdentifier().equals(barcode));
                        String identifier = thisAttendee.getOrder().getIdentifier() + "-" + thisAttendee.getId();
                        if (identifier.equals(barcode)) {
                            Log.d(TAG, "receiveDetections: ");
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra(Constants.SCANNED_IDENTIFIER, thisAttendee.getOrder().getIdentifier());
                            resultIntent.putExtra(Constants.SCANNED_ID, thisAttendee.getId());
                            resultIntent.putExtra(Constants.SCANNED_INDEX, index);
                            setResult(AttendeeListActivity.REQ_CODE, resultIntent);
                            finish();

                        }
                    }
                }

            }
        });

        // Get callback from SurfaceView
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d(TAG, "surfaceCreated: ");

                int permission = ContextCompat.checkSelfPermission(ScanQRActivity.this, Manifest.permission.CAMERA);
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    try {
                        cameraSource.start(surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(
                        ScanQRActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        PERM_REQ_CODE);
                }
            }


            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERM_REQ_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(permission.CAMERA)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        try {
                            if (ActivityCompat.checkSelfPermission(this, permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            cameraSource.start(surfaceView.getHolder());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(this, "user deined permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
