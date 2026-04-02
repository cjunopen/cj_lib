package com.cj.lib_tools.service;

import android.app.Service;

import com.cj.lib_tools.util.MyLogUtils;

import timber.log.Timber;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2024/12/16 10:12
 */
public abstract class BaseService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.tag(MyLogUtils.getTag(this)).i("onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.tag(MyLogUtils.getTag(this)).i("onDestroy");
    }
}
