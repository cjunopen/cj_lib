package com.cj.lib_tools.util;

import android.graphics.Color;
import android.os.Build;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2024/12/26 上午 10:24:03
 */
public class ViewUtils {

    public interface ViewListener{
        void onView(View view);
    }

    /**
     * 遍历view
     * @param viewGroup
     * @param listener
     */
    public static void traverseViewGroup(ViewGroup viewGroup, ViewListener listener) {
        if (viewGroup == null) {
            return;
        }

        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = viewGroup.getChildAt(i);
            // 处理或记录每个遍历到的View节点
            if (listener != null) {
                listener.onView(child);
            }

            // 检查子View是否为ViewGroup，如果是，则递归调用遍历方法
            if (child instanceof ViewGroup) {
                traverseViewGroup((ViewGroup) child, listener);
            }
        }
    }

    /**
     * 有边界涟漪
     *
     * @param view
     */
    public static void setClickRippleAnim(View view) {
        view.setClickable(true);
        TypedValue tv = new TypedValue();
        //从这里可以明显看到是从theme中提取属性值的！
        view.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, tv, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setForeground(ContextCompat.getDrawable(view.getContext(), tv.resourceId));
        }
    }

    public static int getViewWidth(View view) {
        if (view == null) {
            return 0;
        }
        if (view.getLayoutParams() != null) {
            int w = view.getLayoutParams().width;
            if (w > 0) {
                return w;
            }
        }

        view.measure(0, 0);
        return view.getMeasuredWidth();
    }

    public static int getViewHeight(View view) {
        if (view == null) {
            return 0;
        }
        if (view.getLayoutParams() != null) {
            int h = view.getLayoutParams().height;
            if (h > 0) {
                return h;
            }
        }

        view.measure(0, 0);
        return view.getMeasuredHeight();
    }

    /**
     * 设置TextView中指定文本范围的颜色。
     *
     * @param textView 要设置文本颜色的TextView对象。如果为null，则函数直接返回，不执行任何操作。
     * @param color 要设置的文本颜色，使用颜色值（如Color.RED或0xFF0000）。
     * @param start 要设置颜色的文本范围的起始位置（包含）。
     * @param end 要设置颜色的文本范围的结束位置（不包含）。
     */
    public static void setTextColor(TextView textView, int color, int start, int end){
        if (textView == null) {
            return;
        }

        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        SpannableString content = new SpannableString(textView.getText());
        content.setSpan(colorSpan, start, end, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(content);
    }
}
