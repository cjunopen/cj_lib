package com.cj.lib_tools.dialog;

import android.os.Build;
import android.view.WindowManager;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2025/3/11 上午 9:36:52
 */
public class DialogUtils {
    public static WindowManager.LayoutParams setAlertType(WindowManager.LayoutParams layoutParams){
        if (Build.VERSION.SDK_INT >= 23) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        return layoutParams;
    }
}
