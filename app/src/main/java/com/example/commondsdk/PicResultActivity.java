package com.example.commondsdk;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.commondsdk.databinding.ActivityPicResultBinding;

public class PicResultActivity extends BaseActionBarActivity {
    ActivityPicResultBinding picResultBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        picResultBinding = ActivityPicResultBinding.inflate(getLayoutInflater());
        setContentView(picResultBinding.getRoot());
        setActionBarTitle("证件照结果页");
        showNavigation(true);
        setUpView();
    }

    private void setUpView() {
        RequestOptions requestOptions_front = new RequestOptions().placeholder(R.drawable.front_side).override(480, 300);
        RequestOptions requestOptions_back = new RequestOptions().placeholder(R.drawable.back_side).override(480, 300);
        Bundle bundle = getIntent().getBundleExtra(Constant.BUNDLE_PARAMS);
        if (bundle != null) {
            String frontSideUrl = bundle.getString(Constant.IMAGE__FRONT_URL);
            String backSideUrl = bundle.getString(Constant.IMAGE__BACK_URL);
            Log.i(TAG, "frontSideUrl=" + frontSideUrl+ "  backSideUrl="+backSideUrl);
            if(!TextUtils.isEmpty(frontSideUrl)){
                Glide.with(this).load(frontSideUrl).apply(requestOptions_front).into(picResultBinding.imgFront);
            }

            if(!TextUtils.isEmpty(backSideUrl)){
                Glide.with(this).load(backSideUrl).apply(requestOptions_front).into(picResultBinding.imgBack);

            }
        }

        picResultBinding.usePic.setOnClickListener((v) -> {
            //保存图片到相册
            toAlblums();

        });
        picResultBinding.takePicAgain.setOnClickListener((v) -> {
            // 被选择的一个图片重新拍摄
            takeImageAgain();
        });


    }

    private void takeImageAgain() {

    }

    private void toAlblums(){


    }
}