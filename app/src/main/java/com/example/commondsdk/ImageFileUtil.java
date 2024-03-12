package com.example.commondsdk;

import android.media.Image;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ImageFileUtil {
    static void saveImage(byte[] mImage, String uri) {
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                writeImage(mImage, uri);
            }
        });

    }

    public static String productImageUrl() {
        String path = Environment.getExternalStorageDirectory() + "/DCIM/OcrCamera/";
        File mImageFile = new File(path);
        if (!mImageFile.exists()) {
            boolean ret = mImageFile.mkdirs();
            assert (ret);
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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

    private static void writeImage(byte[] image, String fileName) {

        byte[] data = new byte[image.length];
        System.arraycopy(image, 0, data, 0, data.length);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
            fos.write(data, 0, data.length);
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


    /**
     * 实现yuv转换成rgb
     **/
    public  static void yuv2Rgb(Image image) {

    }


}

