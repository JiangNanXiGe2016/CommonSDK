package com.example.commonsdk;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ocrtext.Native;
import com.example.ocrtext.OcrResultBean;
import com.example.ocrtext.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;


public class OcrActivity extends AppCompatActivity {
    TextView retTextView;
    ImageView retImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        Button requestBtn = findViewById(R.id.request_permission);
        Button initBtn = findViewById(R.id.init_ocr);
        Button releaseBtn = findViewById(R.id.release_ocr);
        Button button = findViewById(R.id.select_img);
        retImageView = findViewById(R.id.orc_image);
        retTextView = findViewById(R.id.ocr_result);

        releaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releaseOcr();
            }
        });
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
            }
        });
        initBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initOcr();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    private void releaseOcr() {
       // predictor.release();
    }


    int lastFrameIndex = 0;
    long lastFrameTime;

    // Model settings of object detection
    protected String detModelPath = "ch_ppocr_mobile_v2.0_det_slim_opt.nb";
    protected String recModelPath = "ch_ppocr_mobile_v2.0_rec_slim_opt.nb";
    protected String clsModelPath = "ch_ppocr_mobile_v2.0_cls_slim_opt.nb";
    protected String labelPath = "ppocr_keys_v1.txt";
    protected String configPath = "config.txt";
    protected int cpuThreadNum = 1;
    protected String cpuPowerMode = "LITE_POWER_HIGH";
    Native predictor = new Native();

    private void initOcr() {
        try {
            Utils.copyAssets(this, labelPath);
            String labelRealDir = new File(this.getExternalFilesDir(null), labelPath).getAbsolutePath();

            Utils.copyAssets(this, configPath);
            String configRealDir = new File(this.getExternalFilesDir(null), configPath).getAbsolutePath();

            Utils.copyAssets(this, detModelPath);
            String detRealModelDir = new File(this.getExternalFilesDir(null), detModelPath).getAbsolutePath();

            Utils.copyAssets(this, clsModelPath);
            String clsRealModelDir = new File(this.getExternalFilesDir(null), clsModelPath).getAbsolutePath();

            Utils.copyAssets(this, recModelPath);
            String recRealModelDir = new File(this.getExternalFilesDir(null), recModelPath).getAbsolutePath();

           // predictor.init(this, detRealModelDir, clsRealModelDir, recRealModelDir, configRealDir, labelRealDir, cpuThreadNum, cpuPowerMode);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private void requestPermission() {
        String[] ps = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasPermission(getApplicationContext(), ps)) {
            ActivityCompat.requestPermissions(this, ps, 200);
        }

    }

    private boolean hasPermission(Context context, String... ps) {
        for (String permission : ps) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Log.i("yll", "selectedImage: " + selectedImage);
            ContentResolver contentResolver = getContentResolver();
            InputStream inputStream = null;
            Bitmap bitmap = null;
            try {
                inputStream = contentResolver.openInputStream(selectedImage);

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            Log.i("yll", "bitmap: " + bitmap);
            final Bitmap finalBitmap = bitmap;
            byte[] img = Utils.bitmapToByte(finalBitmap);
            Log.i("yll", "bitmap: img=" + Arrays.toString(img));
            int width = finalBitmap.getWidth();
            int height = finalBitmap.getHeight();
            Log.i("yll", "bitmap: width=" + width + " height=" + height);
            ArrayList<OcrResultBean> resultBeans = predictor.recognise(finalBitmap);
           // ArrayList<OcrResultBean> resultBeans=new ArrayList<>();
            final StringBuilder stringBuilder = new StringBuilder();
            for (int  i=0;i<resultBeans.size();i++){
                OcrResultBean bean=resultBeans.get(i);
                String  line="index="+i+ " " +bean.text+" "+bean.score;
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    retImageView.setImageBitmap(finalBitmap);
                    retTextView.setText(stringBuilder.toString());


                }
            });
        } else if (requestCode == 200) {
            Log.i("yll", "request permission : ");
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

}
