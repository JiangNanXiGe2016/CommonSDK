package com.example.commonsdk.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Px
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.commonsdk.bindPreview
import com.example.commonsdk.ui.theme.Purple40




@Composable
fun ScreenPreView(takePicture: () -> Unit, quiteClick: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val configuration = LocalConfiguration.current
    val density:Density=LocalDensity.current

    // 获取屏幕的宽高
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    Log.i("yl", "screenWidth=$screenWidth screenHeight=$screenHeight")
    val factor = 0.75
    val previewWidth: Float = screenWidth.toFloat();
    val previewHeight = (screenWidth / factor)-50

    Log.i("yl", "previewWidth=$previewWidth blankHeight=$previewHeight")
    val blankFactor = 0.7
    val blankWidth = screenWidth * blankFactor
    val blankHeight = blankWidth / factor

    Log.i("yl", "blankWidth=$blankWidth blankHeight=$blankHeight")
    // start pos
    val startX = (screenWidth - blankWidth) / 2
    val startY = screenHeight / 2 - (blankHeight / 2)
    // end  pos
    val endX = (screenWidth + blankWidth) / 2
    val endY = (screenHeight / 2) + blankHeight / 2
    Log.i("yl", "startX=$startX startY=$startY endX=$endX endY=$endY")
    Box(
        modifier = Modifier
            .height((screenHeight-180).dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { ctx ->
                    val preview = PreviewView(context).apply {

                        layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT)
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                    val executor = ContextCompat.getMainExecutor(ctx)
                    val imageAnalyzer = ImageAnalysis.Analyzer { imageProxy ->
                        val buffer = imageProxy.planes[0].buffer
                        imageProxy.close()
                        Log.i("yll", "onFrame:$buffer")
                    }

                    val imageAnalysis = ImageAnalysis.Builder().build();
                    imageAnalysis.setAnalyzer(executor, imageAnalyzer)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        bindPreview(
                            lifecycleOwner, preview, cameraProvider, imageAnalysis
                        )
                    }, executor)
                    preview
                }, modifier = Modifier
                    .width(previewWidth.dp)
                    .height(previewHeight.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
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
                                    ((startY-100) * density.density).toFloat(),
                                    (endX * density.density).toFloat(),
                                    ((endY-100) * density.density).toFloat(),
                                    dashedPaint
                                )
                            }
                        }
                    }, modifier = Modifier.fillMaxSize()
                )
            }


        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row {
            Button(
                onClick = takePicture,
                Modifier
                    .width(0.dp)
                    .weight(1f)
                    .height(70.dp)
                    .padding(10.dp, 10.dp, 10.dp, 10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple40),
            ) {
                Text(text = "Take Picture")
            }

            Button(
                onClick = quiteClick,
                Modifier
                    .width(0.dp)
                    .weight(1f)
                    .height(70.dp)
                    .padding(10.dp, 10.dp, 10.dp, 10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple40),
            ) {
                Text(text = "Quit")
            }
        }


    }


}