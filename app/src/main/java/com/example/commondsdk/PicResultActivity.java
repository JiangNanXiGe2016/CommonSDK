package com.example.commondsdk;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
        RequestOptions requestOptions_front = new RequestOptions().placeholder(R.drawable.idcard_front).override(100, 100);
        RequestOptions requestOptions_back = new RequestOptions().placeholder(R.drawable.idcard_back).override(100, 100);
        Bundle bundle = getIntent().getBundleExtra(Constant.BUNDLE_PARAMS);
        String url = bundle.getString(Constant.IMAGE_URL);
        Log.i(TAG, "url=" + url);
        Glide.with(this).load(url).apply(requestOptions_front).into(picResultBinding.imgFront);

    }
}