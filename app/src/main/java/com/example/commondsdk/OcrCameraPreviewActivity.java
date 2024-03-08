package com.example.commondsdk;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.commondsdk.databinding.ActivityOcrCameraPreviewBinding;

public class OcrCameraPreviewActivity extends AppCompatActivity {
    ActivityOcrCameraPreviewBinding previewBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        previewBinding = ActivityOcrCameraPreviewBinding.inflate(getLayoutInflater());
        setContentView(previewBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

    private void startPreview() {
    }

    private void closeCamera() {
        previewBinding.ocrCameraView.closeCamera();

    }
}