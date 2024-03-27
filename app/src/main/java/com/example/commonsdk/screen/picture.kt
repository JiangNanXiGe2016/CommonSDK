package com.example.commonsdk.screen

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.commonsdk.ui.theme.Purple40

@Composable
fun ScreenPicture(saveClick: () -> Unit, quiteClick: () -> Unit) {
    Column() {
        Button(
            onClick = saveClick,
            Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(10.dp, 10.dp, 10.dp, 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple40),
        ) {
            Text(text = "Start PreView")
        }
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