package com.example.ocr;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.icu.math.BigDecimal;
import android.media.Image;
import android.media.ImageReader;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public class OrcCameraView extends TextureView {
    private static final String TAG = "OrcCameraView";
    private Context mContext; // 声明一个上下文对象
    private Handler mHandler;
    private HandlerThread mThreadHandler;
    private CaptureRequest.Builder mPreviewBuilder; // 声明一个拍照请求构建器对象
    private CameraCaptureSession mCameraSession; // 声明一个相机拍照会话对象
    private CameraDevice mCameraDevice; // 声明一个相机设备对象
    private ImageReader mImageReader; // 声明一个图像读取器对象
    private Size mPreViewSize; // 预览画面的尺寸
    private int mCameraType = CameraCharacteristics.LENS_FACING_FRONT; // 摄像头类型
    private int mTakeType = 0; // 拍摄类型。0为单拍，1为连拍

    public OrcCameraView(Context context) {
        this(context, null);
    }

    public OrcCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mThreadHandler = new HandlerThread("camera2");
        mThreadHandler.start();
        mHandler = new Handler(mThreadHandler.getLooper());
    }

    // 打开指定摄像头的相机视图
    public void open(int camera_type) {
        mCameraType = camera_type;
        // 设置表面纹理变更监听器
        setSurfaceTextureListener(mSurfacetextlistener);
    }

    public void addFrameListener(OnFrameListener listener) {
        this.onFrameListener = listener;
    }

    // 执行拍照动作
    public void takePicture(PictureTakeCallBack callBack) {
        Log.d(TAG, "正在拍照");
        this.callBack = callBack;
        mTakeType = 0;
        try {
            CaptureRequest.Builder builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 把图像读取器添加到预览目标
            builder.addTarget(mImageReader.getSurface());
            // 设置自动对焦模式
            builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
            // 设置自动曝光模式
            builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            // 开始对焦
            builder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            // 设置照片的方向
//            builder.set(CaptureRequest.JPEG_ORIENTATION, (mCameraType == CameraCharacteristics.LENS_FACING_FRONT) ? 90 : 270);
            builder.set(CaptureRequest.JPEG_ORIENTATION, 0);


            // 拍照会话开始捕获相片
            mCameraSession.capture(builder.build(), null, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> mShootingArray; // 连拍的相片保存路径列表

    // 获取连拍的相片保存路径列表
    public ArrayList<String> getShootingList() {
        Log.d(TAG, "mShootingArray.size()=" + mShootingArray.size());
        return mShootingArray;
    }

    // 开始连拍
    public void startShooting(int duration) {
        Log.d(TAG, "正在连拍");
        mTakeType = 1;
        mShootingArray = new ArrayList<String>();
        try {
            // 停止连拍
            mCameraSession.stopRepeating();
            // 把图像读取器添加到预览目标
            mPreviewBuilder.addTarget(mImageReader.getSurface());
            // 设置连拍请求。此时预览画面会同时发给手机屏幕和图像读取器
            mCameraSession.setRepeatingRequest(mPreviewBuilder.build(), null, mHandler);
            // duration小等于0时，表示持续连拍，此时外部要调用stopShooting方法来结束连拍
            if (duration > 0) {
                // 延迟若干秒后启动拍摄停止任务
                mHandler.postDelayed(mStop, duration);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // 停止连拍
    public void stopShooting() {
        try {
            // 停止连拍
            mCameraSession.stopRepeating();
            // 移除图像读取器的预览目标
            mPreviewBuilder.removeTarget(mImageReader.getSurface());
            // 设置连拍请求。此时预览画面只会发给手机屏幕
            mCameraSession.setRepeatingRequest(mPreviewBuilder.build(), null, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // 定义一个拍摄停止任务
    private Runnable mStop = new Runnable() {
        @Override
        public void run() {
            stopShooting();
        }
    };

    Rect rect;

    // 打开相机
    private void openCamera() {
        // 从系统服务中获取相机管理器
        CameraManager cm = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        String cameraid = mCameraType + "";
        try {
            // 获取可用相机设备列表
            CameraCharacteristics cc = cm.getCameraCharacteristics(cameraid);
            // 检查相机硬件的支持级别
            // CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL表示完全支持
            // CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED表示有限支持
            // CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY表示遗留的
            int level = cc.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
            if (level == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL) {
                Log.i(TAG, "完全支持");
            } else if (level == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED) {
                Log.i(TAG, "有限支持");
            } else if (level == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
                Log.i(TAG, "不建议使用");
            }
            StreamConfigurationMap map = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] sizes = map.getOutputSizes(ImageFormat.JPEG);

            Log.i("yangliang", "sizes=" + Arrays.toString(sizes));

            Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizeByArea());
            Log.i("yangliang", "largest =" + largest.toString());
            Log.i("yangliang", "largest  w=" + largest.getWidth());
            Log.i("yangliang", "largest  h=" + largest.getHeight());


            float originalW = ScreenUtil.getScreenWidthPixels(getContext());
            Log.i("yangliang", "originalW=" + originalW);
            float originalH = ScreenUtil.getScreenHeightPixels(getContext());
            Log.i("yangliang", "originalH=" + originalH);
            // 获取预览画面的尺寸
            mPreViewSize = map.getOutputSizes(SurfaceTexture.class)[0];
            BigDecimal decimal = new BigDecimal(mPreViewSize.getWidth()).multiply(new BigDecimal(1.5));
//            //按照比例对预览帧裁剪
            //  rect = new Rect(0, 0, 2500, 4700);

            // 创建一个JPEG格式的图像读取器
            mImageReader = ImageReader.newInstance(mPreViewSize.getWidth(), mPreViewSize.getHeight(), ImageFormat.JPEG, 10);

            // mImageReader = ImageReader.newInstance(640, 480, ImageFormat.YUV_420_888, 10);


            Log.i("yangliang", "mPreViewSize  w h=" + mPreViewSize.getWidth() + " " + mPreViewSize.getHeight());
            // 设置图像读取器的图像可用监听器，一旦捕捉到图像数据就会触发监听器的onImageAvailable方法
            mImageReader.setOnImageAvailableListener(onImageAvaiableListener, null);
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // 开启摄像头
                cm.openCamera(cameraid, mDeviceStateCallback, mHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // 关闭相机
    public void closeCamera() {
        if (null != mCameraSession) {
            mCameraSession.close(); // 关闭相机拍摄会话
            mCameraSession = null;
        }
        if (null != mCameraDevice) {
            mCameraDevice.close(); // 关闭相机设备
            mCameraDevice = null;
        }
        if (null != mImageReader) {
            mImageReader.close(); // 关闭图像读取器
            mImageReader = null;
        }
    }

    // 定义一个表面纹理变更监听器。TextureView准备就绪后，立即开启相机
    private SurfaceTextureListener mSurfacetextlistener = new SurfaceTextureListener() {
        // 在纹理表面可用时触发
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera(); // 打开相机
        }

        // 在纹理表面的尺寸发生改变时触发
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        // 在纹理表面销毁时触发
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            closeCamera(); // 关闭相机
            return true;
        }

        // 在纹理表面更新时触发
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    // 创建相机预览会话
    private void createCameraPreviewSession() {
        // 获取纹理视图的表面纹理
        SurfaceTexture texture = getSurfaceTexture();
        // 设置表面纹理的默认缓存尺寸
        assert texture != null;
        texture.setDefaultBufferSize(mPreViewSize.getWidth(), mPreViewSize.getHeight());
        // 创建一个该表面纹理的表面对象
        Surface surface = new Surface(texture);
        try {
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 把纹理视图添加到预览目标
            mPreviewBuilder.addTarget(surface);

            mPreviewBuilder.addTarget(mImageReader.getSurface());
            // 设置自动对焦模式
            mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // 设置自动曝光模式
            // mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            // 开始对焦
            mPreviewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            // 设置照片的方向
            mPreviewBuilder.set(CaptureRequest.JPEG_ORIENTATION, (mCameraType == CameraCharacteristics.LENS_FACING_FRONT) ? 90 : 270);

            //设置裁剪
            // mPreviewBuilder.set(CaptureRequest.SCALER_CROP_REGION, rect);
            // 创建一个相片捕获会话。此时预览画面显示在纹理视图上
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()), mSessionStateCallback, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // 相机准备就绪后，开启捕捉影像的会话
    private CameraDevice.StateCallback mDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            cameraDevice.close();
            mCameraDevice = null;
        }
    };
    // 影像配置就绪后，将预览画面呈现到手机屏幕上
    private CameraCaptureSession.StateCallback mSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            try {
                Log.d(TAG, "onConfigured");
                mCameraSession = session;
                // 设置连拍请求。此时预览画面只会发给手机屏幕
                mCameraSession.setRepeatingRequest(mPreviewBuilder.build(), null, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
        }
    };

    // 一旦有图像数据生成，立刻触发onImageAvailable事件
    private ImageReader.OnImageAvailableListener onImageAvaiableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader imageReader) {
            Log.d(TAG, "onImageAvailable");
            if (callBack != null && mCameraDevice != null) {
                callBack.onTakePic(imageReader);
            }
            // onFrame, data  dependency  ImageReader.Format
            if (onFrameListener != null && mCameraDevice != null&&onFrameEnable) {
                onFrameListener.onFrame(imageReader);
            }
        }
    };


    PictureTakeCallBack callBack;
    OnFrameListener onFrameListener;
    boolean onFrameEnable=false;

    // 控制视频帧
    public void enableOnFrame(boolean enable) {
        onFrameEnable=enable;
    }

    public interface OnFrameListener {
        void onFrame(ImageReader imageReader);
    }

    public interface PictureTakeCallBack {
        void onTakePic(ImageReader imageReader);
    }


    private static class CompareSizeByArea implements java.util.Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }
    }

}
