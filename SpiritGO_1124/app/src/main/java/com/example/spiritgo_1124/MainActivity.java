package com.example.spiritgo_1124;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.opencv.android.OpenCVLoader;

import java.io.IOException;




public class MainActivity extends AppCompatActivity implements ButtonListener {



    private static final String TAG = "CamTestActivity";

//    private Camera mCamera;
//    private CameraPreview mPreview;
//    ImageView capturedImageHolder;

    FrameLayout preview;
//    ImageView capturedView;
    CameraView cameraView;
    // 카메라 붙이는 중이었음

    TextView tv, tv2;
    String str="";

    ButtonDriver mButtonDriver;
    boolean mThreadRun=true;

    LEDDriver mLedDriver;
    int mLedCount = 8;

    SegmentDriver mSegmentDriver;
    boolean mSegThreadRun;
    SegmentThread mSegThread;
    int mSegCount;

    float mLedPushed;

    //CameraThread mCameraThread;
    public int mAverage;

    static{
        if(!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV is not loaded!");
        }
        else{
            Log.d(TAG, "OpenCV is loaded successfully!");

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //// camera ////
        Button btn = (Button) findViewById(R.id.button_catch);
        //Create an instance of Camera
        //Create our Preview view and set is as the content of our activity
        //OpenCVLoader.initDebug();
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        cameraView = new CameraView(this, preview, null);

        cameraView.pictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                camera.stopPreview();

                //할거 들어가기

                int[] num = ImageDriver.getResult(data);
                tv2.setText(num[0]);
                camera.startPreview();
            }
        };
        //mCameraThread = new CameraThread();
        //mCameraThread.start();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.takePicture();
            }
        });

        //// button ////
        tv = (TextView) findViewById(R.id.txt);
        tv2 = (TextView) findViewById(R.id.tvName);

        mButtonDriver = new ButtonDriver();
        mButtonDriver.setListener(this);
        if(mButtonDriver.open("/dev/sm9s5422_interrupt")<0){
            Toast.makeText(MainActivity.this,"Driver Open Failed", Toast.LENGTH_SHORT).show();
        }

        //// LED ////
        mLedDriver = new LEDDriver();
        if(mLedDriver.open("/dev/sm9s5422_led")<0){
            Toast.makeText(MainActivity.this, "LEDDriver Open Failed", Toast.LENGTH_SHORT).show();
        }
        mLedDriver.write(mLedCount);
        // 버튼 클릭시 onChanged 필요한가?

        //// segment ////
        mSegmentDriver = new SegmentDriver();
        if(mSegmentDriver.open("/dev/sm9s5422_segment")<0){
            Toast.makeText(MainActivity.this,"Driver Open Failed", Toast.LENGTH_SHORT).show();
        }
        //mSegmentDriver.write(0);

        //MyView mv = (MyView)findViewById(R.id.mv);
        // 정령 잡을 때 점수 write하기 추가해야
    }

    /**
     * A safe way to get an instance of the Camera object.
     */


//    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
//        @Override
//        public void onPictureTaken(byte[] data, Camera camera) {
//            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//            int w = bitmap.getWidth()/2;
//            int h = bitmap.getHeight()/2;
//
//            Matrix mtx = new Matrix();
//            mtx.postRotate(180);
//            bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
//            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
//
//
//
////            Imgproc.cvtColor(InputFrame.rgba(),  , Imgproc.COLOR_RGB2HSV);
////            Core.inRange(candies, new Scalar(0, 0, 0), new Scalar(0, 0, 32), dst)
////            //dst를 최종적으로 비트맵으로 변환하여 ImageView에 나타냄
////            binding.dst.setImageBitmap(bitmapUtil.bitmapFrom(dst))
//
//            if (bitmap == null) {
//                Toast.makeText(MainActivity.this, "Capture image is empty", Toast.LENGTH_LONG).show();
//            }
//            capturedImageHolder.setImageBitmap(scaleDownBitmapImage(rotatedBitmap, 450, 300));
//        }
//    };



    private Bitmap scaleDownBitmapImage(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        return resizedBitmap;
    }

    public Handler handler = new Handler((Looper.getMainLooper())){
        public void handleMessage(Message msg)  {
            switch(msg.arg1){
                case 1:
                    tv.setText("Up");
                    mSegCount = 111;
                    break;
                case 2:
                    tv.setText("Down");
                    mSegCount = 222;
                    break;
                case 3:
                    tv.setText("Left");
                    mSegCount = 333;
                    break;
                case 4:
                    tv.setText("Right");
                    mSegCount = 980722;
                    break;
                case 5:
                    tv.setText("Center");

                    // 중복 입력 방지
                    float now = (float) System.nanoTime()/1_000_000L;
                    if ( now - mLedPushed > 200 ){
                        mLedCount --;
                        mLedPushed = (float) System.nanoTime()/1_000_000L;
                    }
                    mLedDriver.write(mLedCount);
                    break;
            }
        }
    };

    private class SegmentThread extends Thread{
        @Override
        public void run(){
            super.run();
            while(mThreadRun){
                mSegmentDriver.write(mSegCount);

            }
        }
    }


    class MyView extends View{
        //생성자에 인수를 세개 준다. 생성자를 세개 다 만들어줘야한다. 생성자들앞에 public붙여야함
        public MyView(Context context){
            super(context);
        }
        public MyView(Context context, AttributeSet att){
            super(context,att);
        }
        public MyView(Context context,AttributeSet att, int a){
            super(context,att,a);
        }

        //사각형 좌표값의 값을 변수로 설정!
        int x1=100,y1=100,x2=300,y2=300;
        @Override //부모가 가진 onDraw와 오버라이딩
        public void onDraw(Canvas c){
            Paint paint= new Paint();
            paint.setColor(Color.DKGRAY);
            c.drawRect(x1, y1, x2, y2, paint);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mButtonDriver.close();
        mLedDriver.close();
        mSegThreadRun = false;
        mSegThread = null;
        mSegmentDriver.close();
    }

    @Override
    protected void onResume(){
        mButtonDriver = new ButtonDriver();
        mButtonDriver.setListener(this);
        if(mButtonDriver.open("/dev/sm9s5422_interrupt")<0){
            Toast.makeText(MainActivity.this,"ButtonDriver Open Failed", Toast.LENGTH_SHORT).show();
        }

        mLedDriver = new LEDDriver();
        if(mLedDriver.open("/dev/sm9s5422_led")<0){
            Toast.makeText(MainActivity.this, "LEDDriver Open Failed", Toast.LENGTH_SHORT).show();
        }
        mLedDriver.write(mLedCount);

        mSegCount = 0;
        mSegmentDriver = new SegmentDriver();
        if(mSegmentDriver.open("/dev/sm9s5422_segment")<0){
            Toast.makeText(MainActivity.this,"Driver Open Failed", Toast.LENGTH_SHORT).show();
        }
        mSegThreadRun = true;
        mSegThread = new SegmentThread();
        mSegThread.start();

        super.onResume();


    }

    @Override
    public void onReceive(int val){
        Message text = Message.obtain();
        text.arg1=val;
        handler.sendMessage(text);
    }

    protected void onDestroy() {
        cameraView.close();
        super.onDestroy();
    }

}

