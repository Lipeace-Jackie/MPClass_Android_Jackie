package com.example.spiritgo_1124;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SegmentDriver {
    static {
        System.loadLibrary("SegmentDriver");
    }
    private native static int openDriver(String path);
    private native static void closeDriver();
    private native static void writeDriver(byte[] data, int length);


    protected void write(int data_int){
        byte[] n = {0,0,0,0,0,0};
        n[0] = (byte) (data_int % 1000000 / 100000);
        n[1] = (byte) (data_int % 100000 / 10000);
        n[2] = (byte) (data_int % 10000 / 1000);
        n[3] = (byte) (data_int % 1000 / 100);
        n[4] = (byte) (data_int % 100 / 10);
        n[5] = (byte) (data_int % 10);
        writeDriver(n, n.length);
    }



    protected void close(){
        closeDriver();
    }

    protected int open(String driver){
        return openDriver(driver);
    }


}
