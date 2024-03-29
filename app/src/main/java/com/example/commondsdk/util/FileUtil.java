package com.example.commondsdk.util;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;

public class FileUtil {
   public static void saveImage(byte[] mImage, String uri, Listener listener) {
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                writeImage(mImage, uri, listener);
            }
        });

    }

    public  interface Listener {

        void onComplete();
    }

    public static String productImageUrl() {
        String path = Environment.getExternalStorageDirectory() + "/DCIM/OcrCamera/";
        File mImageFile = new File(path);
        if (!mImageFile.exists()) {
            boolean ret = mImageFile.mkdirs();
            assert (ret);
        }
        String timeStamp = new SimpleDateFormat("YYYYMMdd_HHmmss").format(new Date());
        String fileName = path + "IMG_" + timeStamp + ".jpg";
        return fileName;
    }


    public static void clearImageCache() {
        String path = Environment.getExternalStorageDirectory() + "/DCIM/OcrCamera/";
        File mImageFile = new File(path);
        if (mImageFile.exists()) {
            mImageFile.delete();
        }
    }

    private static void writeImage(byte[] image, String fileName, Listener listener) {

        byte[] data = new byte[image.length];
        System.arraycopy(image, 0, data, 0, data.length);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
            fos.write(data, 0, data.length);
            if (listener != null) {
                listener.onComplete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }




}

