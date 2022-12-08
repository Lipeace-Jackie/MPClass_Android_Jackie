package com.example.spiritgo_1124;


public class LEDDriver {

    static {
        System.loadLibrary("LEDDriver");
    }

    private native static int openDriver(String path);
    private native static void closeDriver();
    private native static void writeDriver(byte[] data, int length);

    byte[] data = {0,0,0,0,0,0,0,0};

    protected void write(int count){
        for(int i=0; i<8; i++){
            data[i] = 0;
        }
        for(int i=0; i<count; i++){
            data[i] = 1;
        }
        writeDriver(data, data.length);
    }

    protected void close(){
        closeDriver();
    }

    protected int open(String driver){
        return openDriver(driver);
    }
}