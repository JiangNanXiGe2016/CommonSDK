package com.example.commondsdk;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.commondsdk.databinding.ActivityMainBinding;
import com.example.ocr.Permission;

public class MainActivity extends AppCompatActivity {
    private String TAG = "yangiang";
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
        mainBinding.startTake.setOnClickListener((v) -> {
            startOrc();
        });
        mainBinding.quiteApp.setOnClickListener((v) -> {
            startOrcResult();
        });

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
            Log.i("TAG", "未请求成功");
            return;
        }
        Intent intent = new Intent(getApplicationContext(), OcrCameraPreviewActivity.class);
        startActivity(intent);
    }

    private void startOrcResult() {
        if (!Permission.isPermissionGranted(this)) {
            Log.i("TAG", "未请求成功");
            return;
        }
        Intent intent = new Intent(getApplicationContext(), PicResultActivity.class);
        startActivity(intent);
    }
}