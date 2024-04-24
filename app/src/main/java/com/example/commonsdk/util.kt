package com.example.commonsdk

import android.annotation.SuppressLint
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.VideoCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.LifecycleOwner

fun bindPreview(
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    cameraProvider: ProcessCameraProvider,
    imageAnalyzer: ImageAnalysis
) {
    val preview = androidx.camera.core.Preview.Builder().build()
    val cameraSelector: CameraSelector =
        CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
    preview.setSurfaceProvider(previewView.surfaceProvider)
    val camera =
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalyzer)
    camera.cameraControl.cancelFocusAndMetering()
}

fun bindVideo(
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    cameraProvider: ProcessCameraProvider,
    @SuppressLint("RestrictedApi") videoCapture: VideoCapture,
) {
    val preview = androidx.camera.core.Preview.Builder().build()
    val cameraSelector: CameraSelector =
        CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
    preview.setSurfaceProvider(previewView.surfaceProvider)
    val camera =
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview,videoCapture)
    camera.cameraControl.cancelFocusAndMetering()
}

