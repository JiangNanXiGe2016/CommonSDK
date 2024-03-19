package com.example.commondsdk;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.commondsdk.databinding.ActivityMainBinding;
import com.example.commondsdk.util.Permission;
import com.example.commondsdk.util.SPUtils;

public class MainActivity extends BaseActionBarActivity {
    private String TAG = Constant.TAG;
    private ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setUpView();
    }

    //
    private void setUpView() {
        Permission.checkPermission(this);
        mainBinding.analysisWithCamerax.setOnClickListener(v -> {
            startCameraXAnalysis();
        });
        mainBinding.startTake.setOnClickListener((v) -> {
            startOrc();
        });
        mainBinding.quiteApp.setOnClickListener((v) -> {
            finish();
        });
        setActionBarTitle(getString(R.string.act_main_title));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Permission.REQUEST_CODE && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            finish();
        }
    }

    //打开预览界面
    private void startOrc() {
        if (!Permission.isPermissionGranted(this)) {
            Log.i(TAG, "未请求成功");
            return;
        }
        SPUtils.put(getApplicationContext(), Constant.IMAGE_OCR_STEP, Constant.STEP_FRONT_SIDE);
        //清除本地保存的
        //打开相机预览
        Intent intent = new Intent(getApplicationContext(), OcrCameraPreviewActivity.class);
        startActivity(intent);
    }

    private void startCameraXAnalysis() {
        if (!Permission.isPermissionGranted(this)) {
            Log.i(TAG, "未请求成功");
            return;
        }
        SPUtils.put(getApplicationContext(), Constant.IMAGE_OCR_STEP, Constant.STEP_FRONT_SIDE);
        //清除本地保存的
        //打开相机预览
        Intent intent = new Intent(getApplicationContext(), CameraXPreViewActivity.class);
        startActivity(intent);
    }
}