package com.example.commonsdk

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageAnalysis
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.commonsdk.screen.ScreenMain
import com.example.commonsdk.screen.ScreenPicture
import com.example.commonsdk.screen.ScreenPreView
import com.example.commonsdk.ui.theme.CommonSDKTheme
import com.example.commonsdk.ui.theme.Purple40
import com.google.accompanist.navigation.animation.AnimatedNavHost

class MainActivity : ComponentActivity() {

    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CommonSDKTheme {
                Surface(contentColor = Color.White) {
                    App()
                }
            }
        }
        cameraPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isPermissionGranted ->
                run {
                    if (isPermissionGranted) {
                        // 权限被授予，可以继续使用相机
                        Log.i("registerForActivityResult", "yes")
                    } else {
                        // 权限被拒绝，显示提示或者关闭相机预览
                        Log.i("registerForActivityResult", "no")
                    }

                }
            }
        //1.请求相机权限
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Preview
@Composable
fun App() {
    val navController = rememberNavController()
    var index by remember { mutableStateOf("main") }
    val act: Activity = LocalContext.current as Activity

    Column() {
        when (index) {
            "main" -> {
                TopBar(false, "身份证拍照", navController)
            }

            "preview" -> {
                TopBar(true, "相机预览", navController)
            }

            "picture" -> {
                TopBar(true, "身份证照片", navController)
            }
        }

        AnimatedNavHost(navController = navController, startDestination = "main") {
            composable("main", enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(100)
                )
            }, exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(100)
                )

            }) {
                ScreenMain({
                    navController.navigate("preview")
                }, {
                    act.finish()
                })
                index = "main"
            }
            composable("preview",/*enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            }, */exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(100)
                )

            }) {
                ScreenPreView({
                    navController.navigate("picture")
                }, {
                    navController.popBackStack()
                })
                index = "preview"

            }
            composable("picture", enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(100)
                )
            }, exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(100)
                )

            }) {
                ScreenPicture({
                    navController.navigate("preview")
                }, {
                    navController.navigate("main")
                })
                index = "picture"
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(enableBack: Boolean, title: String, navController: NavController) {

    TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
        containerColor = Purple40, titleContentColor = Color.White

    ), title = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,//设置水平居中对齐
            verticalAlignment = Alignment.CenterVertically//设置垂直居中对齐
        ) {
            Text(
                text = title, color = Color.White, style = MaterialTheme.typography.titleMedium
            )
        }
    }, navigationIcon = {
        if (enableBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "",
                modifier = Modifier.clickable(onClick = {
                    navController.navigate("main")
                }),
                tint = Color.White
            )
        }

    })

}









