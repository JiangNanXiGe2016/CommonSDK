package com.example.commondsdk.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public final class ImageUtil {

    public static byte[] imageToByteArray(Image image) {
        byte[] data = null;
        if (image.getFormat() == ImageFormat.JPEG) {
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            data = new byte[buffer.capacity()];
            buffer.get(data);
            return data;
        } else if (image.getFormat() == ImageFormat.YUV_420_888) {
            data = NV21toJPEG(YUV_420_888toNV21(image), image.getWidth(), image.getHeight());
        }
        return data;
    }

    private static byte[] YUV_420_888toNV21(Image image) {
        byte[] nv21;
        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
        ByteBuffer vuBuffer = image.getPlanes()[2].getBuffer();

        int ySize = yBuffer.remaining();
        int vuSize = vuBuffer.remaining();

        nv21 = new byte[ySize + vuSize];

        yBuffer.get(nv21, 0, ySize);
        vuBuffer.get(nv21, ySize, vuSize);

        return nv21;
    }

    private static byte[] NV21toJPEG(byte[] nv21, int width, int height) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);
        return out.toByteArray();
    }

    private static Bitmap Nv21toBitmap(byte[] nv21, int width, int height) {
        YuvImage image = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compressToJpeg(new Rect(0, 0, width, height), 80, stream);
        Bitmap newBitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
        try {
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return newBitmap;

    }

    public static Bitmap jpeg2Bitmap(byte[] jpeg) {
        return BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length, null);
    }


    public static byte[] jpeg2RgbByteArray(byte[] jpeg) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length, null);
        int size = bitmap.getRowBytes() * bitmap.getHeight();
        ByteBuffer b = ByteBuffer.allocate(size);
        bitmap.copyPixelsToBuffer(b);
        byte[] bytes = new byte[size];

        try {
            b.get(bytes, 0, bytes.length);
        } catch (BufferUnderflowException e) {
            e.fillInStackTrace();
        }
        return bytes;
    }

}
