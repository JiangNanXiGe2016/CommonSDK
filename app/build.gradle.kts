plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.baselineprofile)
}
apply(from = "../jacoco/jacoco.gradle")
android {
    namespace = "com.example.commonsdk"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.commonsdk"
        minSdk = 24
        targetSdk = 28
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildToolsVersion = "34.0.0"
    ndkVersion = "21.4.7075529"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(files("libs/accompanist-navigation-animation-0.34.0.aar"))
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    var cameraxVersion = "1.2.0-beta01"
    implementation("androidx.navigation:navigation-compose:2.7.1")
    implementation("androidx.camera:camera-camera2:1.1.0-alpha06")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-video:$cameraxVersion")

//    implementation "androidx.camera:camera-lifecycle:$cameraxVersion"
//    implementation "androidx.camera:camera-video:$cameraxVersion"
//    implementation "androidx.camera:camera-view:$cameraxVersion"
//    implementation "androidx.camera:camera-extensions:$cameraxVersion"
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("pub.devrel:easypermissions:3.0.0")
    implementation (project(":ocrtext"))
    implementation ("com.android.support.constraint:constraint-layout:1.1.3")
    implementation ("com.android.support:appcompat-v7:28.0.0")
// https://mvnrepository.com/artifact/org.jacoco/org.jacoco.agent
    testImplementation ("org.jacoco:org.jacoco.agent:0.8.12")

}

