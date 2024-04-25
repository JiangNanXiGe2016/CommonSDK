package com.example.commonsdk

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.commonsdk.screen.OrcScreen
import com.example.commonsdk.screen.ScreenMain
import com.example.commonsdk.screen.ScreenPicture
import com.example.commonsdk.screen.ScreenPreView
import com.example.commonsdk.screen.VideoScreen
import com.example.commonsdk.ui.theme.CommonSDKTheme
import com.example.commonsdk.ui.theme.Purple40
import com.google.accompanist.navigation.animation.AnimatedNavHost
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CommonSDKTheme {
                Surface(contentColor = Color.White) {
                    App()
                }
            }
        }

        methodRequiresTwoPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @AfterPermissionGranted(1)
    private fun methodRequiresTwoPermission() {
        val perms = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        if (EasyPermissions.hasPermissions(this, *perms)) {
        } else {
            EasyPermissions.requestPermissions(
                this, "vfvfvfvfd", 1, *perms
            )
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

                "video" -> {
                    TopBar(true, "相机录像", navController)
                }

                "ocrtext" -> {
                    TopBar(true, "ocr文字识别", navController)
                }

                "preview" -> {
                    TopBar(true, "相机预览", navController)
                }

                "picture" -> {
                    TopBar(true, "OCR demo", navController)
                }
            }

            AnimatedNavHost(navController = navController, startDestination = "main") {
                composable("main", enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(100)
                    )
                }, exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(100)
                    )

                }) {
                    ScreenMain(ocrClick = {
                        navController.navigate("ocrtext")
                    }, previewClick = {
                        navController.navigate("preview")

                    }, videoClick = {
                        navController.navigate("video")
                    }, quiteClick = {
                        act.finish()
                    })
                    index = "main"
                }
                composable("video", enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(100)
                    )
                }, exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(100)
                    )

                }) {
                    VideoScreen()
                    index = "video"
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
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(100)
                    )
                }, exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(100)
                    )

                }) {
                    ScreenPicture({
                        navController.navigate("preview")
                    }, {
                        navController.navigate("main")
                    })
                    index = "picture"
                }

                composable("ocrtext", enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(100)
                    )
                }, exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(100)
                    )

                }) {
                    OrcScreen()
                    index = "ocrtext"
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
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
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
}












