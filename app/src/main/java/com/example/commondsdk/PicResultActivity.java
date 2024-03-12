package com.example.commondsdk;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.Manifest;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.commondsdk.databinding.ActivityPicResultBinding;

import java.io.File;

public class PicResultActivity extends BaseActionBarActivity {
    ActivityPicResultBinding picResultBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        picResultBinding = ActivityPicResultBinding.inflate(getLayoutInflater());
        setContentView(picResultBinding.getRoot());
        setActionBarTitle(getString(R.string.act_result_title));
        showNavigation(false);
        setUpView();
    }

    private void setUpView() {
        RequestOptions requestOptions_front = new RequestOptions().placeholder(R.drawable.front_side).override(480, 300);
        Bundle bundle = getIntent().getBundleExtra(Constant.BUNDLE_PARAMS);
        String imageUrl = "";
        if (bundle != null) {
            imageUrl = bundle.getString(Constant.IMAGE_URL);
        }
        Log.i(TAG, "imageUrl=" + imageUrl);
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(this).load(imageUrl).apply(requestOptions_front).into(picResultBinding.imgFront);
        }
        picResultBinding.usePic.setOnClickListener((v) -> {
            //保存图片到相册
            toAlblums();
        });
        picResultBinding.takePicAgain.setOnClickListener((v) -> {
            // 被选择的一个图片重新拍摄
            takeImageAgain();
        });
        picResultBinding.quiteApp.setOnClickListener(v -> finish());

    }

    private void takeImageAgain() {
        ImageFileUtil.clearImageCache();
        SPUtils.clear(getApplicationContext());
        Bundle bundle = getIntent().getBundleExtra(Constant.BUNDLE_PARAMS);
        jump(OcrCameraPreviewActivity.class, bundle);
    }

    private void toAlblums() {
        // 1.现保存到sp 2.保存到相机
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            //保存图片到相册
            doSaveImage();
        }
        ContentValues image = new ContentValues();
        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);
    }

    private void doSaveImage() {
        try {
            Bundle bundle = getIntent().getBundleExtra(Constant.BUNDLE_PARAMS);
            if (bundle != null) {
                String frontSideUrl = bundle.getString(Constant.IMAGE_URL);
                Log.i(TAG, "frontSideUrl=" + frontSideUrl);
                if (TextUtils.isEmpty(frontSideUrl)) {
                    return;
                }
                MediaStore.Images.Media.insertImage(getContentResolver(), frontSideUrl, "title", "description");
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    Toast.makeText(this, "图片已保存到相册！", Toast.LENGTH_SHORT).show();
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(frontSideUrl))));
                    jumpToNextStep();

                }, 1000);
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    private void jumpToNextStep() {
        Bundle bundle = getIntent().getBundleExtra(Constant.BUNDLE_PARAMS);
        int step = bundle.getInt(Constant.IMAGE_OCR_STEP);
        if (step == Constant.STEP_BACK_SIDE) {
            Toast.makeText(this, "正反面均已拍摄完成！", Toast.LENGTH_SHORT).show();
        } else if (step == Constant.STEP_FRONT_SIDE) {
            bundle.putInt(Constant.IMAGE_OCR_STEP, Constant.STEP_BACK_SIDE);
            jump(OcrCameraPreviewActivity.class, bundle);
        }
    }

    //请求权限后的结果回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //保存图片到相册
                doSaveImage();
            } else {
                Toast.makeText(this, "你拒绝了该权限，无法保存图片！", Toast.LENGTH_SHORT).show();
            }
        }
    }

}