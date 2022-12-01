package com.example.spiritgo_1124;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;


public class LEDDriver {

    static {
        System.loadLibrary("LEDDriver");
    }

    private native static int openDriver(String path);
    private native static void closeDriver();
    private native static void writeDriver(byte[] data, int length);

//    int[] btString = { R.id.toggleButton0,
//            R.id.toggleButton1,
//            R.id.toggleButton2,
//            R.id.toggleButton3,
//            R.id.toggleButton4,
//            R.id.toggleButton5,
//            R.id.toggleButton6,
//            R.id.toggleButton7};
//    ToggleButton[] mBtn = new ToggleButton[8];
    byte[] data = {0,0,0,0,0,0,0,0};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CompoundButton.OnCheckedChangeListener listener =
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        switch (buttonView.getId()){
                            case R.id.toggleButton0:
                                if(mBtn[0].isChecked()) data[0]=1;
                                else                    data[0]=0;
                                break;
                            case R.id.toggleButton1:
                                if(mBtn[1].isChecked()) data[1]=1;
                                else                    data[1]=0;
                                break;
                            case R.id.toggleButton2:
                                if(mBtn[2].isChecked()) data[2]=1;
                                else                    data[2]=0;
                                break;
                            case R.id.toggleButton3:
                                if(mBtn[3].isChecked()) data[3]=1;
                                else                    data[3]=0;
                                break;
                            case R.id.toggleButton4:
                                if(mBtn[4].isChecked()) data[4]=1;
                                else                    data[4]=0;
                                break;
                            case R.id.toggleButton5:
                                if(mBtn[5].isChecked()) data[5]=1;
                                else                    data[5]=0;
                                break;
                            case R.id.toggleButton6:
                                if(mBtn[6].isChecked()) data[6]=1;
                                else                    data[6]=0;
                                break;
                            case R.id.toggleButton7:
                                if(mBtn[7].isChecked()) data[7]=1;
                                else                    data[7]=0;
                                break;
                        }

                        writeDriver(data, data.length);
                    }
                };

        for(int i=0; i<8; i++){
            mBtn[i] = (ToggleButton) findViewById(btString[i]);
            mBtn[i].setChecked(false);
            mBtn[i].setOnCheckedChangeListener(listener);
        }
    }

    protected void close(){
        closeDriver();
    }

    protected int open(String driver){
        return openDriver(driver);
    }
}