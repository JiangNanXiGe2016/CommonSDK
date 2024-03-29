package com.example.commonsdk.screen

import android.graphics.drawable.PaintDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.commonsdk.R
import com.example.commonsdk.ui.theme.Purple40

@Composable
fun ScreenPicture(saveClick: () -> Unit, quiteClick: () -> Unit) {
    Column() {
        val configuration = LocalConfiguration.current
        // 获取屏幕的宽高
        val screenWidth = configuration.screenWidthDp
        val factor = 0.6f
        val cardHeight = (screenWidth * factor)
        Box(modifier = Modifier.padding(10.dp),contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeight.dp)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.front_side),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds
                )

            }
        }
        Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeight.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.front_side),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds

                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 0.dp, 10.dp, 0.dp)
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = saveClick,
                colors = ButtonDefaults.buttonColors(containerColor = Purple40),
            ) {
                Text(text = "Use this picture")
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 10.dp, 10.dp, 0.dp)
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = quiteClick,
                colors = ButtonDefaults.buttonColors(containerColor = Purple40),
            ) {
                Text(text = "Quit")
            }
        }
    }
}