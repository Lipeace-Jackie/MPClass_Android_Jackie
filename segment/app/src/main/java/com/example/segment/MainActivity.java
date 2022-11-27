package com.example.segment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("JNIDriver");
    }
    private native static int openDriver(String path);
    private native static void closeDriver();
    private native static void writeDriver(byte[] data, int length);

    int data_int, i;
    boolean mThreadRun, mStart;
    SegmentThread mSegThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button)findViewById((R.id.button1));
        btn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                String str = ((EditText)findViewById(R.id.editText1)).getText().toString();
                try{
                    data_int = Integer.parseInt(str);
                    mStart=true;
                }catch(NumberFormatException E){
                    Toast.makeText(MainActivity.this, "Input Error", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private class SegmentThread extends Thread{
        @Override
        public void run(){
            super.run();
            while(mThreadRun){
                byte[] n = {0,0,0,0,0,0};
                if(mStart==false) writeDriver(n,n.length);
                else{
                    for(i=0; i<1; i++){
                        n[0] = (byte) (data_int % 1000000 / 100000);
                        n[1] = (byte) (data_int % 100000 / 10000);
                        n[2] = (byte) (data_int % 10000 / 1000);
                        n[3] = (byte) (data_int % 1000 / 100);
                        n[4] = (byte) (data_int % 100 / 10);
                        n[5] = (byte) (data_int % 10);
                        writeDriver(n, n.length);
                    }
                    if(data_int>0) data_int--;
                }
            }
        }
    }

    @Override
    protected void onPause(){
        closeDriver();
        mThreadRun = false;
        mSegThread = null;
        super.onPause();
    }
    @Override
    protected void onResume(){
        if(openDriver("/dev/sm9s5422_segment")<0){
            Toast.makeText(MainActivity.this,"Driver Open Failed", Toast.LENGTH_SHORT).show();
        }
        mThreadRun = true;
        mSegThread = new SegmentThread();
        mSegThread.start();
        super.onResume();
    }
}
