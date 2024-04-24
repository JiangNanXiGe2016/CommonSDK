package com.example.commonsdk.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.VideoCapture
import androidx.camera.core.VideoCapture.OnVideoSavedCallback
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.commonsdk.bindVideo
import java.io.File


@SuppressLint("RestrictedApi")
@Preview
@Composable
fun VideoScreen() {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val videoCapture = remember {
        VideoCapture.Builder().setVideoFrameRate(30).build();
    }
    var startVideo = remember {
        false
    }
    Box(modifier = Modifier, contentAlignment = Alignment.BottomCenter) {
        AndroidView(
            factory = { ctx ->
                val preview = PreviewView(context).apply {

                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
                val executor = ContextCompat.getMainExecutor(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    bindVideo(lifecycleOwner, preview, cameraProvider, videoCapture)
                }, executor)
                preview
            }, modifier = Modifier.fillMaxSize()
        )

        Column {
            Box(modifier = Modifier
                .clip(CircleShape)
                .border(BorderStroke(5.dp, Color.White), CircleShape)
                .background(Color.Red)
                .width(60.dp)
                .height(60.dp)
                .clickable {
                    if (!startVideo) {
                        startVideo = true
                        Toast
                            .makeText(context, "开始录像", Toast.LENGTH_LONG)
                            .show()
                        startVideoRecord(context, videoCapture)
                    } else {
                        startVideo = false
                        videoCapture.stopRecording()
                    }
                })
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@SuppressLint("RestrictedApi")
fun startVideoRecord(context: Context, @SuppressLint("RestrictedApi") videoCapture: VideoCapture) {
    val executor = ContextCompat.getMainExecutor(context)
    val file: File = File(context.filesDir.toString(), "video.mp4")
    val outputFileOptions: VideoCapture.OutputFileOptions =
        VideoCapture.OutputFileOptions.Builder(file).build()
    if (ActivityCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }
    videoCapture.startRecording(outputFileOptions, executor, object : OnVideoSavedCallback {
        override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
            Toast.makeText(context, "录像已经保存", Toast.LENGTH_LONG).show()
        }

        override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {

        }
    })
}

