package com.example.commondsdk;

import android.hardware.camera2.CameraCharacteristics;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.commondsdk.databinding.ActivityOcrCameraPreviewBinding;
import com.example.ocr.OrcCameraView;
import com.example.ocr.SoundUtil;

import java.util.Random;


public class OcrCameraPreviewActivity extends BaseActionBarActivity {

    ActivityOcrCameraPreviewBinding previewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        previewBinding = ActivityOcrCameraPreviewBinding.inflate(getLayoutInflater());
        setContentView(previewBinding.getRoot());
        hideActionBar();
        Handler handler = new Handler(Looper.getMainLooper());
        String url = ImageFileUtil.productImageUrl();
        Log.i(TAG, " take image url=" + url);
        previewBinding.takePic.setOnClickListener(v -> {
            SoundUtil.shootSound(getApplicationContext());
            previewBinding.ocrCameraView.takePicture(new OrcCameraView.PictureTakeCallBack() {
                @Override
                public void onTakePic(Image image) {
                    ImageFileUtil.saveImage(image, url);
                    handler.postDelayed(() -> {
                        previewBinding.ocrCameraView.closeCamera();
                        Bundle bundle = new Bundle();

                        boolean isFrontSide=cardFrontSideRandom();
                        if(isFrontSide){
                            bundle.putString(Constant.IMAGE__FRONT_URL, url);
                        }else {
                            bundle.putString(Constant.IMAGE__BACK_URL, url);
                        }
                        jump(PicResultActivity.class, bundle);
                        finish();
                    }, 1000);
                }
            });

        });
        previewBinding.navigationBack.setOnClickListener((v) -> {
            finish();
        });
    }

    // 随机正反面
    private boolean cardFrontSideRandom() {
        Random random = new Random();
        int sand = random.nextInt(1000);
        return sand % 2 == 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        openCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeCamera();
    }

    private void openCamera() {
        int camera_type = CameraCharacteristics.LENS_FACING_FRONT;
        previewBinding.ocrCameraView.open(camera_type);
    }


    private void closeCamera() {
        previewBinding.ocrCameraView.closeCamera();

    }
}