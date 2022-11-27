package com.example.camera_api;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.widget.*;
import android.widget.FrameLayout;
import android.hardware.Camera.PictureCallback;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "CamTestActivity";

    private Camera mCamera;
    private CameraPreview mPreview;
    private ImageView capturedImageHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button) findViewById(R.id.button_capture);
        capturedImageHolder = (ImageView) findViewById(R.id.captured_img);
        //Create an instance of Camera
        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(180);
        //Create our Preview view and set is as the content of our activity
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, pictureCallback);
            }
        });
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use of does not exist)
        }
        return c;
    }

    PictureCallback pictureCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            int w = bitmap.getWidth()/2;
            int h = bitmap.getHeight()/2;

            Matrix mtx = new Matrix();
            mtx.postRotate(180);
            bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);


            if (bitmap == null) {
                Toast.makeText(MainActivity.this, "Capture image is empty", Toast.LENGTH_LONG).show();
            }
            capturedImageHolder.setImageBitmap(scaleDownBitmapImage(rotatedBitmap, 450, 300));
        }
    };

    private Bitmap scaleDownBitmapImage(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        return resizedBitmap;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder(); // if you are using MediaRecorder, release it first
        releaseCamera(); // release the camera immediately on pause event
    }

    private void releaseMediaRecorder() {
        mCamera.lock(); // lock camera for later use
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}

