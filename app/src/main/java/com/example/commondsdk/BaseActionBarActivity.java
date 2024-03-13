package com.example.commondsdk;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActionBarActivity extends AppCompatActivity {
    String TAG = Constant.TAG;
    private TextView title_tv;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_actionbar_title);
        title_tv = actionBar.getCustomView().findViewById(R.id.title_tv);
    }

    void setActionBarTitle(String title) {
        title_tv.setText(title);
        setTitle("");
    }

    void hideActionBar() {
        actionBar.hide();
    }

    void showNavigation(boolean show) {
        actionBar.setHomeButtonEnabled(show);
        actionBar.setDisplayHomeAsUpEnabled(show);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    void jump(Class act, Bundle params) {
        Intent intent = new Intent(this, act);
        intent.putExtras(params);
        startActivity(intent);
    }
}