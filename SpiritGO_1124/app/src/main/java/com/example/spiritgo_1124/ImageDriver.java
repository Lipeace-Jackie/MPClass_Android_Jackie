package com.example.spiritgo_1124;

import static org.opencv.core.Core.mean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;

public class ImageDriver {
    static {
        System.loadLibrary("OpenCLDriver");
    }

    public static native int rgb2hsv(Bitmap bitmap);

    public static native int inRange(Bitmap bitmap, byte[] ranges);

    private static Matrix mtx_180;

    public static byte[] redRange = null, greenRange = null;

    public static boolean isCaliAvailable() {
        return redRange != null && greenRange != null;
    }


    public static Bitmap getImage(byte[] data) {
        if (mtx_180 == null) {
            mtx_180 = new Matrix();
            mtx_180.postRotate(180);
        }
        Bitmap img = BitmapFactory.decodeByteArray(data, 0, data.length);
        int w = img.getWidth(), h = img.getHeight();
        Log.d("BITMAP", String.format("orig captured image size: %d x %d", w, h));
        img = Bitmap.createBitmap(img, 0, 0, w, h, mtx_180, true);
        return img;
    }

    public static Bitmap getCrop(Bitmap img) {
        final int miniW = 80, miniH = 80;
        int w = img.getWidth(), h = img.getHeight();
        int x = (w - miniW) / 2, y = (h - miniH) / 2;
        Bitmap crop = Bitmap.createBitmap(img, x, y, miniW, miniH);
        return crop;
    }

    public static byte[] img2ByteArr(Bitmap img) {
        ByteBuffer buffer = ByteBuffer.allocate(img.getByteCount());
        img.copyPixelsToBuffer(buffer);
        return buffer.array();
    }

    public static byte[] getHsvRange(Bitmap img) { // cropped img
        final int[] MARGIN = {12, 18, 30}; // H,S,V
        final int MASK = 0x00_00_00_FF;
        rgb2hsv(img);
        int[] ranges = {255, -255, 255, 0, 255, 0};
        int sum = 0;
        int w = img.getWidth(), h = img.getHeight();
        byte[] pixels = img2ByteArr(img);

        for (int i = 0; i < w * h * 4; i += 4) {
            //h
            sum += (int) pixels[i + 0] & MASK;
            //s
            ranges[2] = Math.min(ranges[2], (int) pixels[i + 1] & MASK);
            ranges[3] = Math.max(ranges[3], (int) pixels[i + 1] & MASK);
            //v
            ranges[4] = Math.min(ranges[4], (int) pixels[i + 2] & MASK);
            ranges[5] = Math.max(ranges[5], (int) pixels[i + 2] & MASK);
        }
        sum /= (w * h);
        ranges[0] = ranges[1] = sum;
        ranges[0] -= MARGIN[0];
        ranges[0] += (ranges[0] < 0) ? 255 : 0;
        ranges[1] += MARGIN[0];
        ranges[1] += (ranges[1] > 255) ? -255 : 0;

        ranges[2] = Math.max(ranges[2] - MARGIN[1], 0);
        ranges[3] = Math.min(ranges[3] + MARGIN[1], 255);

        ranges[4] = Math.max(ranges[4] - MARGIN[2], 0);
        ranges[5] = Math.min(ranges[5] + MARGIN[2], 255);

        byte[] ret = new byte[6];
        for (int i = 0; i < 6; i++)
            ret[i] = (byte) (ranges[i] & MASK);
        Log.d("BITMAP", arr2str(ret));
        return ret;
    }

    public static boolean isUp(Bitmap img) {
        int w = img.getWidth(), h = img.getHeight();
        ByteBuffer buffer = ByteBuffer.allocate(img.getByteCount());
        img.copyPixelsToBuffer(buffer);
        byte[] pixels = buffer.array();
        int i;
        int direction = 0;
        for (i = 0; i < w * (h / 2) * 4; i += 4) {
            if (pixels[i] != 0) direction++;
        }

        for (; i < w * h * 4; i += 4) {
            if (pixels[i] != 0) direction--;
        }
        return direction > 0;
    }

    public static String arr2str(int[] arr) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int x : arr)
            sb.append(x).append(' ');
        sb.append(']');
        return sb.toString();
    }

    public static String arr2str(byte[] arr) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (byte x : arr)
            sb.append((int) x & 0xFF).append(' ');
        sb.append(']');
        return sb.toString();
    }

    public static int[] getResult(byte[] data) {
        Bitmap img = ImageDriver.getImage(data);
        int w = img.getWidth(), h = img.getHeight();
        img = Bitmap.createScaledBitmap(img, w / 4, h / 4, true);

        Mat mat = new Mat();
        Utils.bitmapToMat(img, mat);
        Mat mat_hsv = new Mat();
        //ImageDriver.rgb2hsv(img); // hsv
        Imgproc.cvtColor(mat, mat_hsv, Imgproc.COLOR_RGB2HSV);

        byte[] hsvRange = {55, 121, 0, 127, 0, 127};
        int[] average = {0, 0, 0, 0, 0};

        for(int i = 0; i < 5; i++) {
            Bitmap img_hsv = img.copy(img.getConfig(), true);
            Utils.matToBitmap(mat_hsv, img_hsv);
            ImageDriver.inRange(img_hsv, hsvRange);

            Mat mat_th = new Mat();
            Utils.bitmapToMat(img_hsv, mat_th);
            Scalar avg = mean(mat_th);

            average[i] = (int) avg.val[0];




        }

        return average;


//        boolean redUp = ImageDriver.isUp(redTh);
//        redTh = img = null;
//
//        ImageDriver.inRange(greenTh, ImageDriver.greenRange);
//        boolean greenUp = ImageDriver.isUp(greenTh);
//        greenTh = null;
//
//        return new FlagState(redUp, greenUp);
    }
}
