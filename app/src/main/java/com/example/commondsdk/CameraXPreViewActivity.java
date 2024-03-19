package com.example.commondsdk;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;

import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;

import com.example.commondsdk.databinding.ActivityCameraXPreviewBinding;
import com.example.commondsdk.databinding.ActivityOcrCameraPreviewBinding;

public class CameraXPreViewActivity extends BaseActionBarActivity {
    ActivityCameraXPreviewBinding binding;
    private long mLastAnalysisResultTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraXPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        hideActionBar();
        setUpCameraX();
        setUpView();
    }

    private void setUpView() {
        binding.navigationBack.setOnClickListener(v -> finish());
    }

    private void setUpCameraX() {

        final PreviewConfig previewConfig = new PreviewConfig.Builder().build();
        final Preview preview = new Preview(previewConfig);
        preview.setOnPreviewOutputUpdateListener(output ->
                binding.cameraxPreview.setSurfaceTexture(output.getSurfaceTexture()));

        final ImageAnalysisConfig imageAnalysisConfig = new ImageAnalysisConfig.Builder().
                setTargetResolution(new Size(480, 640))
                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE).build();
        final ImageAnalysis imageAnalysis = new ImageAnalysis(imageAnalysisConfig);
        imageAnalysis.setAnalyzer((image, rotationDegrees) -> {
            if (SystemClock.elapsedRealtime() - mLastAnalysisResultTime < 500) {
                return;
            }
            mLastAnalysisResultTime = SystemClock.elapsedRealtime();
            analyzeImage(image, rotationDegrees);

        });

        CameraX.bindToLifecycle(this, preview, imageAnalysis);
    }

    protected void analyzeImage(ImageProxy image, int rotationDegrees) {
        Log.d(TAG, "analyzeImage:mImage" + image);

    }
}
