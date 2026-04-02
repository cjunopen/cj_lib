package com.cj.lib_tools.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.Arrays;

import timber.log.Timber;

/**
 * @Description: 权限工具
 * @Author: CJ
 * @CreateDate: 2024/10/29 10:01
 */
public class PermissionUtils {

    /**
     * 检查读写权限
     */
    public static void checkStoragePermissions(Activity activity, OnPermissionCallback callback){
        checkPermissions(activity,
                new String[]{Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE},
                callback);
    }

    /**
     * 检查权限
     */
    public static void checkPermissions(Activity activity, String[] permissions, OnPermissionCallback callback) {
        if (XXPermissions.isGranted(activity, permissions)) {
            if (callback != null) {
                callback.onGranted(Arrays.asList(permissions), true);
            }
        } else {
            XXPermissions.with(activity).permission(permissions).request(callback);
        }
    }

    /**
     * 检查悬浮窗权限
     * @param activity
     */
    public static void checkAlertDialogPermissions(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity)) {
                ToastUtils.showLong("请赋予悬浮窗权限");
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + AppUtils.getAppPackageName()));
                activity.startActivityForResult(intent, 111);
            }else {
                Timber.i("checkAlertDialogPermissions true");
            }
        }
    }
}
