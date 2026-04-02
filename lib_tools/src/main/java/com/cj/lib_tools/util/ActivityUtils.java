package com.cj.lib_tools.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.blankj.utilcode.util.Utils;

/**
 * @Description: activity 相关工具类
 * @Author:      CJ
 * @CreateDate:  2023/5/17 14:41
 */
public class ActivityUtils {

    /**
     * @return 当前屏幕上的activity的名字
     */
    public static String getCurrentActivityName(){
        ActivityManager activityManager = (ActivityManager) Utils.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager.getRunningTasks(1).size() > 0) {
            ComponentName topActivity = activityManager.getRunningTasks(1).get(0).topActivity;
            return topActivity.getClassName();
        }
        return null;
    }


}
