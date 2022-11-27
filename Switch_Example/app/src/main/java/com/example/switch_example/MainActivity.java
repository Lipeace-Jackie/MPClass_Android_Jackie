package com.example.switch_example;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements JNIListener {
    TextView tv;
    String str="";

    JNIDriver mDriver;
    boolean mThreadRun=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textView1);

        mDriver = new JNIDriver();
        mDriver.setListener(this);
        if(mDriver.open("/dev/sm9s5422_interrupt")<0){
            Toast.makeText(MainActivity.this,"Driver Open Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause(){
        mDriver.close();
        super.onPause();
    }

    public Handler handler = new Handler((Looper.getMainLooper())){
        public void handleMessage(Message msg)  {
            switch(msg.arg1){
                case 1:
                    tv.setText("Up");
                    break;
                case 2:
                    tv.setText("Down");
                    break;
                case 3:
                    tv.setText("Left");
                    break;
                case 4:
                    tv.setText("Right");
                    break;
                case 5:
                    tv.setText("Center");
                    break;
            }
        }
    };

    @Override
    protected void onResume(){
        mDriver = new JNIDriver();
        mDriver.setListener(this);
        if(mDriver.open("/dev/sm9s5422_interrupt")<0){
            Toast.makeText(MainActivity.this,"Driver Open Failed", Toast.LENGTH_SHORT).show();
        }
        super.onResume();
    }

    @Override
    public void onReceive(int val){
        Message text = Message.obtain();
        text.arg1=val;
        handler.sendMessage(text);
    }


}