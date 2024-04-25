// Copyright (c) 2019 PaddlePaddle Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#include "Native.h"
#include "pipeline.h"
#include <android/log.h>
#include <opencv2/opencv.hpp>
#include <opencv2/core.hpp>
#include <android/bitmap.h>


#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_baidu_paddle_lite_demo_ocr_db_crnn_Native
 * Method:    nativeInit
 * Signature:
 * (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)J
 */
JNIEXPORT jlong
Java_com_example_ocrtext_Native_nativeInit(
        JNIEnv *env, jclass thiz, jstring jDetModelPath, jstring jClsModelPath,
        jstring jRecModelPath, jstring jConfigPath, jstring jLabelPath,
        jint cpuThreadNum, jstring jCPUPowerMode) {
    std::string detModelPath = jstring_to_cpp_string(env, jDetModelPath);
    std::string clsModelPath = jstring_to_cpp_string(env, jClsModelPath);
    std::string recModelPath = jstring_to_cpp_string(env, jRecModelPath);
    std::string configPath = jstring_to_cpp_string(env, jConfigPath);
    std::string labelPath = jstring_to_cpp_string(env, jLabelPath);
    std::string cpuPowerMode = jstring_to_cpp_string(env, jCPUPowerMode);

    return reinterpret_cast<jlong>(
            new Pipeline(detModelPath, clsModelPath, recModelPath, cpuPowerMode,
                         cpuThreadNum, configPath, labelPath));
}

/*
 * Class:     com_baidu_paddle_lite_demo_ocr_db_crnn_Native
 * Method:    nativeRelease
 * Signature: (J)Z
 */
JNIEXPORT jboolean
Java_com_example_ocrtext_Native_nativeRelease(JNIEnv *env,
                                                                 jclass thiz,
                                                                 jlong ctx) {
    if (ctx == 0) {
        return JNI_FALSE;
    }
    Pipeline *pipeline = reinterpret_cast<Pipeline *>(ctx);
    delete pipeline;
    return JNI_TRUE;
}

/*
 * Class:     com_baidu_paddle_lite_demo_ocr_db_crnn_Native
 * Method:    nativeProcess
 * Signature: (JIIIILjava/lang/String;)Z
 */
JNIEXPORT jboolean
Java_com_example_ocrtext_Native_nativeProcess(
        JNIEnv *env, jclass thiz, jlong ctx, jint inTextureId, jint outTextureId,
        jint textureWidth, jint textureHeight, jstring jsavedImagePath) {
    if (ctx == 0) {
        return JNI_FALSE;
    }
    std::string savedImagePath = jstring_to_cpp_string(env, jsavedImagePath);
    Pipeline *pipeline = reinterpret_cast<Pipeline *>(ctx);
    return pipeline->Process_val(inTextureId, outTextureId, textureWidth,
                                 textureHeight, savedImagePath);
}

#ifdef __cplusplus
}
#endif

extern "C"
JNIEXPORT jobject
Java_com_example_ocrtext_Native_ocr(JNIEnv *env, jclass clazz, jlong ctx,
                                                       jobject obj_bitmap) {
    if (ctx == 0) {
        return JNI_FALSE;
    }
    LOGD("Process_Single_Img===native ocr: %d", 1111111);

    void *bitmapPixels;                                            // Save picture pixel data
    AndroidBitmapInfo bitmapInfo;                                   // Save picture parameters
    cv::Mat matrix;
    bool getInfoRet = AndroidBitmap_getInfo(env, obj_bitmap, &bitmapInfo) >= 0;
    if (!getInfoRet) {
        LOGE("AndroidBitmap_getInfo() failed !");
    }
    bool typeRet = bitmapInfo.format == ANDROID_BITMAP_FORMAT_RGBA_8888
                   || bitmapInfo.format == ANDROID_BITMAP_FORMAT_RGB_565;
    if (!typeRet) {
        LOGE("bitmapInfo.format=%d", bitmapInfo.format);
    }
    bool lockRet = AndroidBitmap_lockPixels(env, obj_bitmap, &bitmapPixels) >= 0;
    if (!lockRet) {
        if (!bitmapPixels) {
            LOGE("AndroidBitmap_lockPixels() failed !");
        }
    }

    LOGD("Process_Single_Img===native ocr: %s", "no error ");
    if (bitmapInfo.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
        cv::Mat tmp(bitmapInfo.height, bitmapInfo.width, CV_8UC4,
                    bitmapPixels);    // Establish temporary mat
        tmp.copyTo(
                matrix);                                                         // Copy to target matrix
    } else {
        cv::Mat tmp(bitmapInfo.height, bitmapInfo.width, CV_8UC2, bitmapPixels);
        cv::cvtColor(tmp, matrix, cv::COLOR_BGR5652RGB);
    }

    //convert RGB to BGR
    cv::cvtColor(matrix, matrix, cv::COLOR_RGB2BGR);

    AndroidBitmap_unlockPixels(env, obj_bitmap);


    Pipeline *pipeline = reinterpret_cast<Pipeline *>(ctx);

    LOGD("Process_Single_Img===native ocr: %d", 22222222);
    std::vector<std::pair<std::string, float>> result = pipeline->Process_Single_Img(matrix);
    //将数据传回java
    jclass cls_ArrayList = env->FindClass("java/util/ArrayList");
    jmethodID construct = env->GetMethodID(cls_ArrayList, "<init>", "()V");
    jobject obj_ArrayList = env->NewObject(cls_ArrayList, construct);
    jmethodID arrayList_add = env->GetMethodID(cls_ArrayList, "add", "(Ljava/lang/Object;)Z");

    jclass cls_bean = env->FindClass("com/example/ocrtext/OcrResultBean");
    jmethodID constructBean = env->GetMethodID(cls_bean, "<init>", "(Ljava/lang/String;F)V");
    for (int i = 0; i < result.size(); i++) {
        std::pair<std::string, float> value = result.at(i);
        std::string text = value.first;
        float score = value.second;
        // 假设User有一个long类型的id字段
        jstring jText = env->NewStringUTF(text.c_str());
        jobject obj_bean = env->NewObject(cls_bean, constructBean, jText, score);
        env->CallBooleanMethod(obj_ArrayList, arrayList_add, obj_bean);
    }
    return obj_ArrayList;
}





