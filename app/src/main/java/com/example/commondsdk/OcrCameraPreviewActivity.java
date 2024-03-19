package com.example.commondsdk;

import android.content.Intent;
import android.hardware.camera2.CameraCharacteristics;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.commondsdk.camera2.ImageInfo;
import com.example.commondsdk.camera2.OrcCameraView;
import com.example.commondsdk.databinding.ActivityOcrCameraPreviewBinding;
import com.example.commondsdk.util.FileUtil;
import com.example.commondsdk.util.ImageUtil;
import com.example.commondsdk.util.SPUtils;
import com.example.commondsdk.util.SoundUtil;

import java.util.Random;


public class OcrCameraPreviewActivity extends BaseActionBarActivity {

    ActivityOcrCameraPreviewBinding previewBinding;
    Handler handler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        previewBinding = ActivityOcrCameraPreviewBinding.inflate(getLayoutInflater());
        setContentView(previewBinding.getRoot());
        hideActionBar();

        previewBinding.ocrCameraView.setDashedRectangleView(previewBinding.dashView);
        previewBinding.takePic.setOnClickListener(v -> {
            SoundUtil.shootSound(getApplicationContext());
            previewBinding.ocrCameraView.takePicture(new OrcCameraView.PictureTakeCallBack() {
                @Override
                public void onTakePic(ImageReader imageReader) {

                }
            });
        });
        previewBinding.navigationBack.setOnClickListener((v) -> {
            finish();
        });
        String frontText = getString(R.string.id_card_front_text);
        String backText = getString(R.string.id_card_back_text);
        int step = (int) SPUtils.get(getApplicationContext(),Constant.IMAGE_OCR_STEP,-1);
        if (step == Constant.STEP_FRONT_SIDE) {
            previewBinding.hintTv.setText(frontText);
        } else if (step == Constant.STEP_BACK_SIDE) {
            previewBinding.hintTv.setText(backText);
        }
        previewBinding.ocrCameraView.enableOnFrame(true);

        // 相机出帧回调
        previewBinding.ocrCameraView.addFrameListener(new OrcCameraView.OnFrameListener() {
            @Override
            public void onFrame(ImageReader imageReader, ImageInfo imageSize) {
                mockProcess(imageReader, imageSize);
            }
        });
    }


    //模拟ocr识别，抓拍
    private void mockProcess(ImageReader imageReader, ImageInfo imageSize) {
        Image mImage = imageReader.acquireLatestImage();

        Log.d(TAG, "onCameraFrame:mImage" + mImage);
        Log.d(TAG, "onCameraFrame:imageSize" + imageSize);

        byte[] data = ImageUtil.imageToByteArray(mImage);
       //  byte[] rbg=ImageUtil.jpeg2RgbByteArray(data);
        // 1.push image to ocr
        if (isPerfect(0, data, imageSize.framePicW, imageSize.framePicH)) {
            //1.stop onFrame
            previewBinding.ocrCameraView.enableOnFrame(false);
            String url = FileUtil.productImageUrl();

            //2.save image
            FileUtil.saveImage(data, url, () -> {
                //3.to image result page
                SPUtils.put(getApplicationContext(), Constant.IMAGE_URL, url);
                Intent intent = new Intent(OcrCameraPreviewActivity.this, PicResultActivity.class);
                startActivity(intent);
                finish();
            });
        } else {
            mImage.close();
        }
        // image must close
    }
    /**
     * 模拟算法接口
     **/
    private boolean isPerfect(int type, byte[] image, int imgW, int imgH) {
        Random random = new Random();
        int ret = random.nextInt(1000);
        return ret % 25 == 0;
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