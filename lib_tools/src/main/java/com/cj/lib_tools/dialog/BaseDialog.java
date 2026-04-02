package com.cj.lib_tools.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cj.lib_tools.util.MyLogUtils;

import timber.log.Timber;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2024/10/30 15:47
 */
public class BaseDialog extends Dialog {
    public BaseDialog(@NonNull Context context) {
        super(context);
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void show() {
        super.show();
        Timber.tag(MyLogUtils.getTag(this)).i("show");
    }

    @Override
    public void dismiss() {
        super.dismiss();
        Timber.tag(MyLogUtils.getTag(this)).i("dismiss()");
    }
}
