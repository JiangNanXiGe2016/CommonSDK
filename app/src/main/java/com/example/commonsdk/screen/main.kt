package com.example.commonsdk.screen

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.commonsdk.OcrActivity
import com.example.commonsdk.ui.theme.Purple40



@Composable
fun ScreenMain(ocrClick: () -> Unit,previewClick: () -> Unit,videoClick:()->Unit, quiteClick:() -> Unit) {
    Column() {
        val act: Activity = LocalContext.current as Activity
        Button(
            onClick = {
                ocrClick.invoke()
            },
            Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(10.dp, 10.dp, 10.dp, 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple40),
        ) {
            Text(text = "Ocr Demo")
        }
        Button(
            onClick = previewClick,
            Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(10.dp, 10.dp, 10.dp, 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple40),
        ) {
            Text(text = "Camera PreView")
        }
        Button(
            onClick = videoClick,
            Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(10.dp, 10.dp, 10.dp, 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple40),
        ) {
            Text(text = "Camera Video")
        }
        Button(
            onClick = {
                quiteClick.invoke()
            },
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