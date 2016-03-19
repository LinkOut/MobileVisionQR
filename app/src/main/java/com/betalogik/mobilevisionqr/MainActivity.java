package com.betalogik.mobilevisionqr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView txtInfo;
    private SurfaceView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Getting view refs
        cameraView = (SurfaceView)findViewById(R.id.cameraView);
        txtInfo = (TextView)findViewById(R.id.txtInfo);

        //Creating a new BarcodeDetector setting the BarcodeFormat to QR_CODE
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();

        //Creating a new CameraSource, passing the dimensions of the surfaceview defined on the layout xml
        final CameraSource cameraSource = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(640, 480).build();

        //Adding a callback to surface, so we know when to stop the camera (surfaceDestroyed) and when to start it (surfaceCreated)
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    //Starting the camera
                    //TODO Android 6 camera permission needed!
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ie) {
                    Log.e("CAMERA START ERROR", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                //Stopping the camera
                cameraSource.stop();
            }
        });

        //Setting a new processor to our BarcodeDetector object
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {}

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                //Actually we got some data
                final SparseArray<Barcode> barcodeDetections = detections.getDetectedItems();
                //Checking that our scanner actually detected something
                if (barcodeDetections.size() != 0) {
                    txtInfo.post(new Runnable() {// Using post from TextView because only the original thread that created a view hierarchy can touch its views
                        public void run() {
                            txtInfo.setText("Detected: " + barcodeDetections.valueAt(0).displayValue); //Updating the TestView
                        }
                    });
                }

            }
        });


    }
}
