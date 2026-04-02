package com.cj.lib_tools.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cj.lib_tools.util.MyLogUtils;

import timber.log.Timber;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2024/10/30 9:40
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(MyLogUtils.getTag(this)).i("onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.tag(MyLogUtils.getTag(this)).i("onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.tag(MyLogUtils.getTag(this)).i("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Timber.tag(MyLogUtils.getTag(this)).i("onPause");
    }
}
