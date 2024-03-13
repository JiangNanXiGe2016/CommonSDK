package com.example.ocr;

import androidx.annotation.NonNull;

public class ImageInfo {
    // frame preview witdh and height
    int framePicW;
    int framePicH;

    // bank in image preview ,start and end point position
    DashBoxInfo dashBoxInfo;

    @NonNull
    @Override
    public String toString() {
        return "ImageInfo{" + "framePicW=" + framePicW + ", framePicH=" + framePicH + ", dashBoxInfo=" + dashBoxInfo.toString() + '}';
    }
}
