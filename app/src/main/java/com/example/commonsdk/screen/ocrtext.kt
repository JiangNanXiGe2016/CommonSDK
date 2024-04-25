package com.example.commonsdk.screen

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.commonsdk.OcrActivity
import com.example.commonsdk.ui.theme.Purple40


@Composable
fun OrcScreen() {
    Column() {
        val act: Activity = LocalContext.current as Activity
        Button(
            onClick = {
                // todo request permission
                val intent = Intent(act, OcrActivity::class.java)
                act.startActivity(intent)
            },
            Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(10.dp, 10.dp, 10.dp, 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple40),
        ) {
            Text(text = "请求权限")
        }
        Button(
            onClick = {
                // todo copy models
            },
            Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(10.dp, 10.dp, 10.dp, 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple40),
        ) {
            Text(text = "算法初始化")
        }
        Button(
            onClick = {},
            Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(10.dp, 10.dp, 10.dp, 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple40),
        ) {
            Text(text = "打开相册")
        }
        Button(
            onClick = {

            },
            Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(10.dp, 10.dp, 10.dp, 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple40),
        ) {
            Text(text = "打开相机")
        }


    }
}