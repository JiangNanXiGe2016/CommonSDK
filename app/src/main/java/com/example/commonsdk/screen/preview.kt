package com.example.commonsdk.screen

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Px
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.commonsdk.ui.theme.Purple40

private fun bindPreview(
    lifecycleOwner: LifecycleOwner, previewView: PreviewView, cameraProvider: ProcessCameraProvider
) {
    val preview = androidx.camera.core.Preview.Builder().build()
    val cameraSelector: CameraSelector =
        CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
    preview.setSurfaceProvider(previewView.surfaceProvider)
    val camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
    camera.cameraControl.cancelFocusAndMetering()
}


@Composable
fun ScreenPreView(takePicture: () -> Unit, quiteClick: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val configuration = LocalConfiguration.current

    // 获取屏幕的宽高
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    Log.i("yl", "screenWidth=$screenWidth screenHeight=$screenHeight")
    val factor = 0.75
    val previewWidth: Float = screenWidth.toFloat();
    val previewHeight = screenWidth / factor

    Log.i("yl", "previewWidth=$previewWidth blankHeight=$previewHeight")


    val blankFactor = 0.8;

    val blankWidth = screenWidth * blankFactor
    val blankHeight = blankWidth / factor
    Log.i("yl", "blankWidth=$blankWidth blankHeight=$blankHeight")
    // start pos
    val startX = (screenWidth - blankWidth) / 2
    val startY = screenHeight/2 - (blankHeight / 2)
    // end  pos
    val endX = (screenWidth + blankWidth) / 2
    val endY = (screenHeight / 2) + blankHeight / 2

    Log.i("yl", "startX=$startX startY=$startY endX=$endX endY=$endY")


    Box(
        modifier = Modifier, contentAlignment = Alignment.BottomCenter
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AndroidView(
                factory = { ctx ->
                    val preview = PreviewView(context).apply {
                        this.scaleType = scaleType
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        // Preview is incorrectly scaled in Compose on some devices without this
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                    val executor = ContextCompat.getMainExecutor(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        bindPreview(
                            lifecycleOwner,
                            preview,
                            cameraProvider,
                        )
                    }, executor)
                    preview
                }, modifier = Modifier
                    .width(previewWidth.dp)
                    .height(previewHeight.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            val dashedPaint = Paint()
            dashedPaint.setColor(android.graphics.Color.WHITE) // 虚线颜色
            dashedPaint.style = Paint.Style.STROKE
            dashedPaint.strokeWidth = 5f // 虚线宽度
            dashedPaint.setPathEffect(DashPathEffect(floatArrayOf(5f, 5f), 5f))
            val density = LocalDensity.current

            AndroidView(
                factory = { context ->
                    object : View(context) {
                        override fun onDraw(canvas: Canvas) {
                            super.onDraw(canvas)
                            canvas.drawRect(
                                (startX * density.density).toFloat(),
                                (startY * density.density).toFloat(),
                                (endX * density.density).toFloat(),
                                (endY * density.density).toFloat(),
                                dashedPaint
                            )
                        }
                    }
                }, modifier = Modifier.fillMaxSize()
            )
        }
        Column {
            Button(
                onClick = quiteClick,
                Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(10.dp, 10.dp, 10.dp, 10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple40),
            ) {
                Text(text = "Quit")
            }
        }
    }
}